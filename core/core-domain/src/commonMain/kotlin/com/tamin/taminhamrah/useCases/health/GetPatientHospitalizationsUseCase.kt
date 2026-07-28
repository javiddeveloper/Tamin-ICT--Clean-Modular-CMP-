package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.PatientHospitalizationsDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetPatientHospitalizationsUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(natCode: String, patientID: Int): Flow<List<PatientHospitalizationsDN>> =
        repository.getPatientHospitalizations(natCode, patientID)
}
