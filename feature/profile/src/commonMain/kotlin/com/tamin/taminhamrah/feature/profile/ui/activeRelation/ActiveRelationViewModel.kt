package com.tamin.taminhamrah.feature.profile.ui.activeRelation

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationEvent
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationIntent
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState.PartialState
import com.tamin.taminhamrah.mapper.activeRelation.toUiModelList
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCase
import com.tamin.taminhamrah.util.currentTime
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ActiveRelationViewModel(
    private val getRelationTaminAllUseCase: GetRelationTaminAllUseCase
) : BaseViewModel<ActiveRelationUiState, PartialState, ActiveRelationEvent, ActiveRelationIntent>(
    initialState = ActiveRelationUiState()
) {

    init {
        sendIntent(ActiveRelationIntent.LoadActiveRelations)
    }

    override fun handleIntent(intent: ActiveRelationIntent): Flow<PartialState> =
        when (intent) {
            is ActiveRelationIntent.LoadActiveRelations -> handleLoadActiveRelations()
            is ActiveRelationIntent.OnBackClicked -> flow { sendEvent(ActiveRelationEvent.NavigateBack) }
        }

    private fun handleLoadActiveRelations(): Flow<PartialState> = flow {
        emit(PartialState.SetLoading(true))
        getRelationTaminAllUseCase.invoke()
            .map { relations ->
                val uiItems = relations.toUiModelList()
                val activeCount = uiItems.count { it.isActive }
                val inactiveCount = uiItems.count { !it.isActive }

                PartialState.SetData(
                    items = uiItems.toImmutableList(),
                    activeCount = activeCount,
                    inactiveCount = inactiveCount,
                    lastCheckTime = currentTime()
                )
            }
            .catch { emit(PartialState.SetError(it.message ?: "خطای نامشخص")) }
            .collect {
                emit(it)
                emit(PartialState.SetLoading(false))
            }
    }

    override fun reduceState(
        currentState: ActiveRelationUiState,
        partialState: PartialState
    ): ActiveRelationUiState = when (partialState) {
        is PartialState.SetLoading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.SetData -> currentState.copy(
            isLoading = false,
            items = partialState.items,
            activeCount = partialState.activeCount,
            inactiveCount = partialState.inactiveCount,
            lastCheckTime = partialState.lastCheckTime,
            error = null
        )
        is PartialState.SetError -> currentState.copy(
            isLoading = false,
            error = partialState.error
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.SetError(message)
}
