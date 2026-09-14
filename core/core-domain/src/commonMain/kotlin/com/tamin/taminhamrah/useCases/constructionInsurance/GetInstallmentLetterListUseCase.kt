package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetInstallmentLetterListUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(workshopId: String, branchId: String): Flow<List<InstallmentLetterDN>> =
        repository.getInstallmentLetterList(workshopId, branchId)
}
