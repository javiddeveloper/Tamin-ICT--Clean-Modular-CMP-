package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.ActFrequencyDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetActFrequenciesUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<ActFrequencyDN>> =
        repository.getActFrequencies()
}
