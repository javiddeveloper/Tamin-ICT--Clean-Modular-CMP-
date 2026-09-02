package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

class GetLegalRepresentativesUseCase(private val repository: WorkShopsRepository) {
    operator fun invoke(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeListDN?> {
        return repository.getLegalRepresentatives(workshopId, branchCode)
    }
}
