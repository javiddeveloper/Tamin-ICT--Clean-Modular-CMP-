package com.tamin.taminhamrah.feature.historyobjection.fake

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Only [getProvinces]/[getCitiesByProvince] are exercised by the stepper — the rest is stubbed. */
class FakeCityProvinceRepository : CityProvinceRepository {
    var provincesResult: List<ProvinceDN> = emptyList()
    var citiesByProvinceResult: List<CityDN> = emptyList()
    /** Set to simulate the real cache-then-network shape: emitted first, before [citiesByProvinceResult]. */
    var staleCitiesByProvinceResult: List<CityDN>? = null
    var lastCitiesByProvinceCode: String? = null
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")

    override fun getCity(cityId: String): Flow<CityDN> = flow {}
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {}

    override fun getProvinces(): Flow<List<ProvinceDN>> = flow {
        if (shouldThrowError) throw error
        emit(provincesResult)
    }

    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = flow { emit(emptyList()) }

    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = flow {
        lastCitiesByProvinceCode = provinceCode
        staleCitiesByProvinceResult?.let { emit(CityListResultDN(it)) }
        if (shouldThrowError) throw error
        emit(CityListResultDN(citiesByProvinceResult))
    }
}
