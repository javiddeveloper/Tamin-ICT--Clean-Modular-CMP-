package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.UpdateSelfDeclarativeRequest
import com.tamin.taminhamrah.repository.health.HealthRepository

class UpdateSelfDeclarativeUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(request: UpdateSelfDeclarativeRequest): UpdateSelfDeclarativeDN =
        repository.updateSelfDeclarative(request)
}
