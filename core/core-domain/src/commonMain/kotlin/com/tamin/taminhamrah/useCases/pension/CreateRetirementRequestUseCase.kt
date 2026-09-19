package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class CreateRetirementRequestUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        authenticationsCode: Long,
        form: RetirementRequestFormDN
    ): Flow<RetirementRequestCreatedDN> {
        return pensionRepository.createRetirementRequest(authenticationsCode, form)
    }
}
