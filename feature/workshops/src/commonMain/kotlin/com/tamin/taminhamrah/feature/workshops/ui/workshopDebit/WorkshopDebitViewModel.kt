package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebitsUseCase
import com.tamin.taminhamrah.useCases.workshops.PayWorkshopDebitUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_debt_payment_refused

/** جزئیات محاسبه گردش حساب بدهی. */
class WorkshopDebitViewModel(
    private val getWorkshopDebits: GetWorkshopDebitsUseCase,
    private val payWorkshopDebit: PayWorkshopDebitUseCase,
) : BaseViewModel<WorkshopDebitUiState, PartialState, WorkshopDebitEvent, WorkshopDebitIntent>(
    initialState = WorkshopDebitUiState()
) {

    override fun handleIntent(intent: WorkshopDebitIntent): Flow<PartialState> = when (intent) {
        is WorkshopDebitIntent.Open -> open(intent)
        WorkshopDebitIntent.LoadMore -> loadMore()
        WorkshopDebitIntent.Retry -> loadPage(page = 0)
        is WorkshopDebitIntent.PayDebit -> pay(intent.debt)
    }

    private fun open(intent: WorkshopDebitIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode))
        emitAll(loadPage(page = 0, identity = intent.workshopId to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (workshopId, branchCode) = identity
        if (workshopId.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getWorkshopDebits(workshopId, branchCode, page)
        emit(
            PartialState.Loaded(
                uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() }
            )
        )
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    /**
     * Pre-check and payment are one step (the use case chains them), so a debt is never paid
     * without being checked and a refusal at either point reads the same to the user.
     */
    private fun pay(debt: WorkShopDebtPR): Flow<PartialState> = flow<PartialState> {
        val state = uiState.value
        if (state.payingDebitNumber != null) return@flow

        emit(PartialState.Paying(debt.debitNumber))
        val result = payWorkshopDebit(
            DebitPaymentRequestDN(
                workshopId = state.workshopId,
                branchCode = state.branchCode,
                debitNumber = debt.debitNumber,
                agreementRow = debt.agreementRow,
            )
        )
        emit(PartialState.Paying(null))

        if (result.isPayable) {
            sendEvent(WorkshopDebitEvent.OpenPaymentPage(result.paymentPageUrl))
        } else {
            reportPaymentFailure(result.message)
        }
    }.catch {
        emit(PartialState.Paying(null))
        // Not [PartialState.Error]: that sets the *list*'s error, which the scaffold only draws
        // for an empty list — so a debt that failed to reach the payment service at all (a 500,
        // a dropped connection) told the user nothing while the rows sat there unchanged.
        reportPaymentFailure(it.toSingleLineMessage())
    }

    /**
     * Why a payment did not happen, said out loud.
     *
     * Refusals and transport failures arrive by different routes but read the same to the user, and
     * both go through here so that neither can end up silent: a blank reason still produces the
     * fallback wording rather than an empty toast.
     */
    private fun reportPaymentFailure(message: String) {
        if (message.isBlank()) {
            sendEvent(WorkshopDebitEvent.ShowMessage(Res.string.workshop_debt_payment_refused))
        } else {
            sendEvent(WorkshopDebitEvent.ShowServerMessage(message))
        }
    }

    override fun reduceState(
        currentState: WorkshopDebitUiState,
        partialState: PartialState,
    ): WorkshopDebitUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(list = currentState.list.failed(partialState.message))
        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.Paying -> currentState.copy(payingDebitNumber = partialState.debitNumber)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
