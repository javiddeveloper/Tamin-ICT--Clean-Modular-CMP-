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
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.userRequest.GetSmartGuideListUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestErrorsUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestTypesUseCase
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestsPageUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.getString
import taminx.feature.userrequest.generated.resources.Res
import taminx.feature.userrequest.generated.resources.user_request_smart_guide_not_found
import taminx.feature.userrequest.generated.resources.user_request_tracking_code_copied
import taminx.feature.userrequest.generated.resources.user_request_no_errors_to_show

class UserRequestsViewModel(
    private val getUserRequestsPageUseCase: GetUserRequestsPageUseCase,
    private val getUserRequestTypesUseCase: GetUserRequestTypesUseCase,
    private val getUserRequestErrorsUseCase: GetUserRequestErrorsUseCase,
    private val getSmartGuideListUseCase: GetSmartGuideListUseCase,
) : BaseViewModel<UserRequestsUiState, PartialState, UserRequestsEvent, UserRequestsIntent>(
    initialState = UserRequestsUiState()
) {

    private var lastInitFilters: UserRequestsIntent.InitFilters? = null

    override fun handleIntent(intent: UserRequestsIntent): Flow<PartialState> {
        return when (intent) {
            is UserRequestsIntent.InitFilters -> {
                // The screen re-sends this each time it re-enters composition (back from detail);
                // reloading then would reset the list to page one and lose the scroll position.
                if (intent == lastInitFilters) return emptyFlow()
                lastInitFilters = intent
                handleInitFilters(intent.refCode, intent.requestTypeId)
            }
            is UserRequestsIntent.LoadRequests -> handleLoadRequests(UserRequestSearchParams())
            is UserRequestsIntent.LoadNextPage -> flow { paginator.loadNext() }
            is UserRequestsIntent.RetryNextPage -> flow { paginator.retry() }
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
            is UserRequestsIntent.CopyTrackingCode -> flow {
                val message = runCatching { getString(Res.string.user_request_tracking_code_copied) }
                    .getOrElse { Res.string.user_request_tracking_code_copied.toString() }
                sendEvent(UserRequestsEvent.ShowToast(message))
            }
        }
    }

    private fun handleInitFilters(refCode: String?, requestTypeId: String?): Flow<PartialState> = flow {
        val cleanRef = refCode?.takeIf { it.isNotBlank() }
        val cleanTypeId = requestTypeId?.takeIf { it.isNotBlank() }
        if (cleanRef != null) {
            emit(PartialState.RefCodeChanged(cleanRef))
        }
        if (cleanTypeId != null) {
            emit(PartialState.RequestTypeSelected(cleanTypeId, null))
        }
        if (cleanRef != null || cleanTypeId != null) {
            emit(PartialState.FilterToggled(true))
        }
        val search = UserRequestSearchParams(
            refCode = cleanRef,
            requestTypeId = cleanTypeId,
        )
        emitAll(handleLoadRequests(search))
    }

    private fun currentSearchParams(): UserRequestSearchParams {
        val state = uiState.value
        return UserRequestSearchParams(
            refCode = state.refCode.takeIf { it.isNotBlank() },
            requestTypeId = state.selectedRequestTypeId,
        )
    }

    /** Read by the paginator at load time; a newer search's refresh() wins via its generation. */
    private var search = UserRequestSearchParams()

    private val paginator = Paginator(
        loadPages = { page -> getUserRequestsPageUseCase(search, page) },
    )

    /**
     * paginator.state never completes, and BaseViewModel's flatMapMerge holds at most 16 open
     * flows — so it is observed exactly once, by the first load; later loads only refresh().
     */
    private var isObservingPaging = false

    private fun handleLoadRequests(search: UserRequestSearchParams): Flow<PartialState> {
        this.search = search
        val load = flow<PartialState> { paginator.refresh() }
        if (isObservingPaging) return load
        isObservingPaging = true
        return merge(observePaging(), load)
    }

    private fun observePaging(): Flow<PartialState> = paginator.state.map { paging ->
        PartialState.PagingChanged(
            requests = paging.items.toPresentation(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = paging.error?.toSingleLineMessage(),
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

    private fun handleLoadErrors(requestId: Long, title: String): Flow<PartialState> = flow {
        emit(PartialState.LoadingErrors(true))
        try {
            val errors = getUserRequestErrorsUseCase(requestId).toErrorPresentation()
            if (errors.isEmpty()) {
                emit(PartialState.LoadingErrors(false))
                val message = runCatching { getString(Res.string.user_request_no_errors_to_show) }
                    .getOrElse { Res.string.user_request_no_errors_to_show.toString() }
                emit(PartialState.InfoDialogToggled(message))
            } else {
                emit(PartialState.ErrorsLoaded(errors, title))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
            // A tap-triggered load must say it failed; state.error isn't rendered on this screen.
            sendEvent(UserRequestsEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleLoadSmartGuide(requestType: Int?, requestStatus: String?, title: String): Flow<PartialState> = flow {
        emit(PartialState.LoadingSmartGuide(true))
        try {
            val params = SmartGuideSearchParams(requestType = requestType, requestStatus = requestStatus, isPublic = true)
            val guides = getSmartGuideListUseCase(params).toSmartGuidePresentation()
            if (guides.isEmpty()) {
                emit(PartialState.LoadingSmartGuide(false))
                val message = runCatching { getString(Res.string.user_request_smart_guide_not_found) }
                    .getOrElse { Res.string.user_request_smart_guide_not_found.toString() }
                emit(PartialState.InfoDialogToggled(message))
            } else {
                emit(PartialState.SmartGuideLoaded(guides, title))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
            // A tap-triggered load must say it failed; state.error isn't rendered on this screen.
            sendEvent(UserRequestsEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleViewDetails(intent: UserRequestsIntent.ViewDetails): Flow<PartialState> = flow {
        sendEvent(
            UserRequestsEvent.NavigateToDetail(
                requestId = intent.request.id,
                refCode = intent.request.refCode,
                requestTypeId = intent.request.requestTypeId,
                title = intent.request.title,
                referenceId = intent.request.referenceId,
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
        is PartialState.PagingChanged -> currentState.copy(
            requests = partialState.requests,
            isLoading = partialState.isLoadingFirstPage,
            isLoadingNextPage = partialState.isLoadingNextPage,
            endReached = partialState.endReached,
            paginationError = partialState.error,
        )
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
