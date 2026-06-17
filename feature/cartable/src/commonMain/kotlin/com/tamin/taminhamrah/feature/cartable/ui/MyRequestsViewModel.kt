package com.tamin.taminhamrah.feature.cartable.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsEvent
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsIntent
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsUiState
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsUiState.PartialState
import com.tamin.taminhamrah.mapper.request.toPresentation
import com.tamin.taminhamrah.useCases.request.GetMyRequestsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MyRequestsViewModel(
    private val getMyRequestsUseCase: GetMyRequestsUseCase
) : BaseViewModel<MyRequestsUiState, PartialState, MyRequestsEvent, MyRequestsIntent>(
    initialState = MyRequestsUiState()
) {

    override fun handleIntent(intent: MyRequestsIntent): Flow<PartialState> {
        return when (intent) {
            is MyRequestsIntent.LoadRequests -> handleLoadRequests()
        }
    }

    private fun handleLoadRequests(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getMyRequestsUseCase().collect { requests ->
                emit(PartialState.RequestsLoaded(requests.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: MyRequestsUiState,
        partialState: PartialState
    ): MyRequestsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.RequestsLoaded -> currentState.copy(
            isLoading = false,
            requests = partialState.requests,
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
