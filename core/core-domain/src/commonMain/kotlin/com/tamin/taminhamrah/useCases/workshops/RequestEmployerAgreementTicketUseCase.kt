package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.repository.WorkShopsRepository

/** Step 1 — درخواست کد تایید for the new mobile/email the employer wants registered. */
class RequestEmployerAgreementTicketUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(mobile: String, email: String): String =
        repository.requestEmployerAgreementTicket(mobile, email)
}
