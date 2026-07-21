package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.PatientLabDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetPatientLabsUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(natCode: String, patientID: Int): Flow<List<PatientLabDN>> =
        repository.getPatientLabs(natCode, patientID)
}
