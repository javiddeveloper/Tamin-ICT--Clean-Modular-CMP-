package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.SyncDrugAllergiesRequest
import com.tamin.taminhamrah.model.health.SyncResultDN
import com.tamin.taminhamrah.repository.health.HealthRepository

class SyncDrugAllergiesUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(request: SyncDrugAllergiesRequest): SyncResultDN =
        repository.syncDrugAllergies(request)
}
