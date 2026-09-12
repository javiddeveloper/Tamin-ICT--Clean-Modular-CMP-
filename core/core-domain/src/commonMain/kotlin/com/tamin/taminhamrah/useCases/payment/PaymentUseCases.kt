package com.tamin.taminhamrah.useCases.payment

import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentLinkDN
import com.tamin.taminhamrah.model.payment.PaymentOutcomeDN
import com.tamin.taminhamrah.model.payment.PaymentPreviewDN
import com.tamin.taminhamrah.model.payment.PaymentVerificationDN
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.repository.payment.PaymentGatewayRepository
import com.tamin.taminhamrah.repository.payment.PaymentVerifier

/** Reads the amount, reason and remaining validity of a ticket the user is about to pay. */
class GetPaymentPreviewUseCase(private val repository: PaymentGatewayRepository) {
    suspend operator fun invoke(ticket: String): PaymentPreviewDN = repository.getPreview(ticket)
}

/**
 * Turns a ticket into the page the user pays on.
 *
 * Refuses before the network when the payer identifier does not match the payer type: the gateway
 * answers a malformed identifier with a generic failure that reads, to the user, as though the
 * payment itself was rejected.
 */
class CreatePaymentLinkUseCase(private val repository: PaymentGatewayRepository) {
    suspend operator fun invoke(
        ticket: String,
        payerType: PayerType,
        payerIdentifier: String,
    ): PaymentLinkDN = repository.createPaymentLink(ticket, payerType, payerIdentifier)
}

/** Releases a ticket the user backed out of, so the same debt can be paid again immediately. */
class CancelPaymentUseCase(private val repository: PaymentGatewayRepository) {
    suspend operator fun invoke(ticket: String) = repository.cancelPayment(ticket)
}

/**
 * Settles what actually happened to a payment: what the gateway saw, and what the owning service
 * confirmed.
 *
 * Runs after the user comes back from the gateway, and is written to work from a cold start — the
 * ticket and the verifier key are all it needs, because the screen that began the payment may no
 * longer exist.
 *
 * [verifiers] is every [PaymentVerifier] registered across the app's Koin modules; a key with no
 * verifier resolves to [PaymentVerificationDN.NotRequired] rather than failing, so a feature whose
 * verifier has not been written yet still gets a working payment screen.
 */
class VerifyPaymentUseCase(
    private val repository: PaymentGatewayRepository,
    private val verifiers: List<PaymentVerifier>,
) {
    suspend operator fun invoke(
        ticket: String,
        verifierKey: PaymentVerifierKey,
        verifierReference: String,
    ): PaymentOutcomeDN {
        val preview = repository.getPreview(ticket)
        val verifier = verifiers.firstOrNull { it.key == verifierKey }
            ?.takeIf { verifierKey != PaymentVerifierKey.NONE }
            ?: return PaymentOutcomeDN(preview, PaymentVerificationDN.NotRequired)

        val verification = runCatching { verifier.verify(verifierReference) }
            .fold(
                onSuccess = { PaymentVerificationDN.Confirmed(it) },
                onFailure = { PaymentVerificationDN.Failed(it.message.orEmpty()) },
            )
        return PaymentOutcomeDN(preview, verification)
    }
}
