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
import com.tamin.taminhamrah.useCases.workersPayment.WorkersPaymentCallbackNotifier
import com.tamin.taminhamrah.util.NetworkConstants
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.workers_payment_url_missing

class WorkersPaymentViewModel(
    private val getWorkersPaymentInfoUseCase: GetWorkersPaymentInfoUseCase,
    private val payWorkersDebitUseCase: PayWorkersDebitUseCase,
    private val inspectWorkersPaymentTicketUseCase: InspectWorkersPaymentTicketUseCase,
    private val callbackNotifier: WorkersPaymentCallbackNotifier,
) : BaseViewModel<WorkersPaymentUiState, PartialState, WorkersPaymentEvent, WorkersPaymentIntent>(
    initialState = WorkersPaymentUiState(),
) {

    /**
     * Set synchronously in [handleIntent] so the two verification triggers (the gateway-callback
     * notifier and `WorkersPaymentRoute`'s lifecycle-RESUMED effect) can't both start an
     * `inspectTicket` call for the same ticket. `handleIntent` is invoked sequentially by
     * `BaseViewModel`, so a plain flag is enough — no atomics needed.
     */
    private var verificationInFlight = false

    init {
        sendIntent(WorkersPaymentIntent.LoadPaymentInfo)
        observeGatewayCallback()
    }

    /**
     * The payment gateway redirects to `mytamin://workers_payment_callback` when the user finishes.
     * The platform deep-link entry points forward that to [callbackNotifier]; when it fires and a
     * ticket is still pending, verify it. `WorkersPaymentRoute` also verifies on lifecycle RESUMED,
     * which covers a manual return (deep link blocked, or not wired on the platform) — the
     * [verificationInFlight] guard keeps the two paths from double-calling `inspectTicket`.
     */
    private fun observeGatewayCallback() {
        viewModelScope.launch {
            callbackNotifier.callbacks.collect {
                if (uiState.value.hasPendingPayment) {
                    sendIntent(WorkersPaymentIntent.VerifyPendingPayment)
                }
            }
        }
    }

    override fun handleIntent(intent: WorkersPaymentIntent): Flow<PartialState> = when (intent) {
        is WorkersPaymentIntent.LoadPaymentInfo -> loadPaymentInfo()
        is WorkersPaymentIntent.Retry -> loadPaymentInfo()
        is WorkersPaymentIntent.OpenPaymentScreen -> flow { emit(PartialState.PaymentScreenOpened(intent.item)) }
        is WorkersPaymentIntent.ClosePaymentScreen -> flow { emit(PartialState.PaymentScreenClosed) }
        is WorkersPaymentIntent.PayItem -> payItem(intent.item)
        is WorkersPaymentIntent.VerifyPendingPayment -> {
            if (verificationInFlight || uiState.value.pendingTicket == null) {
                emptyFlow()
            } else {
                verificationInFlight = true
                verifyPendingPayment()
            }
        }

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
            // Match the legacy flow: a non-200 from inpectTicket on the gateway callback (user
            // cancelled, TFH 500, …) is swallowed silently — no toast, no dialog. Only a
            // successful verification does anything user-visible. Legacy routes the failed result
            // to MainViewModel.mldErrorState, which no Activity-level observer listens to.
            // One-shot, like legacy (which wipes ticket+paymentInfo before every attempt): drop the
            // pending ticket so a later RESUMED / callback can't re-run inpectTicket in a loop.
            emit(PartialState.PendingPaymentCleared)
            // Verification failed → step back off the confirmation screen to the debt list.
            emit(PartialState.PaymentScreenClosed)
        }
    }.onCompletion { verificationInFlight = false }

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

        is PartialState.PendingPaymentCleared -> currentState.copy(
            isVerifying = false,
            pendingTicket = null,
            pendingPaymentInfo = null,
        )

        is PartialState.ReceiptDismissed -> currentState.copy(
            paymentReceipt = null,
            selectedPaymentItem = null,
        )

        // Clear any error on every screen 1 <-> screen 2 transition so an error raised on one
        // screen can never straddle the navigation and reappear on the other.
        is PartialState.PaymentScreenOpened -> currentState.copy(
            selectedPaymentItem = partialState.item,
            errorMessage = null,
        )

        is PartialState.PaymentScreenClosed -> currentState.copy(
            selectedPaymentItem = null,
            errorMessage = null,
        )

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
