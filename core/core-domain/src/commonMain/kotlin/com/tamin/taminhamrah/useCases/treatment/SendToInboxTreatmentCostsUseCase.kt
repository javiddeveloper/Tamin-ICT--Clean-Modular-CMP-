package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class SendToInboxTreatmentCostsUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(repId: String): Flow<String> =
        repository.sendToInboxTreatmentCosts(repId)
}
