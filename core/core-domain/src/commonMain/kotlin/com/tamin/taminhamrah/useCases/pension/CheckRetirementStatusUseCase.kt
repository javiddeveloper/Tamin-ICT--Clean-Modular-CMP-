package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class CheckRetirementStatusUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(): Flow<RetirementStatusDN> {
        return pensionRepository.checkRetirementStatus()
    }
}
