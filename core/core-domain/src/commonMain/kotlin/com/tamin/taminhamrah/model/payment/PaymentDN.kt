package com.tamin.taminhamrah.model.payment

import kotlinx.serialization.Serializable

/**
 * Names the extra check a feature needs run after its payment returns from the gateway.
 *
 * The gateway itself only ever knows whether money moved. Whether the *debt* is now settled is a
 * question each service answers on its own endpoint, and the answer has to be obtainable from a
 * cold start — the user comes back from a browser, and the screen that started the payment may be
 * gone. So the key travels in the navigation route rather than living in a captured lambda.
 *
 * Add a value here, implement a [com.tamin.taminhamrah.repository.payment.PaymentVerifier] for it
 * in the owning feature, and register that verifier in the feature's Koin module.
 */
@Serializable
enum class PaymentVerifierKey {
    /** The gateway's own answer is the whole answer; no service-side confirmation to run. */
    NONE,

    /** `debit-installment-payment/pay-check/{debtSerialNumber}`; reference = debt serial number. */
    WORKSHOP_DEBIT_INSTALLMENT,

    /** `sep/online-payment-widthout-back?systemType=`; reference = the feature's system type. */
    SPECIAL_INSURED,

    /** `workers/inpectTicket`; reference = the payment-info token handed back by `payDebit`. */
    CONSTRUCTION_WORKERS,

    /**
     * Debug only: always refuses to confirm.
     *
     * Answered by the verifier `:feature:payment` registers for the payment sandbox, so the
     * "paid but not confirmed" branch of the result screen can be seen without waiting for a real
     * service to fail. No production code ever sends this key.
     */
    MOCK_UNCONFIRMED,
}

/**
 * Everything the shared payment flow needs from the feature that started it.
 *
 * A feature calls its own `pay*` endpoint — the request shapes have nothing in common, which is why
 * they stay in each feature — and hands the resulting ticket here. From this point the flow is
 * identical for every feature in the app.
 */
data class PaymentRequestDN(
    /** The gateway ticket returned by the feature's own pay endpoint. */
    val ticket: String,
    /** Which post-payment confirmation to run, and what to run it with. */
    val verifierKey: PaymentVerifierKey = PaymentVerifierKey.NONE,
    /** The one value [verifierKey]'s verifier needs. Empty for [PaymentVerifierKey.NONE]. */
    val verifierReference: String = "",
)

/**
 * What the gateway says about a ticket, before and after it is paid.
 *
 * [millisToExpire] is the gateway's own countdown rather than something derived from the device
 * clock, which can be wrong by more than the ticket's whole lifetime.
 *
 * [amount] is what the ticket asks for and [settledAmount] what was actually taken; before payment
 * the second is zero, which is exactly how the old client decided which of the two labels to show.
 */
data class PaymentPreviewDN(
    val ticket: String = "",
    val paymentId: String = "",
    val amount: Long = 0L,
    val settledAmount: Long = 0L,
    val description: String = "",
    val millisToExpire: Long = 0L,
    val status: PaymentStatus = PaymentStatus.UNKNOWN,
    /** Bank reference number, shown on the receipt once the payment succeeds. */
    val referenceNumber: String = "",
    /** Gateway trace number, the other half of what a branch asks for when tracing a payment. */
    val traceNumber: String = "",
    /** The gateway's own wording about the transaction. May be blank. */
    val message: String = "",
) {
    /** A ticket can only be paid while the gateway still calls it unpaid and time remains. */
    val isPayable: Boolean get() = status.isPayable && millisToExpire > 0L
}

/**
 * How far along the gateway considers a ticket to be.
 *
 * [code] is the gateway's own string; the entries are matched to it rather than to an ordinal so
 * that reordering them cannot change what a response is read as.
 */
enum class PaymentStatus(val code: String) {
    /** Issued and waiting to be paid. */
    NOT_PAID("NOT_PAYED"),

