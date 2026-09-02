package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.repository.WorkShopsRepository

class DeleteLegalRepresentativeUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(ticket: String, stakeId: Long) {
        repository.deleteLegalRepresentative(ticket, stakeId)
    }
}
