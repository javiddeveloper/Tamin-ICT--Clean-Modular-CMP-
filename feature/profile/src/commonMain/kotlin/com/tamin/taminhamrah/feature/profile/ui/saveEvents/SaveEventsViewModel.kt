package com.tamin.taminhamrah.feature.profile.ui.saveEvents

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.model.SavedEventPR
import com.tamin.taminhamrah.feature.profile.ui.saveEvents.contract.SaveEventsEvent
import com.tamin.taminhamrah.feature.profile.ui.saveEvents.contract.SaveEventsIntent
import com.tamin.taminhamrah.feature.profile.ui.saveEvents.contract.SaveEventsUiState
import com.tamin.taminhamrah.useCases.stories.GetStoryChannelsUseCase
import com.tamin.taminhamrah.useCases.stories.ObserveStoryEngagementUseCase
import com.tamin.taminhamrah.useCases.stories.ToggleStorySaveUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class SaveEventsViewModel(
    private val getStoryChannelsUseCase: GetStoryChannelsUseCase,
    private val observeStoryEngagementUseCase: ObserveStoryEngagementUseCase,
    private val toggleStorySaveUseCase: ToggleStorySaveUseCase,
) : BaseViewModel<
    SaveEventsUiState,
    SaveEventsUiState.PartialState,
    SaveEventsEvent,
    SaveEventsIntent
    >(
    SaveEventsUiState()
) {

    override fun handleIntent(intent: SaveEventsIntent): Flow<SaveEventsUiState.PartialState> =
        flow {
            when (intent) {
                SaveEventsIntent.NavigateBack -> {
                    sendEvent(SaveEventsEvent.NavigateBack)
                }

                SaveEventsIntent.LoadData -> {
                    val combinedFlow = combine(
                        getStoryChannelsUseCase(),
                        observeStoryEngagementUseCase()
                    ) { channels, engagement ->
                        val savedIds = engagement.savedItemIds
                        val savedEvents = channels.flatMap { channel ->
                            channel.items.filter { it.id in savedIds }.map { item ->
                                SavedEventPR(
                                    id = item.id,
                                    category = channel.name,
                                    time = channel.time,
                                    title = item.title,
                                    description = item.body
                                )
                            }
                        }
                        SaveEventsUiState.PartialState.Success(savedEvents) as SaveEventsUiState.PartialState
                    }.onStart {
                        emit(SaveEventsUiState.PartialState.Loading(true))
                    }
                    emitAll(combinedFlow)
                }
                
                is SaveEventsIntent.ToggleSave -> {
                    toggleStorySaveUseCase(intent.eventId)
                }
            }
        }

    override fun reduceState(
        currentState: SaveEventsUiState,
        partialState: SaveEventsUiState.PartialState
    ): SaveEventsUiState = when (partialState) {
        is SaveEventsUiState.PartialState.Error -> currentState.copy(
            error = partialState.message
        )

        is SaveEventsUiState.PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading
        )

        is SaveEventsUiState.PartialState.Success -> currentState.copy(
            events = partialState.events,
            error = null,
            isLoading = false
        )
    }

    override fun createErrorState(message: String): SaveEventsUiState.PartialState =
        SaveEventsUiState.PartialState.Error(message)
}
