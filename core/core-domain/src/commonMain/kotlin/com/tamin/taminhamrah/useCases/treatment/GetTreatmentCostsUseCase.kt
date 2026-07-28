package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.TreatmentCostDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetTreatmentCostsUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(): Flow<List<TreatmentCostDN>> = repository.getTreatmentCosts()
}
