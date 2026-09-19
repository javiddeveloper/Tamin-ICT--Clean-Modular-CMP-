package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

class GetLegalRepresentativeWorkshopContractsUseCase(private val repository: WorkShopsRepository) {
    operator fun invoke(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeContractListDN?> {
        return repository.getLegalRepresentativeWorkshopContracts(workshopId, branchCode)
    }
}
