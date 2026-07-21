package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.PatientImagingDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetPatientImagingUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(natCode: String, patientID: Int): Flow<List<PatientImagingDN>> =
        repository.getPatientImaging(natCode, patientID)
}
