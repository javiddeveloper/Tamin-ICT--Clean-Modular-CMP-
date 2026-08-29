package com.tamin.taminhamrah.useCases.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import kotlinx.coroutines.flow.Flow

class GetLegalWorkshopUseCase(
    private val repository: EmployerInfoRepository,
) {
    operator fun invoke(legalWorkshopId: String): Flow<LegalWorkshopDN> =
        repository.getLegalWorkshop(legalWorkshopId)
}
