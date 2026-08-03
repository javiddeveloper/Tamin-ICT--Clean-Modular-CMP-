package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.ProvinceCityItemDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetProvinceCitiesUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(provinceID: Int): Flow<List<ProvinceCityItemDN>> =
        repository.getProvinceCities(provinceID)
}
