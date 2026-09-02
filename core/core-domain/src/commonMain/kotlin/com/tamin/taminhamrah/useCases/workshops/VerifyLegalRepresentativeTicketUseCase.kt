package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.repository.WorkShopsRepository

class VerifyLegalRepresentativeTicketUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(ticket: String) {
        repository.verifyLegalRepresentativeTicket(ticket)
    }
}
