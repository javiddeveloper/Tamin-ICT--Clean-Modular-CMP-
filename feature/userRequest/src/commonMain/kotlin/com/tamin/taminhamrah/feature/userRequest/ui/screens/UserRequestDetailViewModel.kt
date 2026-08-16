package com.tamin.taminhamrah.feature.userRequest.ui.screens

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailEvent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailIntent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailState
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailState.PartialState
import com.tamin.taminhamrah.mapper.userRequest.toPresentation
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.useCases.userRequest.GetShowRequestInfoUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestDetailUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRequestDetailViewModel(
    private val getUserRequestDetailUseCase: GetUserRequestDetailUseCase,
    private val getShowRequestInfoUseCase: GetShowRequestInfoUseCase,
) : BaseViewModel<UserRequestDetailState, PartialState, UserRequestDetailEvent, UserRequestDetailIntent>(
    initialState = UserRequestDetailState()
) {

    override fun handleIntent(intent: UserRequestDetailIntent): Flow<PartialState> {
        return when (intent) {
            is UserRequestDetailIntent.NavigateBack -> flow {
                sendEvent(UserRequestDetailEvent.NavigateBack)
            }
            is UserRequestDetailIntent.LoadDetail -> handleLoadDetail(intent)
        }
    }

    private fun handleLoadDetail(intent: UserRequestDetailIntent.LoadDetail): Flow<PartialState> = flow {
        if (uiState.value.isLoading) return@flow
        emit(PartialState.Loading(true))

        val header = runCatching { getUserRequestDetailUseCase(intent.requestId) }.getOrNull()
        val requestTypeId = header?.requestType?.id ?: intent.requestTypeId
        val referenceId = header?.referenceId?.takeIf { it.isNotBlank() } ?: intent.refCode
        val showInfo = runCatching {
            getShowRequestInfoUseCase(referenceId, requestTypeId)
        }.getOrNull()

        val merged = (header ?: fallbackRequest(intent)).copy(
            details = showInfo ?: header?.details,
        )
        emit(PartialState.Loaded(merged.toPresentation()))
    }

    private fun fallbackRequest(intent: UserRequestDetailIntent.LoadDetail): UserRequestDN {
        return UserRequestDN(
            id = intent.requestId,
            refCode = intent.refCode,
            title = intent.title,
            comment = null,
            creationTime = null,
            createByName = null,
            status = null,
            requestType = UserRequestTypeDN(
                id = intent.requestTypeId,
                title = intent.title,
                description = null,
            ),
            referenceId = intent.refCode,
            requestDetails = null,
        )
    }

    override fun reduceState(
        currentState: UserRequestDetailState,
        partialState: PartialState
    ): UserRequestDetailState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            request = partialState.request,
            error = null,
        )
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
