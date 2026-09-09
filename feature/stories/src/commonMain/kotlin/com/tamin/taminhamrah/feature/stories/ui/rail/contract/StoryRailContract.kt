package com.tamin.taminhamrah.feature.stories.ui.rail.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.stories.ui.model.StoryChannelPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

/**
 * The «تازه‌ها» strip on the home page.
 *
 * Four states rather than three: [isLoading] with nothing yet is the shimmer, [error] is the
 * retry row, an empty [channels] with neither of those is the empty line, and anything else is
 * the rings. They are derived here rather than in the composable so the screen has one thing to
 * branch on and the test has one thing to assert.
 */
@Immutable
data class StoryRailUiState(
    val isLoading: Boolean = true,
    val channels: ImmutableList<StoryChannelPR> = persistentListOf(),
    /** Keys of the channels already watched; their rings and labels go grey. */
    val seenKeys: ImmutableSet<String> = persistentSetOf(),
    val error: String? = null,
) {
    val content: StoryRailContent
        get() = when {
            error != null -> StoryRailContent.Error
            channels.isNotEmpty() -> StoryRailContent.Channels
            isLoading -> StoryRailContent.Loading
            else -> StoryRailContent.Empty
        }

    fun isSeen(channel: StoryChannelPR): Boolean = channel.key in seenKeys

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Channels(val channels: ImmutableList<StoryChannelPR>) : PartialState
        data class Seen(val seenKeys: ImmutableSet<String>) : PartialState
        data class Error(val message: String) : PartialState
    }
}

enum class StoryRailContent { Loading, Channels, Empty, Error }

sealed interface StoryRailIntent {
    /**
     * Sent once when the rail is created. Safe to send again — the catalogue fetches at most once,
     * and the observation of it starts at most once.
     */
    data object Load : StoryRailIntent

    /** The retry affordance on the error row: the one call that is allowed to refetch. */
    data object Retry : StoryRailIntent

    data class OpenChannel(val index: Int) : StoryRailIntent
}

sealed interface StoryRailEvent {
    data class OpenViewer(val channelIndex: Int) : StoryRailEvent
}
