package com.tamin.taminhamrah.feature.payment.ui.checkout.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentPreviewDN

@Immutable
data class PaymentCheckoutUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    /** The service's own wording when something failed. Blank means "failed, but said nothing" —
     *  the screen supplies its own copy for that, so no Persian text has to live in a ViewModel. */
    val error: String? = null,
    /** The ticket never arrived, which is a different message from a payment that was refused. */
    val isTicketMissing: Boolean = false,
    val preview: PaymentPreviewDN = PaymentPreviewDN(),
    val payerType: PayerType = PayerType.CURRENT_USER,
    /** What the user typed. Empty for [PayerType.CURRENT_USER], which needs nothing typed. */
    val payerIdentifier: String = "",
    /** Set once the user has typed enough for the identifier to be judged, so an empty field
     *  is not flagged as wrong the moment the payer type changes. */
    val identifierError: Boolean = false,
    /** The signed-in user's own national code, sent for [PayerType.CURRENT_USER]. */
    val currentUserNationalCode: String = "",
    val isExpired: Boolean = false,
    /** True once the ticket has been handed to the gateway and the browser opened. */
    val hasOpenedGateway: Boolean = false,
    val showCancelConfirmation: Boolean = false,
) {
    /** Seconds the gateway says are left, as the countdown needs them. */
    val remainingSeconds: Int get() = (preview.millisToExpire / 1000L).toInt()

    /**
     * [hasOpenedGateway] is part of the guard, not just [isSubmitting]: a ticket is spent the
     * moment it is handed over, and a second tap that slipped through while the browser was
     * opening would ask the gateway to pay the same debt twice.
     */
    val canSubmit: Boolean
        get() = !isLoading && !isSubmitting && !isExpired && !hasOpenedGateway &&
            preview.isPayable &&
            (!payerType.needsIdentifier || (payerIdentifier.isNotBlank() && !identifierError))

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data object TicketMissing : PartialState
        data class SetPreview(val preview: PaymentPreviewDN) : PartialState
        data class SetCurrentUserNationalCode(val nationalCode: String) : PartialState
        data class SetPayerType(val payerType: PayerType) : PartialState
        data class SetPayerIdentifier(val identifier: String, val hasError: Boolean) : PartialState
        data object Expired : PartialState
        data object GatewayOpened : PartialState
        data class ShowCancelConfirmation(val show: Boolean) : PartialState
    }
}

sealed interface PaymentCheckoutIntent {
    data class Load(val ticket: String) : PaymentCheckoutIntent
    data class PayerTypeSelected(val payerType: PayerType) : PaymentCheckoutIntent
    data class PayerIdentifierChanged(val identifier: String) : PaymentCheckoutIntent
    data object PayClicked : PaymentCheckoutIntent
    data object TimerFinished : PaymentCheckoutIntent
    /** The user asked to leave; the ticket is still live, so this only opens the confirmation. */
    data object BackRequested : PaymentCheckoutIntent
    data object CancelConfirmed : PaymentCheckoutIntent
    data object CancelDismissed : PaymentCheckoutIntent
    data object ErrorDismissed : PaymentCheckoutIntent
}

sealed interface PaymentCheckoutEvent {
    /** Hand the gateway page to the platform, then move to the result screen. */
    data class OpenGateway(val url: String) : PaymentCheckoutEvent

    /** The payment was abandoned or the ticket expired; there is nothing to come back to. */
    data object Cancelled : PaymentCheckoutEvent
}
