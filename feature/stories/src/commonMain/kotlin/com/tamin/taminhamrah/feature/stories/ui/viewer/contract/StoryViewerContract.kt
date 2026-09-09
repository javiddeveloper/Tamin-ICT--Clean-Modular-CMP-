package com.tamin.taminhamrah.feature.stories.ui.viewer.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.stories.model.StoryChannel
import com.tamin.taminhamrah.feature.stories.model.StoryItem
import com.tamin.taminhamrah.feature.stories.ui.theme.STORY_DEFAULT_DURATION_MS
import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

/**
 * The full-screen viewer.
 *
 * ### Why the progress fill is not in here
 *
 * There is no `progress: Float`. A bar that filled from state would push a new state through the
 * whole MVI pipeline on every frame it moves, and recompose the screen with it. Instead the state
 * describes the *segment* — how long it runs, how much of it was already spent, and whether it is
 * running at all — and the bar animates itself from that. [segmentToken] is the signal to restart
 * that animation: it changes exactly when a slide begins, when a clip finally reports its length,
 * when the media gives up, and when a paused slide resumes. Nothing else moves it.
 *
 * The consequence worth knowing: a swipe through a whole channel costs a handful of state
 * emissions rather than several hundred, and every timing rule above is a plain assertion in a
 * unit test rather than something only a screenshot could show.
 */
@Immutable
data class StoryViewerUiState(
    val isLoading: Boolean = true,
    val channels: ImmutableList<StoryChannel> = persistentListOf(),
    val channelIndex: Int = 0,
    val itemIndex: Int = 0,
    /** Bumped whenever the fill has to start over. See the class comment. */
    val segmentToken: Int = 0,
    val segmentDurationMs: Long = STORY_DEFAULT_DURATION_MS,
    /** How much of the segment had already run when it was held; zero at every fresh start. */
    val segmentElapsedMs: Long = 0L,
    /** A finger resting on the story. */
    val isTouchHeld: Boolean = false,
    /**
     * The comment field has focus and the keyboard is up.
     *
     * Tracked apart from [isTouchHeld] because the two are independent: lifting a finger must not
     * restart a story that is waiting on the keyboard, and closing the keyboard must not restart
     * one a finger is still resting on.
     */
    val isComposingComment: Boolean = false,
    val commentDraft: String = "",
    /** A clip that has not yet said how long it is. The bar waits rather than guessing. */
    val isBuffering: Boolean = false,
    /** The media could not be shown; the slide falls back to its gradient and carries on. */
    val mediaFailed: Boolean = false,
    val likedItems: ImmutableSet<String> = persistentSetOf(),
    val savedItems: ImmutableSet<String> = persistentSetOf(),
    val error: String? = null,
) {
    val channel: StoryChannel? get() = channels.getOrNull(channelIndex)

    val item: StoryItem? get() = channel?.items?.getOrNull(itemIndex)

    val itemCount: Int get() = channel?.items?.size ?: 0

    /** Anything at all is holding the story: a finger, or the comment keyboard. */
    val isPaused: Boolean get() = isTouchHeld || isComposingComment

    /** Whether the fill should be moving right now. */
    val isPlaying: Boolean get() = !isPaused && !isBuffering && item != null

    val isLiked: Boolean get() = item?.id in likedItems

    val isSaved: Boolean get() = item?.id in savedItems

    /** The slide's own count plus this reader's, which is held locally until a service owns it. */
    val likeCount: Int
        get() = item?.let { it.baseLikes + if (it.id in likedItems) 1 else 0 } ?: 0

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState

        data class Loaded(
            val channels: ImmutableList<StoryChannel>,
            val channelIndex: Int,
        ) : PartialState

        /** A slide came up. Resets the elapsed time and restarts the fill. */
        data class SegmentStarted(
            val channelIndex: Int,
            val itemIndex: Int,
            val durationMs: Long,
            val isBuffering: Boolean,
        ) : PartialState

        /** The clip reported its length, so the segment restarts on the real duration. */
        data class DurationResolved(val durationMs: Long) : PartialState

        /** The media could not be shown; the slide runs out on the default duration instead. */
        data class MediaFailed(val durationMs: Long) : PartialState

        /**
         * A hold started or ended. Both reasons travel together so the reducer always writes a
         * consistent pair, and [elapsedMs] carries how far the slide got for the bar to pick up
         * from — it is unchanged when a hold merely swaps reasons.
         */
        data class Held(
            val touchHeld: Boolean,
            val composingComment: Boolean,
            val elapsedMs: Long,
        ) : PartialState

        data class CommentDraftChanged(val draft: String) : PartialState

        data class Engagement(
            val likedItems: ImmutableSet<String>,
            val savedItems: ImmutableSet<String>,
        ) : PartialState

        data class Error(val message: String) : PartialState
    }
}

sealed interface StoryViewerIntent {
    /** Sent once by the screen with the channel the rail was tapped on. */
    data class Open(val channelIndex: Int) : StoryViewerIntent

    /**
     * The wide tap column, which under a right-to-left page is the left of the screen.
     *
     * A **tap**, never a hold: the gesture detector separates the two by the platform's long-press
     * threshold, so resting a finger on this column pauses the story and never moves it.
     */
    data object Next : StoryViewerIntent

    /** The narrow tap column, on the right. Same tap-not-hold rule as [Next]. */
    data object Previous : StoryViewerIntent

    /** A finger came to rest on the story, past the tap threshold. */
    data object Pause : StoryViewerIntent

    /** That finger lifted. */
    data object Resume : StoryViewerIntent

    /**
     * The comment field gained or lost focus. Holds the story for as long as the keyboard is up,
     * independently of any finger resting on it.
     */
    data class CommentFocusChanged(val focused: Boolean) : StoryViewerIntent

    data class CommentChanged(val draft: String) : StoryViewerIntent

    /**
     * The reader sent their comment.
     *
     * There is nowhere to send it yet, so this clears the field and lets the story run on. The
     * field is deliberately real anyway: how it feels to type into is exactly what this stage is
     * meant to test.
     */
    data object CommentSubmitted : StoryViewerIntent

    /**
     * Raised by the segment clock when a slide has had its time.
     *
     * [serial] identifies the run that raised it. A tap that moves the story on cancels the clock
     * and starts a new run, and this lets a tick already in flight from the old one be discarded
     * instead of advancing the story a second time.
     */
    data class AutoAdvance(val serial: Int) : StoryViewerIntent

    /** The player knows how long the clip is; the segment adopts that as its duration. */
    data class MediaReady(val durationMs: Long) : StoryViewerIntent

    /** The clip played out. Moves on without waiting for the clock, which agrees to the frame. */
    data object MediaEnded : StoryViewerIntent

    /** The image or the clip could not be loaded. */
    data object MediaFailed : StoryViewerIntent

    data object ToggleLike : StoryViewerIntent
    data object ToggleSave : StoryViewerIntent

    data object CtaClicked : StoryViewerIntent
    data object Close : StoryViewerIntent
}

sealed interface StoryViewerEvent {
    /** Leave the viewer: the last story ended, or the reader closed it. */
    data object Close : StoryViewerEvent

    /**
     * A call to action was taken. Carries the flag rather than a route so the host graph decides
     * where it goes — and whether the server has that service switched on.
     */
    data class OpenFeature(val flag: FeatureFlag) : StoryViewerEvent
}
