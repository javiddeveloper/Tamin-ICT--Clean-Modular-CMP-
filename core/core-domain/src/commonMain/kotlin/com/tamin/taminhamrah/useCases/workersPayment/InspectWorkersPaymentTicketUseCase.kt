package com.tamin.taminhamrah.useCases.workersPayment

import com.tamin.taminhamrah.repository.workersPayment.WorkersPaymentRepository

class InspectWorkersPaymentTicketUseCase(
    private val repository: WorkersPaymentRepository,
) {
    suspend operator fun invoke(ticket: String?, paymentInfo: String?): String =
        repository.inspectTicket(ticket, paymentInfo)
}
