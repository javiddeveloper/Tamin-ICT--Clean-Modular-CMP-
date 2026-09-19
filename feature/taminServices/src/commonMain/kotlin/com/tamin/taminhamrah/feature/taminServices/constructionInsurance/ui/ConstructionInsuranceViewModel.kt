package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsurancePartialState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.toApiQueryParam
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetConstructionFilesPageUseCase
import com.tamin.taminhamrah.useCases.user.GetIdentityInfoUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.operation_request_selected

class ConstructionInsuranceViewModel(
    private val getConstructionFilesPageUseCase: GetConstructionFilesPageUseCase,
    private val getIdentityInfoUseCase: GetIdentityInfoUseCase,
) : BaseViewModel<ConstructionInsuranceState, ConstructionInsurancePartialState, ConstructionInsuranceEvent, ConstructionInsuranceIntent>(
    initialState = ConstructionInsuranceState(isLoading = true)
) {

    private val paginator = Paginator(
        loadPage = { query -> getConstructionFilesPageUseCase(query).first() },
    )

    init {
        sendIntent(ConstructionInsuranceIntent.LoadData)
    }

    override fun handleIntent(intent: ConstructionInsuranceIntent): Flow<ConstructionInsurancePartialState> =
        flow {
            val state = uiState.value
            when (intent) {
                is ConstructionInsuranceIntent.LoadData -> {
                    emitAll(merge(loadIdentity(), observePaging(), flow { paginator.loadNext() }))
                }

                is ConstructionInsuranceIntent.Refresh -> {
                    emitAll(applySearch(state))
                }

                is ConstructionInsuranceIntent.ToggleSearchExpanded -> {
                    emit(ConstructionInsurancePartialState.SearchExpandedToggled(intent.expanded))
                }

                is ConstructionInsuranceIntent.OnFileNoQueryChanged -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = intent.query,
                            reqNo = state.reqNoQuery,
                            workshopId = state.workshopIdQuery,
                            branchCode = state.branchCodeQuery
                        )
                    )
                }

                is ConstructionInsuranceIntent.OnReqNoQueryChanged -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = state.fileNoQuery,
                            reqNo = intent.query,
                            workshopId = state.workshopIdQuery,
                            branchCode = state.branchCodeQuery
                        )
                    )
                }

                is ConstructionInsuranceIntent.OnWorkshopIdQueryChanged -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = state.fileNoQuery,
                            reqNo = state.reqNoQuery,
                            workshopId = intent.query,
                            branchCode = state.branchCodeQuery
                        )
                    )
                }

                is ConstructionInsuranceIntent.OnBranchCodeQueryChanged -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = state.fileNoQuery,
                            reqNo = state.reqNoQuery,
                            workshopId = state.workshopIdQuery,
                            branchCode = intent.query
                        )
                    )
                }

                is ConstructionInsuranceIntent.ExecuteSearch -> {
                    emitAll(applySearch(state))
                }

                is ConstructionInsuranceIntent.ResetSearch -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = "",
                            reqNo = "",
                            workshopId = "",
                            branchCode = ""
                        )
                    )
                    emit(ConstructionInsurancePartialState.AppliedQueryChanged())
                    paginator.refresh(query = ApiQueryParamDN())
                }

                is ConstructionInsuranceIntent.OnDetailClick -> {
                    sendEvent(ConstructionInsuranceEvent.NavigateToDetails(intent.item))
                }

                is ConstructionInsuranceIntent.OnActionClick -> {
                    sendEvent(ConstructionInsuranceEvent.ShowToast(getString(Res.string.operation_request_selected)))
                }

                is ConstructionInsuranceIntent.ToggleNoticeVisibility -> {
                    emit(ConstructionInsurancePartialState.NoticeVisibilityToggled(!state.isNoticeVisible))
                }

                is ConstructionInsuranceIntent.LoadNextPage -> {
                    paginator.loadNext()
                }

                is ConstructionInsuranceIntent.RetryNextPage -> {
                    paginator.retry()
                }
            }
        }

    private fun loadIdentity(): Flow<ConstructionInsurancePartialState> = flow {
        getIdentityInfoUseCase().collect { identity ->
            val name = listOfNotNull(identity.firstName, identity.lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .trim()
            val nationalCode = identity.nationalId.orEmpty()
            emit(
                ConstructionInsurancePartialState.IdentityLoaded(
                    userName = name,
                    nationalCode = nationalCode
                )
            )
        }
    }.catch { /* degraded to blank if fails */ }

    /**
     * The four query fields of [state] become both the new "applied" snapshot (for the filter chip
     * row) and the paginator's next base query — a blank field is simply omitted as a filter, so an
     * all-blank [state] naturally clears back to an unfiltered list.
     */
    private fun applySearch(state: ConstructionInsuranceState): Flow<ConstructionInsurancePartialState> = flow {
        emit(
            ConstructionInsurancePartialState.AppliedQueryChanged(
                appliedFileNo = state.fileNoQuery,
                appliedReqNo = state.reqNoQuery,
                appliedWorkshopId = state.workshopIdQuery,
                appliedBranchCode = state.branchCodeQuery,
            )
        )
        paginator.refresh(query = buildSearchParams(state).toApiQueryParam())
    }

    private fun observePaging(): Flow<ConstructionInsurancePartialState> = paginator.state.map { paging ->
        val errorMessage = paging.error?.toSingleLineMessage()
        if (errorMessage != null && paging.items.isEmpty()) {
            sendEvent(ConstructionInsuranceEvent.ShowToast(errorMessage))
        }
        ConstructionInsurancePartialState.PagingChanged(
            items = paging.items.map { it.toPR() }.toImmutableList(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = errorMessage,
        )
    }

    private fun buildSearchParams(state: ConstructionInsuranceState): ConstructionFileSearchParamsDN? {
        if (state.fileNoQuery.isBlank() &&
            state.reqNoQuery.isBlank() &&
            state.workshopIdQuery.isBlank() &&
            state.branchCodeQuery.isBlank()
        ) {
            return null
        }
        return ConstructionFileSearchParamsDN(
            fileNo = state.fileNoQuery.takeIf { it.isNotBlank() },
            reqNo = state.reqNoQuery.takeIf { it.isNotBlank() },
            workshopId = state.workshopIdQuery.takeIf { it.isNotBlank() },
            branchCode = state.branchCodeQuery.takeIf { it.isNotBlank() }
        )
    }

    override fun reduceState(
        currentState: ConstructionInsuranceState,
        partialState: ConstructionInsurancePartialState
    ): ConstructionInsuranceState = when (partialState) {
        is ConstructionInsurancePartialState.IdentityLoaded -> currentState.copy(
            userName = partialState.userName,
            nationalCode = partialState.nationalCode
        )

        is ConstructionInsurancePartialState.PagingChanged -> currentState.copy(
            items = partialState.items,
            isLoading = partialState.isLoadingFirstPage,
            isLoadingNextPage = partialState.isLoadingNextPage,
            endReached = partialState.endReached,
            error = null,
            paginationError = partialState.error,
        )

        is ConstructionInsurancePartialState.AppliedQueryChanged -> currentState.copy(
            appliedFileNoQuery = partialState.appliedFileNo,
            appliedReqNoQuery = partialState.appliedReqNo,
            appliedWorkshopIdQuery = partialState.appliedWorkshopId,
            appliedBranchCodeQuery = partialState.appliedBranchCode,
        )

        is ConstructionInsurancePartialState.SearchQueriesChanged -> currentState.copy(
            fileNoQuery = partialState.fileNo,
            reqNoQuery = partialState.reqNo,
            workshopIdQuery = partialState.workshopId,
            branchCodeQuery = partialState.branchCode
        )

        is ConstructionInsurancePartialState.SearchExpandedToggled -> currentState.copy(
            isSearchExpanded = partialState.expanded
        )

        is ConstructionInsurancePartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )

        is ConstructionInsurancePartialState.NoticeVisibilityToggled -> currentState.copy(
            isNoticeVisible = partialState.isVisible
        )
    }

    override fun createErrorState(message: String): ConstructionInsurancePartialState =
        ConstructionInsurancePartialState.Error(message)
}
