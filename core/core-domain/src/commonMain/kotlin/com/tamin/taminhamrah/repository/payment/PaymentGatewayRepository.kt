package com.tamin.taminhamrah.repository.payment

import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentLinkDN
import com.tamin.taminhamrah.model.payment.PaymentPreviewDN

/**
 * The four calls every payment in the app goes through, whatever produced the ticket.
 *
 * Everything before this — calculating the amount, listing the instalments, asking the service to
 * issue a ticket — belongs to the feature. Everything from the ticket onwards is the same for all
 * of them, and lives here.
 */
interface PaymentGatewayRepository {

    /** What the gateway knows about [ticket]: amount, reason, remaining validity, status. */
    suspend fun getPreview(ticket: String): PaymentPreviewDN

    /**
     * Asks the gateway for the page the user pays on.
     *
     * [payerIdentifier] is the national code / national ID / foreign-national code the user typed,
     * or the signed-in user's own national code for [PayerType.CURRENT_USER].
     */
    suspend fun createPaymentLink(
        ticket: String,
        payerType: PayerType,
        payerIdentifier: String,
    ): PaymentLinkDN

    /**
     * Releases [ticket] when the user backs out or lets it expire.
     *
     * The old client left an abandoned ticket open whenever cancelling failed, which blocked the
     * next attempt on the same debt until it timed out server-side — so failures here are reported
     * rather than swallowed, and the caller decides whether the user needs to know.
     */
    suspend fun cancelPayment(ticket: String)
}

/**
 * The service-side confirmation one feature needs after its payment returns from the gateway.
 *
 * Implemented in the feature that owns the debt and registered in that feature's Koin module, so
 * the shared payment flow never has to know which features exist.
 */
interface PaymentVerifier {

    /** Which [com.tamin.taminhamrah.model.payment.PaymentVerifierKey] this verifier answers for. */
    val key: com.tamin.taminhamrah.model.payment.PaymentVerifierKey

    /**
     * Confirms the payment with the owning service.
     *
     * [reference] is whatever that service needs — a debt serial number, a system type, a payment
     * token. Returns the service's own message on success; throws on failure, which the caller
     * turns into [com.tamin.taminhamrah.model.payment.PaymentVerificationDN.Failed].
     */
    suspend fun verify(reference: String): String
}
