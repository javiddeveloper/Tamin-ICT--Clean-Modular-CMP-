package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterProperty
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

    override fun getProvincesPage(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = flow {
        if (shouldThrowError) throw error
        emit(PageDN(items = provincesResult, total = provincesResult.size))
    }

    override fun getCitiesPage(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flow {
        lastCitiesSearch = query.filters.valueOf(FilterProperty.CITY_NAME)
        lastProvinceCode = query.filters.valueOf(FilterProperty.PROVINCE_CODE_CITY)
        if (shouldThrowError) throw error
        val provinceCode = lastProvinceCode
        val cities = if (provinceCode.isNullOrBlank()) {
            citiesResult
        } else {
            citiesResult.filter { city ->
                city.provinceCode == provinceCode ||
                    city.provinceCode?.trimStart('0') == provinceCode.trimStart('0')
            }
        }
        emit(PageDN(items = cities, total = cities.size))
    }

    override fun getCitiesByProvincePage(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flow {
        lastCitiesByProvinceCode = provinceCode
        if (shouldThrowError) throw error
        emit(PageDN(items = citiesByProvinceResult, total = citiesByProvinceResult.size))
    }

    private fun List<ApiFilterDN>.valueOf(property: FilterProperty): String? =
        firstOrNull { it.property == property }?.value
}
