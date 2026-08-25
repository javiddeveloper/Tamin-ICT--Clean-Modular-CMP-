package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCityProvinceRepository : CityProvinceRepository {
    var cityResult: CityDN? = null
    var provinceResult: ProvinceDN? = null
    var citiesResult: List<CityDN> = emptyList()
    var provincesResult: List<ProvinceDN> = emptyList()
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var lastCitiesSearch: String? = null
    var lastProvinceCode: String? = null
    var citiesByProvinceResult: List<CityDN> = emptyList()
    var lastCitiesByProvinceCode: String? = null

    override fun getCity(cityId: String): Flow<CityDN> = flow {
        cityResult?.let { emit(it) }
    }

    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {
        provinceResult?.let { emit(it) }
    }

    override fun getProvinces(): Flow<List<ProvinceDN>> = flow {
        if (shouldThrowError) throw error
        emit(provincesResult)
    }

    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = flow {
        lastCitiesSearch = cityName
        lastProvinceCode = provinceCode
        if (shouldThrowError) throw error
        val cities = if (provinceCode.isNullOrBlank()) {
            citiesResult
        } else {
            citiesResult.filter { city ->
                city.provinceCode == provinceCode ||
                    city.provinceCode?.trimStart('0') == provinceCode.trimStart('0')
            }
        }
        emit(cities)
    }

    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = flow {
        lastCitiesByProvinceCode = provinceCode
        if (shouldThrowError) throw error
        emit(CityListResultDN(citiesByProvinceResult))
    }
}
