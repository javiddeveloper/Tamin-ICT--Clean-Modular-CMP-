package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.core.model.common.CityDN
import com.tamin.taminhamrah.core.model.common.ProvinceDN
import com.tamin.taminhamrah.core.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface CityProvinceRepository {
    fun getCity(query: ApiQueryParamDN): Flow<CityDN>
    fun getProvince(query: ApiQueryParamDN): Flow<ProvinceDN>
}
