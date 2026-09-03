package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class FinalConfirmDisabilityRequestUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(requestId: Long, body: DisabilityFinalConfirmDN): Flow<String?> {
        return pensionRepository.finalConfirmDisabilityRequest(requestId, body)
    }
}
