package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCityProvinceRepository : CityProvinceRepository {
    var cityResult: CityDN? = null
    var provinceResult: ProvinceDN? = null
    var citiesResult: List<CityDN> = emptyList()
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var lastCitiesSearch: String? = null

    override fun getCity(cityId: String): Flow<CityDN> = flow {
        cityResult?.let { emit(it) }
    }

    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {
        provinceResult?.let { emit(it) }
    }

    override fun getCities(cityName: String?): Flow<List<CityDN>> = flow {
        lastCitiesSearch = cityName
        if (shouldThrowError) throw error
        emit(citiesResult)
    }
}
