package com.tamin.taminhamrah.useCases.employerInfo

import com.tamin.taminhamrah.model.employerInfo.RealWorkshopInfoRequestDN
import com.tamin.taminhamrah.repository.employerInfo.EmployerInfoRepository
import kotlinx.coroutines.flow.Flow

class SubmitRealWorkshopInfoUseCase(
    private val repository: EmployerInfoRepository,
) {
    operator fun invoke(request: RealWorkshopInfoRequestDN): Flow<String> =
        repository.submitRealWorkshopInfo(request)
}
