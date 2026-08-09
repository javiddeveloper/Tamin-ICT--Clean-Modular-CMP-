package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetPatientSelfDeclarativeUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(natCode: String, patientID: Int): Flow<PatientSelfDeclarativeDN> =
        repository.getPatientSelfDeclarative(natCode, patientID)
}
