package com.tamin.taminhamrah.data.repository

import com.tamin.core.network.datasource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.core.model.common.CityDN
import com.tamin.taminhamrah.core.model.common.ProvinceDN
import com.tamin.taminhamrah.core.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

internal class CityProvinceRepositoryImpl(
    private val commonRemoteDataSource: CommonRemoteDataSource,
    private val cityProvinceDao: CityProvinceDao,
) : CityProvinceRepository {

    override fun getCity(query: ApiQueryParamDN): Flow<CityDN> = flow {
        val cityId = query.filters.firstOrNull()?.value ?: return@flow

        val localCity = cityProvinceDao.getCity(cityId).firstOrNull()
        if (localCity != null) {
            emit(localCity.toDomain())
        }

        try {
            val response = commonRemoteDataSource.getCityName(query)
            response.list.forEach { city ->
                cityProvinceDao.upsertCity(city.toEntity())
            }
            cityProvinceDao.getCity(cityId).firstOrNull()?.toDomain()?.let { emit(it) }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getProvince(query: ApiQueryParamDN): Flow<ProvinceDN> = flow {
        val provinceId = query.filters.firstOrNull()?.value ?: return@flow

        try {
            cityProvinceDao.getProvince(provinceId).firstOrNull()?.toDomain()?.let { emit(it) }
        } catch (e: Exception) {
            throw e
        }

        try {
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
