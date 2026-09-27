package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    /**
     * Offline-first: emits this page's cached slice (if any), then the network page. Collect the
     * whole flow (`Paginator(loadPages = …)`, `.collect`, `.last()`), not `.first()`.
     *
     * Each page is served from Room by the same offset/limit + filter the server gets. A fresh
     * first page from the network replaces that filter's whole cached list (stale rows are
     * dropped); later pages are appended.
     */
    override fun getCitiesPage(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flow {
        val localFilter = query.toLocalCityFilter()
        val localCities = if (localFilter != null) {
            cityProvinceDao.getCitiesSlice(
                cityName = localFilter.cityName,
                provinceCode = localFilter.provinceCode,
                limit = query.limit,
                offset = query.start,
            )
        } else {
            emptyList()
        }
        if (localCities.isNotEmpty()) {
            emit(PageDN(items = localCities.map { it.toDomain() }, isFromCache = true))
        }

        val response = try {
            commonRemoteDataSource.getCityName(query)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (localCities.isEmpty()) throw e
            return@flow
        }
        cacheCities(localFilter, query, response.list.map { it.toEntity() })
        emit(PageDN(items = response.list.map { it.toDomain() }, total = response.total))
    }

    /** Offline-first, same contract as [getCitiesPage]; the cache is scoped to [provinceCode]. */
    override fun getCitiesByProvincePage(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>> = flow {
        // The only filter this endpoint gets is the province (CityByProvinceQuery); anything else
        // the local query can't reproduce, so it skips the cache.
        val cacheable = query.sorts.isEmpty() &&
            query.filters.all { it.property == FilterProperty.PROVINCE_CODE_CITY }
        val localFilter = if (cacheable) LocalCityFilter(cityName = null, provinceCode = provinceCode) else null
        val localCities = if (localFilter != null) {
            cityProvinceDao.getCitiesSlice(
                cityName = localFilter.cityName,
                provinceCode = localFilter.provinceCode,
                limit = query.limit,
                offset = query.start,
            )
        } else {
            emptyList()
        }
        if (localCities.isNotEmpty()) {
            emit(PageDN(items = localCities.map { it.toDomain() }, isFromCache = true))
        }

        val response = try {
            commonRemoteDataSource.getCitiesByProvince(query)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (localCities.isEmpty()) throw e
            return@flow
        }
        cacheCities(localFilter, query, response.list.map { it.toEntity() })
        emit(PageDN(items = response.list.map { it.toDomain() }, total = response.total))
    }

    /**
     * Offline-first, same contract as [getCitiesPage]. Only the plain province list is cached;
     * a filtered/sorted request goes straight to the network. The table is ~31 rows, so the
     * page is sliced in memory from the existing `getAllProvinces()` query.
     */
    override fun getProvincesPage(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = flow {
        val cacheable = query.filters.isEmpty() && query.sorts.isEmpty()
        val localProvinces = if (cacheable) {
            cityProvinceDao.getAllProvinces().first().drop(query.start).take(query.limit)
        } else {
            emptyList()
        }
        if (localProvinces.isNotEmpty()) {
            emit(PageDN(items = localProvinces.map { it.toDomain() }, isFromCache = true))
        }

        val response = try {
            commonRemoteDataSource.getProvinceName(query)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (localProvinces.isEmpty()) throw e
            return@flow
        }
        val entities = response.list.map { it.toEntity() }
        if (cacheable && query.start == 0) {
            // Fresh first page replaces the whole cached province list.
            cityProvinceDao.replaceAllProvinces(entities)
        } else {
            cityProvinceDao.upsertProvinces(entities)
        }
        emit(PageDN(items = response.list.map { it.toDomain() }, total = response.total))
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

    private data class LocalCityFilter(val cityName: String?, val provinceCode: String?)

    /**
     * First page of a cacheable list → replace every cached row of that list (same filter);
     * later pages → append. A request the cache can't scope ([filter] `null`) only upserts.
     */
    private suspend fun cacheCities(filter: LocalCityFilter?, query: ApiQueryParamDN, cities: List<CityEntity>) {
        if (filter != null && query.start == 0) {
            cityProvinceDao.replaceCitiesMatching(filter.cityName, filter.provinceCode, cities)
        } else {
            cityProvinceDao.upsertCities(cities)
        }
    }

    /**
     * Translates the server filters built by `CityListQuery` into the Room query's arguments.
     * Returns `null` (don't use the cache) for anything the local query can't reproduce, so the
     * cache never shows rows the server wouldn't have returned.
     */
    private fun ApiQueryParamDN.toLocalCityFilter(): LocalCityFilter? {
        if (sorts.isNotEmpty()) return null
        var cityName: String? = null
        var provinceCode: String? = null
        for (filter in filters) {
            when (filter.property) {
                // Server convention is LIKE `*term*`; the DAO adds its own `%` wildcards.
                FilterProperty.CITY_NAME -> cityName = filter.value.trim('*').takeIf { it.isNotBlank() }
                FilterProperty.PROVINCE_CODE_CITY -> provinceCode = filter.value
                else -> return null
            }
        }
        return LocalCityFilter(cityName = cityName, provinceCode = provinceCode)
    }
}
