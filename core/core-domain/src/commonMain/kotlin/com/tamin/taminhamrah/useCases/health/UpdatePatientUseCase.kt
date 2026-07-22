package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.UpdatePatientDN
import com.tamin.taminhamrah.model.health.UpdatePatientRequest
import com.tamin.taminhamrah.repository.health.HealthRepository

class UpdatePatientUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(request: UpdatePatientRequest): UpdatePatientDN =
        repository.updatePatient(request)
}
