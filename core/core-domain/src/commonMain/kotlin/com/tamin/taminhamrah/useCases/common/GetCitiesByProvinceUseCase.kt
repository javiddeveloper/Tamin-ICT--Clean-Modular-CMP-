package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

class GetCitiesByProvinceUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(provinceCode: String): Flow<List<CityDN>> =
        repository.getCitiesByProvince(provinceCode)
}
