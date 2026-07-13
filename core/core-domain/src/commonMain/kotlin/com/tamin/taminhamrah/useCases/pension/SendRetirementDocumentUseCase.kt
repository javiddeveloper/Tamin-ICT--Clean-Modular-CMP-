package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class SendRetirementDocumentUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(
        requestId: String,
        request: RetirementSaveDocumentDN
    ): Flow<String?> {
        return pensionRepository.sendRetirementDocument(requestId, request)
    }
}
