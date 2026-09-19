package com.tamin.taminhamrah.feature.payment.ui.result.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.payment.PaymentOutcomeDN
import com.tamin.taminhamrah.model.payment.PaymentStatus
import com.tamin.taminhamrah.model.payment.PaymentVerificationDN
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey

@Immutable
data class PaymentResultUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val outcome: PaymentOutcomeDN = PaymentOutcomeDN(),
) {
    /**
     * Which of the four things the screen can say.
     *
     * Derived rather than stored so the gateway status and the service confirmation can never
     * drift apart from the headline shown above them.
     */
    val kind: PaymentResultKind
        get() = when {
            isLoading -> PaymentResultKind.CHECKING
            outcome.isPaidButUnconfirmed -> PaymentResultKind.PAID_UNCONFIRMED
            outcome.isFullySuccessful -> PaymentResultKind.SUCCESS
            outcome.preview.status == PaymentStatus.EXPIRED -> PaymentResultKind.EXPIRED
            else -> PaymentResultKind.FAILED
        }

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class SetOutcome(val outcome: PaymentOutcomeDN) : PartialState
    }
}

/** The four outcomes a user can be told about, in the order of how much they need to act on it. */
enum class PaymentResultKind {
    CHECKING,
    SUCCESS,

    /**
     * The money left the account but the owning service would not confirm the debt is settled.
     *
     * Deliberately not folded into [FAILED]: telling a user who has already paid to pay again is
     * the worst thing this screen can do. They need the reference number and a branch.
     */
    PAID_UNCONFIRMED,
    EXPIRED,
    FAILED,
}

sealed interface PaymentResultIntent {
    data class Check(
        val ticket: String,
        val verifierKey: PaymentVerifierKey,
        val verifierReference: String,
    ) : PaymentResultIntent

    /** Runs the same check again — the service side can settle a moment after the gateway does. */
    data object Recheck : PaymentResultIntent

    data object DoneClicked : PaymentResultIntent
    data object ErrorDismissed : PaymentResultIntent
}

sealed interface PaymentResultEvent {
    data object Finished : PaymentResultEvent
}

/** Convenience for the screen: the service's own wording, when it sent any. */
val PaymentVerificationDN.messageOrNull: String?
    get() = when (this) {
        is PaymentVerificationDN.Confirmed -> message.takeIf { it.isNotBlank() }
        is PaymentVerificationDN.Failed -> message.takeIf { it.isNotBlank() }
        PaymentVerificationDN.NotRequired, PaymentVerificationDN.Pending -> null
    }
