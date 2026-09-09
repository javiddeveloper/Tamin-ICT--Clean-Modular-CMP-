package com.tamin.taminhamrah.feature.payment.verifier

import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.repository.payment.PaymentVerifier

/**
 * Always refuses to confirm, so the payment sandbox can reach the one outcome no other scenario
 * produces: the gateway took the money and the owning service would not agree the debt is settled.
 *
 * That branch matters more than the ones around it — the user has already paid, so showing them
 * the payment button again would be wrong — and without this it could only be seen by waiting for
 * a real service to fail.
 *
 * Reachable only through [PaymentVerifierKey.MOCK_UNCONFIRMED], which no production code sends, so
 * registering it can never shadow a real feature's verifier.
 */
class UnconfirmedMockPaymentVerifier : PaymentVerifier {

    override val key: PaymentVerifierKey = PaymentVerifierKey.MOCK_UNCONFIRMED

    override suspend fun verify(reference: String): String =
        throw IllegalStateException(MOCK_REFUSAL)

    private companion object {
        // Simulated service response, shown only in a debug build; not app copy.
        const val MOCK_REFUSAL = "سرویس تأیید نکرد: بدهی همچنان باز است (Mock)"
    }
}
