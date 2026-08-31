package com.tamin.taminhamrah.model.workshop

/**
 * Outcome of asking to pay a debt online.
 *
 * A rejection is a *successful* call that says no, so it is modelled rather than thrown:
 * [succeeded] false with a [message] is the normal shape of "this debt cannot be paid".
 */
data class DebitPaymentDN(
    val succeeded: Boolean = false,
    val message: String = "",
    /** The full address of the payment page, already built from the ticket. Blank when refused. */
    val paymentPageUrl: String = "",
) {
    /** There is somewhere to send the user only when the service both agreed and gave a ticket. */
    val isPayable: Boolean get() = succeeded && paymentPageUrl.isNotBlank()
}

/** The pre-check the payment flow runs first; only `"1"` opens the way to the payment call. */
data class DebitPaymentPreCheckDN(
    val allowed: Boolean = false,
    val days: String = "",
)

/** Everything `pay-normal-debit` needs, gathered from the debt row and the workshop identity. */
data class DebitPaymentRequestDN(
    val workshopId: String,
    val branchCode: String,
    val debitNumber: String,
    val agreementRow: String,
    val deposit: Boolean = false,
)
