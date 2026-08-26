package com.tamin.taminhamrah.feature.taminServices.workersPayment.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class WorkersPaymentUiState(
    val isLoading: Boolean = false,
    val isProcessingPayment: Boolean = false,
    val isVerifying: Boolean = false,
    val totalAmount: Long = 0L,
    val totalPenalty: Long = 0L,
    val totalPremium: Long = 0L,
    val items: ImmutableList<WorkersPaymentInfoPR> = persistentListOf(),
    /** Non-null once the user taps "پرداخت" on a card — the Route swaps to screen 2 for that item. */
    val selectedPaymentItem: WorkersPaymentInfoPR? = null,
    /** Ticket + paymentInfo from the last `payDebit`, awaiting the gateway callback. */
    val pendingTicket: String? = null,
    val pendingPaymentInfo: String? = null,
    /** Non-null once `inspectTicket` confirms a payment — screen 2 shows the success dialog. */
    val paymentReceipt: PaymentReceipt? = null,
    val errorMessage: String? = null,
) {
    val hasItems: Boolean get() = items.isNotEmpty()
    val isEmptyResult: Boolean get() = !isLoading && errorMessage == null && items.isEmpty()
    val hasPendingPayment: Boolean get() = pendingTicket != null

    /** Cards the user can still pay — drives the header's debt total and the "N ماه" chip. */
    val payableItems: List<WorkersPaymentInfoPR>
        get() = items.filter { it.status == WorkersPaymentInfoPR.Status.PAYABLE }

    /** Sum actually owed right now (premium + penalty of every still-payable card). */
    val payableTotal: Long get() = payableItems.sumOf { it.totalPayable }

    /** Everything screen 2's success dialog needs, captured at verify-time. */
    @Immutable
    data class PaymentReceipt(
        val item: WorkersPaymentInfoPR,
        val trackingCode: String,
        val message: String,
    )

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data object ClearError : PartialState
        data class ListLoaded(
            val totalAmount: Long,
            val totalPenalty: Long,
            val totalPremium: Long,
            val items: ImmutableList<WorkersPaymentInfoPR>,
        ) : PartialState

        data class ProcessingPayment(val inProgress: Boolean) : PartialState
        data class PaymentTicketReady(val ticket: String?, val paymentInfo: String?) : PartialState
        data class Verifying(val inProgress: Boolean) : PartialState
        data class PaymentVerified(val receipt: PaymentReceipt) : PartialState
        data object ReceiptDismissed : PartialState
        data class PaymentScreenOpened(val item: WorkersPaymentInfoPR) : PartialState
        data object PaymentScreenClosed : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface WorkersPaymentIntent {
    data object LoadPaymentInfo : WorkersPaymentIntent
    data object Retry : WorkersPaymentIntent

    /** Card "پرداخت" tap → open screen 2 for [item]. */
    data class OpenPaymentScreen(val item: WorkersPaymentInfoPR) : WorkersPaymentIntent
    data object ClosePaymentScreen : WorkersPaymentIntent

    /** Screen 2 concerns — kept here so the single ViewModel owns the whole flow. */
    data class PayItem(val item: WorkersPaymentInfoPR) : WorkersPaymentIntent

    /** Fired when the app is resumed via the payment deep-link callback. */
    data object VerifyPendingPayment : WorkersPaymentIntent

    /** Success-dialog "متوجه شدم" tap — clears the receipt, returns to the list and refreshes it. */
    data object DismissReceipt : WorkersPaymentIntent
}

sealed interface WorkersPaymentEvent {
    data class OpenPaymentUrl(val url: String) : WorkersPaymentEvent
    data class ShowToast(val message: String) : WorkersPaymentEvent
    data object NavigateBack : WorkersPaymentEvent
}
