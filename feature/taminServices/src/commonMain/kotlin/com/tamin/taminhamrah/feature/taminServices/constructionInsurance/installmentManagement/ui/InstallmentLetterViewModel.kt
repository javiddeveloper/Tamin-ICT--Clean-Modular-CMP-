package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterUiState.PartialState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetInstallmentLetterListPageUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class InstallmentLetterViewModel(
    private val getInstallmentLetterListPageUseCase: GetInstallmentLetterListPageUseCase,
) : BaseViewModel<InstallmentLetterUiState, PartialState, InstallmentLetterEvent, InstallmentLetterIntent>(
    initialState = InstallmentLetterUiState()
) {

    private var workshopId: String = ""
    private var branchId: String = ""
    private var hasLoaded = false

    private val paginator = Paginator(
        loadPage = { query -> getInstallmentLetterListPageUseCase(workshopId, branchId, query).first() },
    )

    override fun handleIntent(intent: InstallmentLetterIntent): Flow<PartialState> =
        when (intent) {
            is InstallmentLetterIntent.Load -> {
                if (hasLoaded) {
                    emptyFlow()
                } else {
                    hasLoaded = true
                    workshopId = intent.workshopId
                    branchId = intent.branchId
                    merge(
                        flow { emit(PartialState.HeaderSeeded(workshopId, branchId)) },
                        observePaging(),
                        flow { paginator.loadNext() },
                    )
                }
            }

            InstallmentLetterIntent.LoadNextPage -> flow { paginator.loadNext() }

            InstallmentLetterIntent.RetryNextPage -> flow { paginator.retry() }

            InstallmentLetterIntent.OnBackClicked -> {
                sendEvent(InstallmentLetterEvent.NavigateBack)
                emptyFlow()
            }
        }

    private fun observePaging(): Flow<PartialState> = paginator.state.map { paging ->
        val errorMessage = paging.error?.toSingleLineMessage()
        if (errorMessage != null && paging.items.isEmpty()) {
            sendEvent(InstallmentLetterEvent.ShowError(errorMessage))
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
        currentState: InstallmentLetterUiState,
        partialState: PartialState,
    ): InstallmentLetterUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            workshopId = partialState.workshopId,
            branchId = partialState.branchId,
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
