package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

class GetCitiesUseCase(
    private val cityProvinceRepository: CityProvinceRepository,
) {
    operator fun invoke(
        cityName: String? = null,
        provinceCode: String? = null,
    ): Flow<List<CityDN>> = cityProvinceRepository.getCities(cityName, provinceCode)
}
