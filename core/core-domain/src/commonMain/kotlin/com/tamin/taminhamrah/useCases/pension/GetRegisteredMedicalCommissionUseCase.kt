package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetRegisteredMedicalCommissionUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(filters: List<ApiFilterDN> = emptyList()): Flow<List<RegisteredMedicalCommissionDN>> {
        return pensionRepository.getRegisteredMedicalCommission(filters)
    }
}
