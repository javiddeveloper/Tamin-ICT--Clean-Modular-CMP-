package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import kotlinx.coroutines.flow.Flow

interface CityProvinceRepository {
    fun getCity(cityId: String): Flow<CityDN>
    fun getProvince(provinceId: String): Flow<ProvinceDN>
}
