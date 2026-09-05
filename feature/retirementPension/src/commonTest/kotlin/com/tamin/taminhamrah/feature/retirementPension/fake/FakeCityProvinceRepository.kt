package com.tamin.taminhamrah.feature.retirementPension.fake

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.ProvinceDN
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
    override fun getProvinces(): Flow<List<ProvinceDN>> = flow { emit(emptyList()) }
    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = flow { emit(emptyList()) }
    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = flow { emit(CityListResultDN(emptyList())) }
}
