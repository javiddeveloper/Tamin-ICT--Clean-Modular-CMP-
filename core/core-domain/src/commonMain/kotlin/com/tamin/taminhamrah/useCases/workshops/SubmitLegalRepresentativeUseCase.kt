package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class SubmitLegalRepresentativeUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(ticket: String, request: LegalRepresentativeRequestDN) {
        repository.submitLegalRepresentative(ticket, request)
    }
}
