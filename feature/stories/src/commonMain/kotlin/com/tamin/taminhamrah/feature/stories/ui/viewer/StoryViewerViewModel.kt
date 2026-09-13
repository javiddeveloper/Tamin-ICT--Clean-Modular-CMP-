package com.tamin.taminhamrah.feature.stories.ui.viewer

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.stories.ui.mapper.toPresentation
import com.tamin.taminhamrah.feature.stories.ui.model.StoryChannelPR
import com.tamin.taminhamrah.feature.stories.ui.model.StoryMediaPR
import com.tamin.taminhamrah.feature.stories.ui.theme.STORY_DEFAULT_DURATION_MS
import com.tamin.taminhamrah.feature.stories.ui.theme.STORY_TICK_MS
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerEvent
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerIntent
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerUiState
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerUiState.PartialState
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.stories.GetStoryChannelsUseCase
import com.tamin.taminhamrah.useCases.stories.MarkStoryChannelSeenUseCase
import com.tamin.taminhamrah.useCases.stories.ObserveStoryEngagementUseCase
import com.tamin.taminhamrah.useCases.stories.ToggleStoryLikeUseCase
import com.tamin.taminhamrah.useCases.stories.ToggleStorySaveUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * How long a clip is given to say how long it is before the viewer stops waiting for it.
 *
 * Without this a player that neither succeeds nor reports a failure — an unreachable host, a file
 * that is not really a video — would leave the reader on a buffering slide with a bar that never
 * moves and no way out but the close button.
 */
private const val MEDIA_READY_TIMEOUT_MS = 8_000L

/**
 * The full-screen story viewer.
 *
 * ### The segment clock
 *
 * Exactly one clock runs at a time, in [segmentJob]. It accumulates in [STORY_TICK_MS] steps
 * rather than sleeping out the whole duration in one go, which is what lets a pause say how far
 * the slide already got — no wall clock is consulted, so the whole thing runs on virtual time
 * under test. Nothing observes a tick: [elapsedMs] is a plain field, published into the state only
 * when the reader actually pauses.
 *
 * Every path that changes the slide cancels the clock before starting the next one, so a story
 * tapped through quickly cannot leave a trail of timers behind. A tick that was already in flight
 * when that happened is discarded by the [segmentSerial] check rather than advancing the story
 * twice.
 *
 * [watchdogJob] is the only other timer, and it is mutually exclusive with the clock: it runs
 * exactly while a clip is buffering, which is exactly when the clock is not.
 *
 * ### Video
 *
 * A clip's segment has no duration until the player reports one. The slide comes up buffering,
 * the bar holds at zero, and [StoryViewerIntent.MediaReady] both sets the real duration and
 * starts the clock. Failure — reported, or timed out — falls back to the image duration so the
 * story always moves on.
 */
