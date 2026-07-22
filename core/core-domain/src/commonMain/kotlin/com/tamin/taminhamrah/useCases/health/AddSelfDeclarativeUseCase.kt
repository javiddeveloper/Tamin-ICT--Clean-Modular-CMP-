package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.AddSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.AddSelfDeclarativeRequest
import com.tamin.taminhamrah.repository.health.HealthRepository

class AddSelfDeclarativeUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(request: AddSelfDeclarativeRequest): AddSelfDeclarativeDN =
        repository.addSelfDeclarative(request)
}
