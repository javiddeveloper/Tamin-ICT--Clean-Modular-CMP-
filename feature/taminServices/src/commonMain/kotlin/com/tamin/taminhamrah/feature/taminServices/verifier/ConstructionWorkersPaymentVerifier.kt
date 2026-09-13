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
        val parts = reference.split("|", limit = 2)
        val (ticket, paymentInfo) = if (parts.size == 2) {
            parts[0].ifBlank { null } to parts[1].ifBlank { null }
        } else {
            null to reference.ifBlank { null }
        }
        return inspectWorkersPaymentTicketUseCase(ticket = ticket, paymentInfo = paymentInfo)
    }
}
