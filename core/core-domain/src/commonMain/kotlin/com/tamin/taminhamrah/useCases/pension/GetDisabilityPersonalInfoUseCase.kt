package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetDisabilityPersonalInfoUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(): Flow<DisabilityPersonalInfoDN> {
        return pensionRepository.getDisabilityPersonalInfo()
    }
}
