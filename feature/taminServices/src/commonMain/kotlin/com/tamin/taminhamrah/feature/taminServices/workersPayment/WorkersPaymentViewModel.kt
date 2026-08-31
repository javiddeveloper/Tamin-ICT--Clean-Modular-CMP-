package com.tamin.taminhamrah.feature.taminServices.workersPayment

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentEvent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentIntent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentUiState
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentUiState.PartialState
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.toPR
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workersPayment.GetWorkersPaymentInfoUseCase
import com.tamin.taminhamrah.useCases.workersPayment.InspectWorkersPaymentTicketUseCase
import com.tamin.taminhamrah.useCases.workersPayment.PayWorkersDebitUseCase
import com.tamin.taminhamrah.util.NetworkConstants
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.workers_payment_url_missing

class WorkersPaymentViewModel(
    private val getWorkersPaymentInfoUseCase: GetWorkersPaymentInfoUseCase,
    private val payWorkersDebitUseCase: PayWorkersDebitUseCase,
    private val inspectWorkersPaymentTicketUseCase: InspectWorkersPaymentTicketUseCase,
) : BaseViewModel<WorkersPaymentUiState, PartialState, WorkersPaymentEvent, WorkersPaymentIntent>(
    initialState = WorkersPaymentUiState(),
) {

    init {
        sendIntent(WorkersPaymentIntent.LoadPaymentInfo)
    }

    override fun handleIntent(intent: WorkersPaymentIntent): Flow<PartialState> = when (intent) {
        is WorkersPaymentIntent.LoadPaymentInfo -> loadPaymentInfo()
        is WorkersPaymentIntent.Retry -> loadPaymentInfo()
        is WorkersPaymentIntent.OpenPaymentScreen -> flow { emit(PartialState.PaymentScreenOpened(intent.item)) }
        is WorkersPaymentIntent.ClosePaymentScreen -> flow { emit(PartialState.PaymentScreenClosed) }
        is WorkersPaymentIntent.PayItem -> payItem(intent.item)
        is WorkersPaymentIntent.VerifyPendingPayment -> verifyPendingPayment()
        is WorkersPaymentIntent.DismissReceipt -> dismissReceipt()
    }

    private fun dismissReceipt(): Flow<PartialState> = flow {
        emit(PartialState.ReceiptDismissed)
        emitAll(loadPaymentInfo())
    }

    private fun loadPaymentInfo(): Flow<PartialState> = flow {
        try {
            val result = getWorkersPaymentInfoUseCase()
            emit(
                PartialState.ListLoaded(
                    totalAmount = result.totalAmount,
                    totalPenalty = result.totalPenalty,
                    totalPremium = result.totalPremium,
                    items = result.list.map { it.toPR() }.toImmutableList(),
                ),
            )
        } catch (e: Exception) {
            val message = e.toSingleLineMessage()
            emit(PartialState.Error(message))
            sendEvent(WorkersPaymentEvent.ShowToast(message))
        }
    }.onStart {
        emit(PartialState.ClearError)
        emit(PartialState.Loading(true))
    }.onCompletion {
        emit(PartialState.Loading(false))
    }

    private fun payItem(item: WorkersPaymentInfoPR): Flow<PartialState> = flow {
        emit(PartialState.ProcessingPayment(true))
        try {
            val params = WorkersPayDebitParamsDN(
                amount = item.totalPayable,
                dates = listOf(item.fromDatePersian),
                fromToDate = listOf(item.fromDateToDate),
                redirectUrl = NetworkConstants.WORKERS_PAYMENT_CALLBACK,
            )
            val result = payWorkersDebitUseCase(params)
            val url = result.paymentUrl
            if (url.isNullOrBlank()) {
                emit(PartialState.ProcessingPayment(false))
                sendEvent(WorkersPaymentEvent.ShowToast(getString(Res.string.workers_payment_url_missing)))
                return@flow
            }
            emit(PartialState.PaymentTicketReady(result.ticket, result.paymentInfo))
            emit(PartialState.ProcessingPayment(false))
            sendEvent(WorkersPaymentEvent.OpenPaymentUrl(url))
        } catch (e: Exception) {
            emit(PartialState.ProcessingPayment(false))
            sendEvent(WorkersPaymentEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun verifyPendingPayment(): Flow<PartialState> = flow {
        val state = uiState.value
        val ticket = state.pendingTicket ?: return@flow
        val paidItem = state.selectedPaymentItem ?: return@flow
        emit(PartialState.Verifying(true))
        try {
            val message = inspectWorkersPaymentTicketUseCase(ticket, state.pendingPaymentInfo)
            emit(
                PartialState.PaymentVerified(
                    WorkersPaymentUiState.PaymentReceipt(
                        item = paidItem,
                        trackingCode = ticket,
                        message = message,
                    ),
                ),
            )
            emit(PartialState.Verifying(false))
        } catch (e: Exception) {
            emit(PartialState.Verifying(false))
            sendEvent(WorkersPaymentEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: WorkersPaymentUiState,
        partialState: PartialState,
    ): WorkersPaymentUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.ClearError -> currentState.copy(errorMessage = null)
        is PartialState.ListLoaded -> currentState.copy(
            isLoading = false,
            errorMessage = null,
            totalAmount = partialState.totalAmount,
            totalPenalty = partialState.totalPenalty,
            totalPremium = partialState.totalPremium,
            items = partialState.items,
        )

        is PartialState.ProcessingPayment -> currentState.copy(isProcessingPayment = partialState.inProgress)
        is PartialState.PaymentTicketReady -> currentState.copy(
            pendingTicket = partialState.ticket,
            pendingPaymentInfo = partialState.paymentInfo,
        )

        is PartialState.Verifying -> currentState.copy(isVerifying = partialState.inProgress)
        is PartialState.PaymentVerified -> currentState.copy(
            pendingTicket = null,
            pendingPaymentInfo = null,
            paymentReceipt = partialState.receipt,
        )

        is PartialState.ReceiptDismissed -> currentState.copy(
            paymentReceipt = null,
            selectedPaymentItem = null,
        )

        is PartialState.PaymentScreenOpened -> currentState.copy(selectedPaymentItem = partialState.item)
        is PartialState.PaymentScreenClosed -> currentState.copy(selectedPaymentItem = null)

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            errorMessage = partialState.message.ifBlank { null },
        )
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(WorkersPaymentEvent.ShowToast(message))
        return PartialState.Loading(false)
    }
}
