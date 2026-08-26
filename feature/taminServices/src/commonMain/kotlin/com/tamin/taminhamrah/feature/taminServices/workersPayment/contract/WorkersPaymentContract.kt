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
    /** Ticket + paymentInfo from the last `payDebit`, awaiting the gateway callback. */
    val pendingTicket: String? = null,
    val pendingPaymentInfo: String? = null,
    val errorMessage: String? = null,
) {
    val hasItems: Boolean get() = items.isNotEmpty()
    val isEmptyResult: Boolean get() = !isLoading && errorMessage == null && items.isEmpty()
    val hasPendingPayment: Boolean get() = pendingTicket != null

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
        data object PaymentVerified : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface WorkersPaymentIntent {
    data object LoadPaymentInfo : WorkersPaymentIntent
    data object Retry : WorkersPaymentIntent
    data class PayItem(val item: WorkersPaymentInfoPR) : WorkersPaymentIntent

    /** Fired when the app is resumed via the payment deep-link callback. */
    data object VerifyPendingPayment : WorkersPaymentIntent
}

sealed interface WorkersPaymentEvent {
    data class OpenPaymentUrl(val url: String) : WorkersPaymentEvent
    data class ShowToast(val message: String) : WorkersPaymentEvent
    data class PaymentVerified(val message: String) : WorkersPaymentEvent
    data object NavigateBack : WorkersPaymentEvent
}
