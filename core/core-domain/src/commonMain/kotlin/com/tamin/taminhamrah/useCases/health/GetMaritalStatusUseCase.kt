package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.MaritalStatusDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetMaritalStatusUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<MaritalStatusDN>> =
        repository.getMaritalStatus()
}
