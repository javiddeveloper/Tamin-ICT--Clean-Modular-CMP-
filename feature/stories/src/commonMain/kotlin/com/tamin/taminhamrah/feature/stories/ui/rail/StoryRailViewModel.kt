package com.tamin.taminhamrah.feature.stories.ui.rail

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.stories.ui.mapper.toPresentation
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailEvent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailIntent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailUiState
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailUiState.PartialState
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.stories.GetStoryChannelsUseCase
import com.tamin.taminhamrah.useCases.stories.ObserveSeenStoryChannelsUseCase
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * The «تازه‌ها» rail.
 *
 * Owns nothing but the presentation of two use cases: the channels, and which of them have been
 * watched. Opening one is an event, because the viewer is a destination in the host graph rather
 * than somewhere this feature can navigate to.
 */
class StoryRailViewModel(
    private val getStoryChannelsUseCase: GetStoryChannelsUseCase,
    private val observeSeenStoryChannelsUseCase: ObserveSeenStoryChannelsUseCase,
) : BaseViewModel<StoryRailUiState, PartialState, StoryRailEvent, StoryRailIntent>(
    initialState = StoryRailUiState(),
) {
    init {
        sendIntent(StoryRailIntent.Load)
    }

    override fun handleIntent(intent: StoryRailIntent): Flow<PartialState> = flow {
        when (intent) {
            StoryRailIntent.Load -> load(forceRefresh = false)

            // Guarded rather than always allowed: the retry only exists on the error row, and
            // without this a second tap would leave a second permanent collector behind.
            StoryRailIntent.Retry -> if (uiState.value.error != null) load(forceRefresh = true)

            is StoryRailIntent.OpenChannel -> sendEvent(StoryRailEvent.OpenViewer(intent.index))
        }
    }

    private suspend fun FlowCollector<PartialState>.load(forceRefresh: Boolean) {
        emit(PartialState.Loading(true))
        // Merged rather than combined: the two change for unrelated reasons, and combining them
        // would republish the whole list every time a single ring goes grey. Neither completes —
        // the catalogue keeps answering, and that is what carries a later refresh to the rail.
        emitAll(
            merge(
                getStoryChannelsUseCase(forceRefresh)
                    .map { PartialState.Channels(it.toPresentation()) },
                observeSeenStoryChannelsUseCase()
                    .map { PartialState.Seen(it.toImmutableSet()) },
            ).catch { emit(PartialState.Error(it.toSingleLineMessage())) },
        )
    }

    override fun reduceState(
        currentState: StoryRailUiState,
        partialState: PartialState,
    ): StoryRailUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            // A load beginning retires the previous failure, so the retry row cannot outlive the
            // attempt it belongs to.
            error = if (partialState.isLoading) null else currentState.error,
        )

        is PartialState.Channels -> currentState.copy(
            isLoading = false,
            channels = partialState.channels,
            error = null,
        )

        is PartialState.Seen -> currentState.copy(seenKeys = partialState.seenKeys)

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
