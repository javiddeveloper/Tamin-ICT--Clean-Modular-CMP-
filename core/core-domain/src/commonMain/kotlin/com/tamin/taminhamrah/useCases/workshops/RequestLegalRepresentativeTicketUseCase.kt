package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.repository.WorkShopsRepository

class RequestLegalRepresentativeTicketUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(nationalCode: String? = null) {
        repository.requestLegalRepresentativeTicket(nationalCode)
    }
}
