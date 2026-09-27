package com.tamin.taminhamrah.feature.retirementPension.fake

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCityProvinceRepository : CityProvinceRepository {
    override fun getCity(cityId: String): Flow<CityDN> = flow {
        emit(CityDN(cityCode = cityId, cityName = "تهران", provinceCode = "01"))
    }
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {
        emit(ProvinceDN(provinceCode = provinceId, provinceName = "تهران", status = null, statusStartDate = null))
    }
    override fun getProvincesPage(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = flow { emit(PageDN(emptyList())) }
    override fun getCitiesPage(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flow { emit(PageDN(emptyList())) }
    override fun getCitiesByProvincePage(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>> =
        flow { emit(PageDN(emptyList())) }
}
