package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface CityProvinceRepository {
    fun getCity(cityId: String): Flow<CityDN>
    fun getProvince(provinceId: String): Flow<ProvinceDN>
    fun getProvincesPage(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>>
    fun getCitiesPage(query: ApiQueryParamDN): Flow<PageDN<CityDN>>
    fun getCitiesByProvincePage(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>>
}