class StoryViewerViewModel(
    private val getStoryChannelsUseCase: GetStoryChannelsUseCase,
    private val markStoryChannelSeenUseCase: MarkStoryChannelSeenUseCase,
    private val observeStoryEngagementUseCase: ObserveStoryEngagementUseCase,
    private val toggleStoryLikeUseCase: ToggleStoryLikeUseCase,
    private val toggleStorySaveUseCase: ToggleStorySaveUseCase,
) : BaseViewModel<StoryViewerUiState, PartialState, StoryViewerEvent, StoryViewerIntent>(
    initialState = StoryViewerUiState(),
) {
    private var segmentJob: Job? = null
    private var watchdogJob: Job? = null

    /** How much of the current segment has run. The clock's own bookkeeping. */
    private var elapsedMs = 0L

    /** Identifies the current clock run, so a stale tick can be told apart from a live one. */
    private var segmentSerial = 0

    private var opened = false

    override fun handleIntent(intent: StoryViewerIntent): Flow<PartialState> = flow {
        when (intent) {
            is StoryViewerIntent.Open -> open(intent.channelIndex)
            StoryViewerIntent.Next -> goNext()
            StoryViewerIntent.Previous -> goPrevious()
            StoryViewerIntent.Pause -> setHold(touchHeld = true)
            is StoryViewerIntent.Resume -> {
                if (intent.channelIndex == null || intent.channelIndex == uiState.value.channelIndex) {
                    setHold(touchHeld = false)
                }
            }
            is StoryViewerIntent.JumpToChannel -> jumpToChannel(intent.index)
            is StoryViewerIntent.CommentFocusChanged -> setHold(composingComment = intent.focused)
            is StoryViewerIntent.CommentChanged ->
                emit(PartialState.CommentDraftChanged(intent.draft))

            StoryViewerIntent.CommentSubmitted -> {
                emit(PartialState.CommentDraftChanged(""))
                setHold(composingComment = false)
            }
            is StoryViewerIntent.AutoAdvance -> if (intent.serial == segmentSerial) goNext()
            is StoryViewerIntent.MediaReady -> mediaReady(intent.durationMs)
            StoryViewerIntent.MediaEnded -> if (currentIsVideo()) goNext()
            StoryViewerIntent.MediaFailed -> mediaFailed()
            StoryViewerIntent.ToggleLike -> {
                val item = uiState.value.item ?: return@flow
                val wasLiked = uiState.value.isLiked
                toggleStoryLikeUseCase(item.id)
                if (!wasLiked) {
                    sendEvent(StoryViewerEvent.ShowLikeAnimation)
                }
            }

            StoryViewerIntent.ToggleSave ->
                uiState.value.item?.let { toggleStorySaveUseCase(it.id) }
            StoryViewerIntent.CtaClicked -> ctaClicked()
            StoryViewerIntent.Close -> close()
        }
    }

    /* ---- Opening ----------------------------------------------------------------------- */

    private suspend fun FlowCollector<PartialState>.open(channelIndex: Int) {
        // The screen sends this from a LaunchedEffect, which a configuration change re-runs; the
        // story should carry on from where it was rather than start over.
        if (opened) return
        opened = true

        emit(PartialState.Loading(true))
        val channels = try {
            // `first()` and not `collect`, deliberately: this flow is a cached fetch with a single
            // source, not a cache-then-network pair, so there is no fresher second emission to
            // miss. In practice the rail that opened this viewer already loaded it, and the
            // repository's guard means no second fetch happens here at all.
            getStoryChannelsUseCase().first().toPresentation()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            emit(PartialState.Error(e.toSingleLineMessage()))
            return
        }

        if (channels.isEmpty()) {
            emit(PartialState.Loading(false))
            sendEvent(StoryViewerEvent.Close)
            return
        }

        val index = channelIndex.coerceIn(0, channels.lastIndex)
        emit(PartialState.Loaded(channels, index))
        startSegment(channels, index, itemIndex = 0)

        // Never completes, which is the point: a like made in the viewer has to reach the state
        // that draws the heart, and this is the only thing watching for it.
        emitAll(
            observeStoryEngagementUseCase().map { engagement ->
                PartialState.Engagement(
                    likedItems = engagement.likedItemIds.toImmutableSet(),
                    savedItems = engagement.savedItemIds.toImmutableSet(),
                )
            },
        )
    }

    /* ---- Moving between slides ---------------------------------------------------------- */

    private suspend fun FlowCollector<PartialState>.goNext() {
        val state = uiState.value
        val channel = state.channel ?: return

        val nextItem = state.itemIndex + 1
        if (nextItem <= channel.items.lastIndex) {
            startSegment(state.channels, state.channelIndex, nextItem)
            return
        }

        // The channel is finished, which is what marks it watched — the ring behind us on the
        // home page goes grey from here.
        markStoryChannelSeenUseCase(channel.key)

        val nextChannel = state.channelIndex + 1
        if (nextChannel <= state.channels.lastIndex) {
            startSegment(state.channels, nextChannel, itemIndex = 0)
        } else {
            stopClock()
            stopWatchdog()
            sendEvent(StoryViewerEvent.Close)
        }
    }

    private suspend fun FlowCollector<PartialState>.goPrevious() {
        val state = uiState.value
        if (state.channel == null) return

        val previousItem = state.itemIndex - 1
        if (previousItem >= 0) {
            startSegment(state.channels, state.channelIndex, previousItem)
            return
        }

        val previousChannel = state.channelIndex - 1
        if (previousChannel < 0) {
            // Nowhere further back to go. Replaying the slide is the answer to what the reader
            // asked for — they wanted to see it again — and it beats a tap that does nothing.
            startSegment(state.channels, state.channelIndex, itemIndex = 0)
            return
        }

        val target = state.channels[previousChannel]
        startSegment(state.channels, previousChannel, target.items.lastIndex)
    }

    private suspend fun FlowCollector<PartialState>.jumpToChannel(channelIndex: Int) {
        val state = uiState.value
        val validIndex = channelIndex.coerceIn(0, state.channels.lastIndex)
        if (state.channelIndex == validIndex) return
        
        // Mark the current one as seen before leaving it
        state.channel?.let { markStoryChannelSeenUseCase(it.key) }
        
        startSegment(state.channels, validIndex, itemIndex = 0)
    }

    /**
     * Brings a slide up: publishes it, then either starts its clock or, for a clip, waits for the
     * player to say how long it is.
     */
    private suspend fun FlowCollector<PartialState>.startSegment(
        channels: ImmutableList<StoryChannelPR>,
        channelIndex: Int,
        itemIndex: Int,
    ) {
        val item = channels.getOrNull(channelIndex)?.items?.getOrNull(itemIndex) ?: return
        val isVideo = item.media is StoryMediaPR.Video

        stopClock()
        stopWatchdog()

        emit(
            PartialState.SegmentStarted(
                channelIndex = channelIndex,
                itemIndex = itemIndex,
                durationMs = STORY_DEFAULT_DURATION_MS,
                isBuffering = isVideo,
            ),
        )

        if (isVideo) startWatchdog() else startClock(STORY_DEFAULT_DURATION_MS)
    }

    /* ---- Holding the story --------------------------------------------------------------- */

    /**
     * The one place either reason for holding a story is applied.
     *
     * Two independent reasons — a finger on the screen, and the comment keyboard — share one
     * clock, so neither may start or stop it on its own: the clock stops when the first hold
     * arrives and starts again only when the last one leaves. A finger lifting while the keyboard
     * is still up therefore leaves the story where it is, which is the whole point of tracking
     * them apart.
     *
     * Each parameter defaults to what it already is, so a caller states only the reason it owns.
     */
    private suspend fun FlowCollector<PartialState>.setHold(
        touchHeld: Boolean = uiState.value.isTouchHeld,
        composingComment: Boolean = uiState.value.isComposingComment,
    ) {
        val state = uiState.value
        if (touchHeld == state.isTouchHeld && composingComment == state.isComposingComment) return

        val wasHeld = state.isPaused
        val isHeld = touchHeld || composingComment

        // The watchdog is left running on purpose: a finger held on a buffering slide should not
        // buy a stalled clip unlimited time to answer.
        val elapsedMsSoFar = if (isHeld && !wasHeld) stopClock() else state.segmentElapsedMs
        emit(PartialState.Held(touchHeld, composingComment, elapsedMsSoFar))

        if (wasHeld && !isHeld && !state.isBuffering) {
            // Read off this class rather than off the state just emitted, which has not been
            // reduced yet — and which is the same number anyway.
            startClock(state.segmentDurationMs, fromMs = elapsedMs)
        }
    }

    /* ---- Media -------------------------------------------------------------------------- */

    private suspend fun FlowCollector<PartialState>.mediaReady(durationMs: Long) {
        if (!currentIsVideo()) return
        stopWatchdog()
        // A player that reports nothing useful still gets a slide of the ordinary length.
        val duration = durationMs.takeIf { it > 0L } ?: STORY_DEFAULT_DURATION_MS
        emit(PartialState.DurationResolved(duration))
        if (!uiState.value.isPaused) startClock(duration)
    }

    private suspend fun FlowCollector<PartialState>.mediaFailed() {
        val state = uiState.value
        if (state.item == null || state.mediaFailed) return
        stopWatchdog()
        emit(PartialState.MediaFailed(STORY_DEFAULT_DURATION_MS))
        if (!state.isPaused) startClock(STORY_DEFAULT_DURATION_MS)
    }

    private fun currentIsVideo(): Boolean = uiState.value.item?.media is StoryMediaPR.Video

    /* ---- Leaving ------------------------------------------------------------------------ */

    private suspend fun ctaClicked() {
        val state = uiState.value
        val link = state.item?.cta?.deepLink ?: return
        stopClock()
        stopWatchdog()
        state.channel?.let { markStoryChannelSeenUseCase(it.key) }
        sendEvent(StoryViewerEvent.OpenDeepLink(link))
    }

    private suspend fun close() {
        stopClock()
        stopWatchdog()
        uiState.value.channel?.let { markStoryChannelSeenUseCase(it.key) }
        sendEvent(StoryViewerEvent.Close)
    }

    /* ---- Timers ------------------------------------------------------------------------- */

    private fun startClock(durationMs: Long, fromMs: Long = 0L) {
        segmentJob?.cancel()
        elapsedMs = fromMs
        val serial = ++segmentSerial
        segmentJob = viewModelScope.launch {
            var elapsed = fromMs
            while (elapsed < durationMs) {
                delay(STORY_TICK_MS)
                elapsed += STORY_TICK_MS
                elapsedMs = elapsed
            }
            sendIntent(StoryViewerIntent.AutoAdvance(serial))
        }
    }

    /** Stops the clock and reports how far the slide got, for the bar to resume from. */
    private fun stopClock(): Long {
        segmentJob?.cancel()
        segmentJob = null
        return elapsedMs
    }

    private fun startWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = viewModelScope.launch {
            delay(MEDIA_READY_TIMEOUT_MS)
            sendIntent(StoryViewerIntent.MediaFailed)
        }
    }

    private fun stopWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = null
    }

    /* ---- Reduce ------------------------------------------------------------------------- */

    override fun reduceState(
        currentState: StoryViewerUiState,
        partialState: PartialState,
    ): StoryViewerUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = if (partialState.isLoading) null else currentState.error,
        )

        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            channels = partialState.channels,
            channelIndex = partialState.channelIndex,
            itemIndex = 0,
        )

        is PartialState.SegmentStarted -> currentState.copy(
            channelIndex = partialState.channelIndex,
            itemIndex = partialState.itemIndex,
            segmentDurationMs = partialState.durationMs,
            segmentElapsedMs = 0L,
            segmentToken = currentState.segmentToken + 1,
            isBuffering = partialState.isBuffering,
            isTouchHeld = false,
            // A draft belongs to the slide it was written under, and a story cannot advance while
            // the keyboard is up anyway — so arriving on a new slide starts from an empty field.
            isComposingComment = false,
            commentDraft = "",
            mediaFailed = false,
        )

        is PartialState.DurationResolved -> currentState.copy(
            segmentDurationMs = partialState.durationMs,
            segmentElapsedMs = 0L,
            segmentToken = currentState.segmentToken + 1,
            isBuffering = false,
        )

        is PartialState.MediaFailed -> currentState.copy(
            segmentDurationMs = partialState.durationMs,
            segmentElapsedMs = 0L,
            segmentToken = currentState.segmentToken + 1,
            isBuffering = false,
            mediaFailed = true,
        )

        // No token bump: the bar's animation is keyed on isPlaying as well, so a hold arriving or
        // leaving already restarts it — from segmentElapsedMs, which is published right here.
        is PartialState.Held -> currentState.copy(
            isTouchHeld = partialState.touchHeld,
            isComposingComment = partialState.composingComment,
            segmentElapsedMs = partialState.elapsedMs,
        )

        is PartialState.CommentDraftChanged -> currentState.copy(commentDraft = partialState.draft)

        is PartialState.Engagement -> currentState.copy(
            likedItems = partialState.likedItems,
            savedItems = partialState.savedItems,
        )

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
