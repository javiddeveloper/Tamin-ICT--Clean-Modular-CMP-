package com.tamin.taminhamrah.feature.stories.ui.rail

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.stories.data.StoryCatalog
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailEvent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailIntent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailUiState
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailUiState.PartialState
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

/**
 * The «تازه‌ها» rail.
 *
 * Owns nothing but the presentation of [StoryCatalog]: the list, whether it is still arriving,
 * whether it failed, and which channels have been watched. Opening one is an event, because the
 * viewer is a destination in the host graph rather than something this feature can navigate to.
 */
class StoryRailViewModel(
    private val catalog: StoryCatalog,
) : BaseViewModel<StoryRailUiState, PartialState, StoryRailEvent, StoryRailIntent>(
    initialState = StoryRailUiState(),
) {
    /**
     * Whether the catalogue is already being watched.
     *
     * The observation never completes — a `StateFlow` does not — so a second one would be a
     * second permanent collector rather than a refresh. The catalogue guards the fetch itself;
     * this guards the subscription, which is the other half of not doing the work twice.
     */
    private var observing = false

    init {
        sendIntent(StoryRailIntent.Load)
    }

    override fun handleIntent(intent: StoryRailIntent): Flow<PartialState> = flow {
        when (intent) {
            StoryRailIntent.Load -> load(force = false)
            StoryRailIntent.Retry -> load(force = true)
            is StoryRailIntent.OpenChannel -> sendEvent(StoryRailEvent.OpenViewer(intent.index))
        }
    }

    private suspend fun FlowCollector<PartialState>.load(force: Boolean) {
        emit(PartialState.Loading(true))
        try {
            catalog.ensureLoaded(force = force)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            emit(PartialState.Error(e.toSingleLineMessage()))
            return
        }
        emit(PartialState.Loading(false))

        if (observing) return
        observing = true
        // Merged rather than combined: the two flows change for unrelated reasons, and combining
        // them would republish the whole list every time a ring goes grey.
        emitAll(
            merge(
                catalog.channels.map { PartialState.Channels(it) },
                catalog.seenChannels.map { PartialState.Seen(it) },
            ),
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
