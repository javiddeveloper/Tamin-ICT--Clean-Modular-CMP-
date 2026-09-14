package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetBeneficiariesWorkshopUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String? = null,
    ): Flow<List<BeneficiaryConstructionDN>> =
        repository.getBeneficiariesWorkshop(requestNumber, fileNumber, requestDate)
}
