package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.HealthMutationResult
import com.tamin.taminhamrah.model.health.SyncIllnessSelfDeclarativesRequest
import com.tamin.taminhamrah.model.health.SyncResultDN
import com.tamin.taminhamrah.repository.health.HealthRepository

class SyncIllnessSelfDeclarativesUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(request: SyncIllnessSelfDeclarativesRequest): HealthMutationResult<SyncResultDN> =
        repository.syncIllnessSelfDeclaratives(request)
}
