package com.tamin.taminhamrah.data.repository

import com.tamin.core.network.datasource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.RecipientDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.data.repository.city.CityListQuery
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

internal class CityProvinceRepositoryImpl(
    private val commonRemoteDataSource: CommonRemoteDataSource,
    private val cityProvinceDao: CityProvinceDao,
) : CityProvinceRepository {

    override fun getCity(cityId: String): Flow<CityDN> = flow {

        val localCity = cityProvinceDao.getCity(cityId).firstOrNull()
        if (localCity != null) {
            emit(localCity.toDomain())
        }

        try {
            val query = ApiQueryParamDN(
                filters = listOf(
                    ApiFilterDN(property = FilterProperty.CITY_CODE, operator = FilterOperator.EQUAL, value = cityId)
                )
            )
            val response = commonRemoteDataSource.getCityName(query)
            response.list.forEach { city ->
                cityProvinceDao.upsertCity(city.toEntity())
            }
            cityProvinceDao.getCity(cityId).firstOrNull()?.toDomain()?.let { emit(it) }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getCities(cityName: String?): Flow<List<CityDN>> = flow {
        val response = commonRemoteDataSource.getCityName(CityListQuery.build(cityName))
        response.list.forEach { cityDto ->
            cityProvinceDao.upsertCity(cityDto.toEntity())
        }
        emit(response.list.map { it.toDomain() })
    }

    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {

        try {
            cityProvinceDao.getProvince(provinceId).firstOrNull()?.toDomain()?.let { emit(it) }
        } catch (e: Exception) {
            throw e
        }

        try {
            val query = ApiQueryParamDN(
                filters = listOf(
                    ApiFilterDN(property = FilterProperty.PROVINCE_CODE, operator = FilterOperator.EQUAL, value = provinceId)
                )
            )
            val response = commonRemoteDataSource.getProvinceName(query)
            response.list.forEach { province ->
                cityProvinceDao.upsertProvince(province.toEntity())
            }
            cityProvinceDao.getProvince(provinceId).firstOrNull()?.toDomain()?.let { emit(it) }
        } catch (e: Exception) {
            throw e
        }
    }


}
