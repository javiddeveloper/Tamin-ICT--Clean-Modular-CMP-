package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

class GetCitiesUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(cityName: String? = null, provinceCode: String? = null): Flow<List<CityDN>> =
        repository.getCities(cityName, provinceCode)
}
