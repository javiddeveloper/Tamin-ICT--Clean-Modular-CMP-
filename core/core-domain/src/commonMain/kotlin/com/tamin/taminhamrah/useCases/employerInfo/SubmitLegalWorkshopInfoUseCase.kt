package com.tamin.taminhamrah.useCases.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopInfoRequestDN
import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import kotlinx.coroutines.flow.Flow

class SubmitLegalWorkshopInfoUseCase(
    private val repository: EmployerInfoRepository,
) {
    operator fun invoke(request: LegalWorkshopInfoRequestDN): Flow<String> =
        repository.submitLegalWorkshopInfo(request)
}
