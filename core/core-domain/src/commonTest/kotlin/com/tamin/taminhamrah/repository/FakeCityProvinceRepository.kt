package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.core.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCityProvinceRepository : CityProvinceRepository {
    var cityResult: CityDN? = null
    var provinceResult: ProvinceDN? = null

    override fun getCity(query: ApiQueryParamDN): Flow<CityDN> = flow {
        cityResult?.let { emit(it) }
    }

    override fun getProvince(query: ApiQueryParamDN): Flow<ProvinceDN> = flow {
        provinceResult?.let { emit(it) }
    }
}
