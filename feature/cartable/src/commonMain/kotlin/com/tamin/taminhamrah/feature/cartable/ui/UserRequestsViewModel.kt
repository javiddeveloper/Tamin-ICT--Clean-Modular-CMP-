package com.tamin.taminhamrah.feature.cartable.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsEvent
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsIntent
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsUiState
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsUiState.PartialState
import com.tamin.taminhamrah.mapper.userRequest.toPresentation
import com.tamin.taminhamrah.mapper.userRequest.toTypePresentation
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestTypesUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRequestsViewModel(
    private val getUserRequestsUseCase: GetUserRequestsUseCase,
    private val getUserRequestTypesUseCase: GetUserRequestTypesUseCase,
) : BaseViewModel<MyRequestsUiState, PartialState, MyRequestsEvent, MyRequestsIntent>(
    initialState = MyRequestsUiState()
) {

    override fun handleIntent(intent: MyRequestsIntent): Flow<PartialState> {
        return when (intent) {
            is MyRequestsIntent.LoadRequests -> handleLoadRequests(UserRequestSearchParams())
            is MyRequestsIntent.LoadRequestTypes -> handleLoadRequestTypes()
            is MyRequestsIntent.UpdateRefCode -> flow { emit(PartialState.RefCodeChanged(intent.refCode)) }
            is MyRequestsIntent.UpdateRequestType -> flow {
                emit(PartialState.RequestTypeChanged(intent.requestTypeId))
            }
            is MyRequestsIntent.SearchRequests -> handleLoadRequests(currentSearchParams())
        }
    }

    private fun currentSearchParams(): UserRequestSearchParams {
        val state = uiState.value
        return UserRequestSearchParams(
            refCode = state.refCode.takeIf { it.isNotBlank() },
            requestTypeId = state.selectedRequestTypeId,
        )
    }

    private fun handleLoadRequestTypes(): Flow<PartialState> = flow {
        emit(PartialState.LoadingTypes(true))
        try {
            val types = getUserRequestTypesUseCase().toTypePresentation()
            emit(PartialState.RequestTypesLoaded(types))
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadRequests(search: UserRequestSearchParams): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getUserRequestsUseCase(search).collect { requests ->
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
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.LoadingTypes -> currentState.copy(isLoadingTypes = partialState.isLoadingTypes)
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isLoadingTypes = false,
            error = partialState.message,
        )
        is PartialState.RefCodeChanged -> currentState.copy(refCode = partialState.refCode)
        is PartialState.RequestTypeChanged -> currentState.copy(selectedRequestTypeId = partialState.requestTypeId)
        is PartialState.RequestTypesLoaded -> currentState.copy(
            isLoadingTypes = false,
            requestTypes = partialState.requestTypes,
        )
        is PartialState.RequestsLoaded -> currentState.copy(
            isLoading = false,
            requests = partialState.requests,
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
