package com.tamin.taminhamrah.feature.taminServices.verifier

import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.repository.payment.PaymentVerifier
import com.tamin.taminhamrah.useCases.workersPayment.InspectWorkersPaymentTicketUseCase

/**
 * Post-payment confirmation for construction workers (حق‌بیمه کارگران ساختمانی) payments.
 *
 * Inspects payment ticket status using [InspectWorkersPaymentTicketUseCase].
 */
class ConstructionWorkersPaymentVerifier(
    private val inspectWorkersPaymentTicketUseCase: InspectWorkersPaymentTicketUseCase,
) : PaymentVerifier {

    override val key: PaymentVerifierKey = PaymentVerifierKey.CONSTRUCTION_WORKERS

    override suspend fun verify(reference: String): String {
        return inspectWorkersPaymentTicketUseCase(ticket = null, paymentInfo = reference)
    }
}
