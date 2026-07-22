package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetPatientGeneralUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(natCode: String): Flow<PatientGeneralDN> =
        repository.getPatientGeneral(natCode)
}
