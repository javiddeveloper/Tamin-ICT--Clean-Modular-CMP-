package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.PatientVisitDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetPatientVisitsUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(natCode: String, patientID: Int): Flow<List<PatientVisitDN>> =
        repository.getPatientVisits(natCode, patientID)
}
