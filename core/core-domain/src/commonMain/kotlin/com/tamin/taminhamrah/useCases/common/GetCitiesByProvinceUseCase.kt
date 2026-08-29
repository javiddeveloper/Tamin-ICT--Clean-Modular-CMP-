package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

/** Offline-first (cache-then-network), keyed by province code. For network-only search by city name, use [GetCitiesUseCase] instead. */
class GetCitiesByProvinceUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(provinceCode: String): Flow<CityListResultDN> =
        repository.getCitiesByProvince(provinceCode)
}
