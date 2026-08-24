package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

class GetProvincesUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(): Flow<List<ProvinceDN>> = repository.getProvinces()
}
