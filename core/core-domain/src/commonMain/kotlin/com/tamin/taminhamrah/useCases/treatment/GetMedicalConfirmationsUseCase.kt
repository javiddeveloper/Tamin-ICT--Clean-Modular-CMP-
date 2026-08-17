package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.treatment.MedicalConfirmationDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetMedicalConfirmationsUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(): Flow<List<MedicalConfirmationDN>> = repository.getMedicalConfirmations()
}