    /** Money moved and the gateway is still settling it with the bank. */
    VERIFYING("VERIFYING"),

    /** Money moved and the gateway has settled it. */
    SUCCESSFUL("SUCCESSFUL"),

    /** The ticket ran out of time before it was paid. */
    EXPIRED("EXPIRED"),

    /** The gateway finished and money did not move. */
    FAILED("FAILED"),

    /** The gateway sent a status this app does not know, or none at all. */
    UNKNOWN("UNKNOWN");

    /**
     * Whether the user's money has left their account.
     *
     * [VERIFYING] counts: the payment has gone through and only the gateway's own bookkeeping is
     * outstanding. The old client treated it as success for the same reason, and showing "failed"
     * here would send a user who has already paid back to pay a second time.
     */
    val isSettled: Boolean get() = this == SUCCESSFUL || this == VERIFYING

    /** Only an unpaid ticket may be sent to the gateway. */
    val isPayable: Boolean get() = this == NOT_PAID

    companion object {
        fun fromCode(code: String?): PaymentStatus =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: UNKNOWN
    }
}

/**
 * The address the user is sent to in order to pay.
 *
 * A refusal arrives here as a *successful* call with [succeeded] false and a reason — the gateway
 * declining a payer is normal business, not a transport failure, so it is modelled rather than
 * thrown.
 */
data class PaymentLinkDN(
    val succeeded: Boolean = false,
    val paymentUrl: String = "",
    val message: String = "",
) {
    val isOpenable: Boolean get() = succeeded && paymentUrl.isNotBlank()
}

/**
 * The final word on one payment: what the gateway saw, plus what the owning service confirmed.
 *
 * The two can disagree — money can move while the service-side confirmation fails — and the user
 * has to be told that plainly rather than shown a green tick, so both halves are kept.
 */
data class PaymentOutcomeDN(
    val preview: PaymentPreviewDN = PaymentPreviewDN(),
    val verification: PaymentVerificationDN = PaymentVerificationDN.NotRequired,
) {
    /** Fully settled: the gateway took the money and the service agreed the debt is closed. */
    val isFullySuccessful: Boolean
        get() = preview.status.isSettled && verification !is PaymentVerificationDN.Failed

    /**
     * The money moved but the owning service would not confirm it.
     *
     * Worth separating from an outright failure: the user has paid, so telling them to pay again
     * is wrong — they need the reference number and a branch, not the payment button.
     */
    val isPaidButUnconfirmed: Boolean
        get() = preview.status.isSettled && verification is PaymentVerificationDN.Failed
}

/** The result of the service-side confirmation named by [PaymentVerifierKey]. */
sealed interface PaymentVerificationDN {
    /** The feature asked for no confirmation beyond the gateway's own answer. */
    data object NotRequired : PaymentVerificationDN

    /** Still running, or not run yet. */
    data object Pending : PaymentVerificationDN

    /** The service confirmed. [message] is its own wording, when it sent any. */
    data class Confirmed(val message: String = "") : PaymentVerificationDN

    /** The service did not confirm. [message] is why, in the service's own words. */
    data class Failed(val message: String = "") : PaymentVerificationDN
}

/**
 * Whether the payment gateway is being stood in for, and with which answer.
 *
 * The gateway is frequently unavailable to developers — the test bank is down, the ticket service
 * refuses non-production traffic — and a payment screen that cannot be opened cannot be built.
 * Selecting a mode here swaps the gateway data source for a fake that answers the whole flow.
 *
 * Debug builds only: [com.tamin.taminhamrah.repository.DeveloperOptionsRepository] returns
 * [DISABLED] in a release build no matter what is stored.
 */
enum class PaymentMockMode {
    /** Talk to the real gateway. */
    DISABLED,

    /** Every step answers as though the payment went through. */
    SUCCESS,

    /** The ticket previews normally, and the payment then fails at the gateway. */
    FAILURE,
}
