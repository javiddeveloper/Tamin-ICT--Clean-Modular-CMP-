package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementUiState.PartialState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetInstallmentConstructionListPageUseCase
import com.tamin.taminhamrah.useCases.constructionInsurance.IssuancePaymentSheetUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class InstallmentManagementViewModel(
    private val getInstallmentConstructionListPageUseCase: GetInstallmentConstructionListPageUseCase,
    private val issuancePaymentSheetUseCase: IssuancePaymentSheetUseCase,
) : BaseViewModel<InstallmentManagementUiState, PartialState, InstallmentManagementEvent, InstallmentManagementIntent>(
    initialState = InstallmentManagementUiState()
) {

    private var fileNumber: Long? = null
    private var workshopId: String? = null
    private var branchId: String = ""
    private var debitNumber: String = ""
    private var debitStepDescription: String? = null
    private var hasLoaded = false

    private val paginator = Paginator(
        loadPage = { query -> getInstallmentConstructionListPageUseCase(debitNumber, branchId, query).first() },
    )

    override fun handleIntent(intent: InstallmentManagementIntent): Flow<PartialState> =
        when (intent) {
            is InstallmentManagementIntent.Load -> {
                if (hasLoaded) {
                    emptyFlow()
                } else {
                    hasLoaded = true
                    fileNumber = intent.fileNumber
                    workshopId = intent.workshopId
                    branchId = intent.branchId
                    debitNumber = intent.debitNumber
                    debitStepDescription = intent.debitStepDescription
                    merge(
                        flow {
                            emit(
                                PartialState.HeaderSeeded(
                                    fileNumber, workshopId, branchId, debitNumber, debitStepDescription,
                                )
                            )
                        },
                        observePaging(),
                        flow { paginator.loadNext() },
                    )
                }
            }

            InstallmentManagementIntent.LoadNextPage -> flow { paginator.loadNext() }

            InstallmentManagementIntent.RetryNextPage -> flow { paginator.retry() }

            InstallmentManagementIntent.IssuePaymentSheet -> issuePaymentSheet()

            InstallmentManagementIntent.DismissIssuanceNotice -> flow {
                emit(PartialState.IssuanceNoticeDismissed)
            }

            InstallmentManagementIntent.OnBackClicked -> {
                sendEvent(InstallmentManagementEvent.NavigateBack)
                emptyFlow()
            }
        }

    /**
     * «صدور برگ پرداخت این قسط» — the old app only ever issues a payment sheet per whole debit
     * (`issuancePaymentSheet(debitNumber)`); there is no per-installment endpoint, so the per-row
     * button in the new design triggers the same call as `PaymentSheetViewModel.issuePaymentSheet`.
     */
    private fun issuePaymentSheet(): Flow<PartialState> = flow {
        if (uiState.value.isIssuing) return@flow
        emit(PartialState.IssuanceFailed(false))
        emit(PartialState.IssuanceLoading(true))
        try {
            val message = issuancePaymentSheetUseCase(debitNumber).first()
            emit(PartialState.IssuanceSucceeded(message))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            sendEvent(InstallmentManagementEvent.ShowError(e.toSingleLineMessage()))
            emit(PartialState.IssuanceFailed(true))
        } finally {
            emit(PartialState.IssuanceLoading(false))
        }
    }

    private fun observePaging(): Flow<PartialState> = paginator.state.map { paging ->
        val errorMessage = paging.error?.toSingleLineMessage()
        if (errorMessage != null && paging.items.isEmpty()) {
            sendEvent(InstallmentManagementEvent.ShowError(errorMessage))
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
        currentState: InstallmentManagementUiState,
        partialState: PartialState,
    ): InstallmentManagementUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            fileNumber = partialState.fileNumber,
            workshopId = partialState.workshopId,
            branchId = partialState.branchId,
            debitNumber = partialState.debitNumber,
            debitStepDescription = partialState.debitStepDescription,
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

        is PartialState.IssuanceLoading -> currentState.copy(isIssuing = partialState.loading)
        is PartialState.IssuanceSucceeded -> currentState.copy(
            issuanceMessage = partialState.message,
            issuanceFailed = false,
        )
        is PartialState.IssuanceFailed -> currentState.copy(issuanceFailed = partialState.failed)
        is PartialState.IssuanceNoticeDismissed -> currentState.copy(issuanceMessage = null)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
