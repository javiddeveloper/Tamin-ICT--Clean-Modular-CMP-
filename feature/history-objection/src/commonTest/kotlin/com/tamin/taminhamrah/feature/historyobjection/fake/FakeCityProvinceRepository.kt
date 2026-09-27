package com.tamin.taminhamrah.feature.historyobjection.fake

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Only [getProvincesPage]/[getCitiesByProvincePage] are exercised by the stepper — the rest is stubbed. */
class FakeCityProvinceRepository : CityProvinceRepository {
    var provincesResult: List<ProvinceDN> = emptyList()
    var citiesByProvinceResult: List<CityDN> = emptyList()
    var lastCitiesByProvinceCode: String? = null
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")

    override fun getCity(cityId: String): Flow<CityDN> = flow {}
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {}

    override fun getProvincesPage(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = flow {
        if (shouldThrowError) throw error
        emit(PageDN(provincesResult))
    }

    override fun getCitiesPage(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flow { emit(PageDN(emptyList())) }

    override fun getCitiesByProvincePage(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flow {
        lastCitiesByProvinceCode = provinceCode
        if (shouldThrowError) throw error
        emit(PageDN(citiesByProvinceResult))
    }
}
