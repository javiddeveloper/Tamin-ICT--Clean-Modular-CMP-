package com.tamin.taminhamrah.useCases.health

import com.tamin.taminhamrah.model.health.ProvinceItemDN
import com.tamin.taminhamrah.repository.health.HealthRepository
import kotlinx.coroutines.flow.Flow

class GetAllProvincesUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(): Flow<List<ProvinceItemDN>> =
        repository.getAllProvinces()
}
