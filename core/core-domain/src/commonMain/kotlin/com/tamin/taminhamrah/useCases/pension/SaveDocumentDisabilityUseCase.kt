package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class SaveDocumentDisabilityUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(requestId: Long, body: DisabilitySaveDocumentDN): Flow<String?> {
        return pensionRepository.saveDocumentDisability(requestId, body)
    }
}
