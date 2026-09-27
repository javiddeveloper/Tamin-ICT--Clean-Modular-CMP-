package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.contract.InstallmentDebitListEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.contract.InstallmentDebitListIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.contract.InstallmentDebitListUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.contract.InstallmentDebitListUiState.PartialState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetDetailDebitListPageUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class InstallmentDebitListViewModel(
    private val getDetailDebitListPageUseCase: GetDetailDebitListPageUseCase,
) : BaseViewModel<InstallmentDebitListUiState, PartialState, InstallmentDebitListEvent, InstallmentDebitListIntent>(
    initialState = InstallmentDebitListUiState()
) {

    private var fileNumber: Long? = null
    private var workshopId: String? = null
    private var branchId: String = ""
    private var debitNumber: String = ""
    private var hasLoaded = false

    private val paginator = Paginator(
        loadPages = { query -> getDetailDebitListPageUseCase(debitNumber, branchId, query) }, // offline-first: collect the whole flow, not `.first()`
    )

    override fun handleIntent(intent: InstallmentDebitListIntent): Flow<PartialState> =
        when (intent) {
            is InstallmentDebitListIntent.Load -> {
                if (hasLoaded) {
                    emptyFlow()
                } else {
                    hasLoaded = true
                    fileNumber = intent.fileNumber
                    workshopId = intent.workshopId
                    branchId = intent.branchId
                    debitNumber = intent.debitNumber
                    merge(
                        flow { emit(PartialState.HeaderSeeded(fileNumber, workshopId, branchId, debitNumber)) },
                        observePaging(),
                        flow { paginator.loadNext() },
                    )
                }
            }

            InstallmentDebitListIntent.LoadNextPage -> flow { paginator.loadNext() }

            InstallmentDebitListIntent.RetryNextPage -> flow { paginator.retry() }

            InstallmentDebitListIntent.OnBackClicked -> {
                sendEvent(InstallmentDebitListEvent.NavigateBack)
                emptyFlow()
            }
        }

    private fun observePaging(): Flow<PartialState> = paginator.state.map { paging ->
        val errorMessage = paging.error?.toSingleLineMessage()
        if (errorMessage != null && paging.items.isEmpty()) {
            sendEvent(InstallmentDebitListEvent.ShowError(errorMessage))
        }
        PartialState.PagingChanged(
            items = paging.items.map { it.toPR() }.toImmutableList(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = errorMessage,
        )
    }

    override fun reduceState(
        currentState: InstallmentDebitListUiState,
        partialState: PartialState,
    ): InstallmentDebitListUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            fileNumber = partialState.fileNumber,
            workshopId = partialState.workshopId,
            branchId = partialState.branchId,
            debitNumber = partialState.debitNumber,
        )

        is PartialState.PagingChanged -> currentState.copy(
            items = partialState.items,
            isLoading = partialState.isLoadingFirstPage,
            isLoadingNextPage = partialState.isLoadingNextPage,
            endReached = partialState.endReached,
            error = null,
            paginationError = partialState.error,
        )

        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
