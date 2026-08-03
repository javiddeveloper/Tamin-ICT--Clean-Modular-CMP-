package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.SmokingStatusDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetSmokingStatusUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<SmokingStatusDN>> =
        repository.getSmokingStatus()
}
