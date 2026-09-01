package com.tamin.taminhamrah.useCases.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import kotlinx.coroutines.flow.Flow

class GetLegalWorkshopCeoUseCase(
    private val repository: EmployerInfoRepository,
) {
    operator fun invoke(nationalCode: String, birthDateMillis: Long): Flow<LegalWorkshopCeoDN> =
        repository.getLegalWorkshopCeo(nationalCode, birthDateMillis)
}
