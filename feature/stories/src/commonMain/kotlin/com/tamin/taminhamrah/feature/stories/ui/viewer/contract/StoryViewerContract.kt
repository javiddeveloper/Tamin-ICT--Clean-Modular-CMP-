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
    /** How much of the segment had already run when it was paused; zero at every fresh start. */
    val segmentElapsedMs: Long = 0L,
    val isPaused: Boolean = false,
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

        data class Paused(val elapsedMs: Long) : PartialState

        data object Resumed : PartialState

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

    /** The wide tap column, which under a right-to-left page is the left of the screen. */
    data object Next : StoryViewerIntent

    /** The narrow tap column, on the right. */
    data object Previous : StoryViewerIntent

    data object Pause : StoryViewerIntent
    data object Resume : StoryViewerIntent

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
