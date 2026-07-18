package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetDeservedTreatmentUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(nationalCode: String): Flow<List<DeservedTreatmentDN>> =
        repository.getDeservedTreatment(nationalCode)
}
