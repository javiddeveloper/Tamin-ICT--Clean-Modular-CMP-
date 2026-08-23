package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.data.repository.city.CityListQuery
import com.tamin.taminhamrah.data.repository.city.ProvinceListQuery
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.data.repository.city.CityByProvinceQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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

    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = flow {
        val response = commonRemoteDataSource.getCityName(CityListQuery.build(cityName))
        response.list.forEach { cityDto ->
            cityProvinceDao.upsertCity(cityDto.toEntity())
        }
        val cities = response.list.map { it.toDomain() }
        emit(
            if (provinceCode.isNullOrBlank()) {
                cities
            } else {
                cities.filter { it.matchesProvinceCode(provinceCode) }
            },
        )
    }

    override fun getCitiesByProvince(provinceCode: String): Flow<List<CityDN>> = flow {
        val localCities = cityProvinceDao.getCitiesByProvinceCode(provinceCode).first()
        emit(localCities.map { it.toDomain() })

        try {
            val response = commonRemoteDataSource.getCitiesByProvince(CityByProvinceQuery.build(provinceCode))
            cityProvinceDao.replaceCitiesForProvince(provinceCode, response.list.map { it.toEntity() })
        } catch (e: Exception) {
            if (localCities.isEmpty()) throw e
        }

        emitAll(
            cityProvinceDao.getCitiesByProvinceCode(provinceCode).map { entities ->
                entities.map { it.toDomain() }
            },
        )
    }.distinctUntilChanged()

    private fun CityDN.matchesProvinceCode(selectedProvinceCode: String): Boolean {
        val cityProvinceCode = provinceCode ?: return false
        if (cityProvinceCode == selectedProvinceCode) return true
        return cityProvinceCode.trimStart('0') == selectedProvinceCode.trimStart('0')
    }

    override fun getProvinces(): Flow<List<ProvinceDN>> = flow {
        val localProvinces = cityProvinceDao.getAllProvinces().first()
        emit(localProvinces.map { it.toDomain() })

        try {
            val response = commonRemoteDataSource.getProvinceName(ProvinceListQuery.build())
            cityProvinceDao.replaceAllProvinces(response.list.map { it.toEntity() })
        } catch (e: Exception) {
            if (localProvinces.isEmpty()) throw e
        }

        emitAll(
            cityProvinceDao.getAllProvinces().map { entities ->
                entities.map { it.toDomain() }
            },
        )
    }.distinctUntilChanged()

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
