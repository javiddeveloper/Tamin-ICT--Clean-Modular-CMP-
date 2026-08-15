package com.tamin.taminhamrah.feature.userRequest.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsEvent
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsIntent
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsUiState
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsUiState.PartialState
import com.tamin.taminhamrah.mapper.userRequest.toErrorPresentation
import com.tamin.taminhamrah.mapper.userRequest.toPresentation
import com.tamin.taminhamrah.mapper.userRequest.toSmartGuidePresentation
import com.tamin.taminhamrah.mapper.userRequest.toTypePresentation
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.useCases.userRequest.GetSmartGuideListUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestErrorsUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestTypesUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.feature.userrequest.generated.resources.Res
import taminx.feature.userrequest.generated.resources.user_request_smart_guide_not_found
import taminx.feature.userrequest.generated.resources.user_request_tracking_code_copied

class UserRequestsViewModel(
    private val getUserRequestsUseCase: GetUserRequestsUseCase,
    private val getUserRequestTypesUseCase: GetUserRequestTypesUseCase,
    private val getUserRequestErrorsUseCase: GetUserRequestErrorsUseCase,
    private val getSmartGuideListUseCase: GetSmartGuideListUseCase,
) : BaseViewModel<UserRequestsUiState, PartialState, UserRequestsEvent, UserRequestsIntent>(
    initialState = UserRequestsUiState()
) {

    override fun handleIntent(intent: UserRequestsIntent): Flow<PartialState> {
        return when (intent) {
            is UserRequestsIntent.LoadRequests -> handleLoadRequests(UserRequestSearchParams())
            is UserRequestsIntent.LoadRequestTypes -> handleLoadRequestTypes()
            is UserRequestsIntent.SelectTab -> flow { emit(PartialState.TabChanged(intent.tab)) }
            is UserRequestsIntent.UpdateRefCode -> flow { emit(PartialState.RefCodeChanged(intent.refCode)) }
            is UserRequestsIntent.SelectRequestType -> flow {
                emit(PartialState.RequestTypeSelected(intent.typeId, intent.typeName))
            }
            is UserRequestsIntent.ToggleFilter -> flow {
                emit(PartialState.FilterToggled(!uiState.value.isFilterOpen))
            }
            is UserRequestsIntent.SearchRequests -> handleLoadRequests(currentSearchParams())
            is UserRequestsIntent.OpenSmartGuide -> handleLoadSmartGuide(intent.requestType, intent.requestStatus, intent.title)
            is UserRequestsIntent.OpenErrors -> handleLoadErrors(intent.requestId, intent.title)
            is UserRequestsIntent.ShowInfoDialog -> flow { emit(PartialState.InfoDialogToggled(intent.message)) }
            is UserRequestsIntent.CloseSmartGuide -> flow { emit(PartialState.SmartGuideToggled(false)) }
            is UserRequestsIntent.CloseErrors -> flow { emit(PartialState.ErrorsToggled(false)) }
            is UserRequestsIntent.ViewDetails -> handleViewDetails(intent)
            is UserRequestsIntent.CopyTrackingCode -> flow { sendEvent(UserRequestsEvent.ShowToast(getString(Res.string.user_request_tracking_code_copied))) }
        }
    }

    private fun currentSearchParams(): UserRequestSearchParams {
        val state = uiState.value
        return UserRequestSearchParams(
            refCode = state.refCode.takeIf { it.isNotBlank() },
            requestTypeId = state.selectedRequestTypeId,
        )
    }

    private fun handleLoadRequests(search: UserRequestSearchParams): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        getUserRequestsUseCase(search)
            .catch { emit(PartialState.Error(it.message)) }
            .collect { requests ->
                emit(PartialState.RequestsLoaded(requests.toPresentation()))
            }
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

    private fun handleLoadErrors(requestId: Long, title: String): Flow<PartialState> = flow {
        emit(PartialState.LoadingErrors(true))
        try {
            val errors = getUserRequestErrorsUseCase(requestId).toErrorPresentation()
            emit(PartialState.ErrorsLoaded(errors, title))
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadSmartGuide(requestType: Int?, requestStatus: String?, title: String): Flow<PartialState> = flow {
        emit(PartialState.LoadingSmartGuide(true))
        try {
            val params = SmartGuideSearchParams(requestType = requestType, requestStatus = requestStatus, isPublic = true)
            val guides = getSmartGuideListUseCase(params).toSmartGuidePresentation()
            if (guides.isEmpty()) {
                // Mirror legacy: "راهنمای هوشمند برای این وضعیت موجود نیست"
                emit(PartialState.LoadingSmartGuide(false))
                emit(PartialState.InfoDialogToggled("راهنمای هوشمند برای وضعیت فعلی این درخواست موجود نیست."))
            } else {
                emit(PartialState.SmartGuideLoaded(guides, title))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleViewDetails(intent: UserRequestsIntent.ViewDetails): Flow<PartialState> = flow {
        sendEvent(
            UserRequestsEvent.NavigateToDetail(
                requestId = intent.request.id,
                refCode = intent.request.refCode,
                requestTypeId = 22L,
                title = intent.request.title

            )
        )
    }

    override fun reduceState(
        currentState: UserRequestsUiState,
        partialState: PartialState
    ): UserRequestsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.LoadingTypes -> currentState.copy(isLoadingTypes = partialState.isLoading)
        is PartialState.LoadingErrors -> currentState.copy(isLoadingErrors = partialState.isLoading)
        is PartialState.LoadingSmartGuide -> currentState.copy(isLoadingSmartGuide = partialState.isLoading)
        is PartialState.RequestsLoaded -> currentState.copy(isLoading = false, requests = partialState.requests, error = null)
        is PartialState.RequestTypesLoaded -> currentState.copy(isLoadingTypes = false, requestTypes = partialState.types)
        is PartialState.SmartGuideLoaded -> currentState.copy(
            isLoadingSmartGuide = false,
            smartGuideItems = partialState.items,
            smartGuideTitle = partialState.title,
            isSmartGuideOpen = true
        )
        is PartialState.ErrorsLoaded -> currentState.copy(
            isLoadingErrors = false,
            errorItems = partialState.items,
            errorTitle = partialState.title,
            isErrorsOpen = true
        )
        is PartialState.TabChanged -> currentState.copy(selectedTab = partialState.tab)
        is PartialState.RefCodeChanged -> currentState.copy(refCode = partialState.refCode)
        is PartialState.RequestTypeSelected -> currentState.copy(
            selectedRequestTypeId = partialState.typeId,
            selectedRequestTypeName = partialState.typeName
        )
        is PartialState.FilterToggled -> currentState.copy(isFilterOpen = partialState.isOpen)
        is PartialState.SmartGuideToggled -> currentState.copy(isSmartGuideOpen = partialState.isOpen)
        is PartialState.ErrorsToggled -> currentState.copy(isErrorsOpen = partialState.isOpen)
        is PartialState.InfoDialogToggled -> currentState.copy(infoDialogMessage = partialState.message)
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isLoadingTypes = false,
            isLoadingErrors = false,
            isLoadingSmartGuide = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
