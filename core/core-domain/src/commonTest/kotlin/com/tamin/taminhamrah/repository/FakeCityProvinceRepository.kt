package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCityProvinceRepository : CityProvinceRepository {
    var cityResult: CityDN? = null
    var provinceResult: ProvinceDN? = null
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")

    override fun getCity(cityId: String): Flow<CityDN> = flow {
        cityResult?.let { emit(it) }
    }

    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {
        provinceResult?.let { emit(it) }
    }


}
