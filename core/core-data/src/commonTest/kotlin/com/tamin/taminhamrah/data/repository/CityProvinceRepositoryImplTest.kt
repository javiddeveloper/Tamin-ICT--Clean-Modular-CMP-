package com.tamin.taminhamrah.data.repository

import app.cash.turbine.test
import com.tamin.core.network.model.common.CityDto
import com.tamin.core.network.model.common.CityNameDto
import com.tamin.core.network.model.common.ProvinceDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity
import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.model.common.BeneficiaryDTO
import com.tamin.taminhamrah.model.common.InsuranceTypeDTO
import com.tamin.taminhamrah.model.common.JobTitleDTO
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.RecipientDTO
import com.tamin.taminhamrah.model.common.UserInsuredInfoDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.query.city.CityByProvinceQuery
import com.tamin.taminhamrah.query.city.CityListQuery
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.repository.CityProvinceRepository
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `getCitiesPage`/`getProvincesPage` are offline-first: cached slice first, then the network
 * page (see [CityProvinceRepositoryImpl]). With an empty [FakeDao] they emit only the network page.
 */
class CityProvinceRepositoryImplTest {

    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var dao: FakeDao
    private lateinit var repository: CityProvinceRepository

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeRemoteDataSource()
        dao = FakeDao()
        repository = CityProvinceRepositoryImpl(remoteDataSource, dao)
    }

    @Test
    fun `getProvincesPage returns the page the remote data source answers with`() = runTest {
        remoteDataSource.provinceNameResult = ProvinceNameDto(list = listOf(createProvinceDto(code = "2")), total = 1)

        repository.getProvincesPage(ApiQueryParamDN()).test {
            val page = awaitItem()
            assertEquals(listOf("2"), page.items.map { it.provinceCode })
            assertEquals(1, page.total)
            awaitComplete()
        }
    }

    @Test
    fun `getProvincesPage propagates a remote failure`() = runTest {
        remoteDataSource.shouldThrowError = true

        repository.getProvincesPage(ApiQueryParamDN()).test {
            awaitError()
        }
    }

    @Test
    fun `getCitiesPage returns the page the remote data source answers with`() = runTest {
        remoteDataSource.cityNameResult = CityNameDto(list = listOf(createCityDto(code = "0701")), total = 1)

        repository.getCitiesPage(ApiQueryParamDN()).test {
            val page = awaitItem()
            assertEquals(listOf("0701"), page.items.map { it.cityCode })
            assertEquals(1, page.total)
            awaitComplete()
        }
    }

    @Test
    fun `getCitiesByProvincePage returns the page the remote data source answers with`() = runTest {
        remoteDataSource.citiesByProvinceResult = CityNameDto(list = listOf(createCityDto(code = "0701")), total = 1)

        repository.getCitiesByProvincePage("07", ApiQueryParamDN()).test {
            val page = awaitItem()
            assertEquals(listOf("0701"), page.items.map { it.cityCode })
            awaitComplete()
        }
    }

    @Test
    fun `getCitiesPage emits the cached slice then the network page and caches it`() = runTest {
        dao.citiesFlow.value = listOf(CityEntity(cityCode = "0701", provinceCode = "07", cityName = "city-0701"))
        remoteDataSource.cityNameResult = CityNameDto(list = listOf(createCityDto(code = "0702")), total = 5)

        repository.getCitiesPage(ApiQueryParamDN(start = 0, limit = 10)).test {
            val cached = awaitItem()
            assertEquals(listOf("0701"), cached.items.map { it.cityCode })
            assertTrue(cached.isFromCache)

            val remote = awaitItem()
            assertEquals(listOf("0702"), remote.items.map { it.cityCode })
            assertEquals(5, remote.total)
            assertFalse(remote.isFromCache)
            awaitComplete()
        }
        // Page 0 from the network replaces the cached list: the stale 0701 is gone.
        assertEquals(listOf("0702"), dao.citiesFlow.value.map { it.cityCode })
    }

    @Test
    fun `getCitiesPage serves the cached slice when offline`() = runTest {
        dao.citiesFlow.value = listOf(CityEntity(cityCode = "0701", provinceCode = "07", cityName = "city-0701"))
        remoteDataSource.shouldThrowError = true

        repository.getCitiesPage(ApiQueryParamDN(start = 0, limit = 10)).test {
            val page = awaitItem()
            assertEquals(listOf("0701"), page.items.map { it.cityCode })
            assertTrue(page.isFromCache)
            awaitComplete()
        }
    }

    @Test
    fun `getCitiesPage throws when offline and nothing is cached for the slice`() = runTest {
        remoteDataSource.shouldThrowError = true

        repository.getCitiesPage(ApiQueryParamDN(start = 0, limit = 10)).test {
            awaitError()
        }
    }

    @Test
    fun `getCitiesPage passes the server name and province filters to the cache query`() = runTest {
        val query = ApiQueryParamDN(
            start = 10,
            limit = 5,
            filters = CityListQuery.filters(cityName = "teh", provinceCode = "07"),
        )

        repository.getCitiesPage(query).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(CitySliceArgs(cityName = "teh", provinceCode = "07", limit = 5, offset = 10), dao.lastCitySlice)
    }

    @Test
    fun `getCitiesPage skips the cache for a filter the local query cannot reproduce`() = runTest {
        dao.citiesFlow.value = listOf(CityEntity(cityCode = "0701", provinceCode = "07", cityName = "city-0701"))
        remoteDataSource.cityNameResult = CityNameDto(list = listOf(createCityDto(code = "0702")), total = 1)
        val query = ApiQueryParamDN(
            filters = listOf(ApiFilterDN(FilterProperty.CITY_CODE, "0701", FilterOperator.EQUAL)),
        )

        repository.getCitiesPage(query).test {
            assertFalse(awaitItem().isFromCache)
            awaitComplete()
        }
        assertNull(dao.lastCitySlice)
    }

    @Test
    fun `getProvincesPage serves the cached slice when offline`() = runTest {
        dao.provincesFlow.value = listOf(
            ProvinceEntity("1", "a", null, null),
            ProvinceEntity("2", "b", null, null),
            ProvinceEntity("3", "c", null, null),
        )
        remoteDataSource.shouldThrowError = true

        repository.getProvincesPage(ApiQueryParamDN(start = 1, limit = 1)).test {
            val page = awaitItem()
            assertEquals(listOf("2"), page.items.map { it.provinceCode })
            assertTrue(page.isFromCache)
            awaitComplete()
        }
    }

    @Test
    fun `getProvincesPage caches the network page`() = runTest {
        remoteDataSource.provinceNameResult = ProvinceNameDto(list = listOf(createProvinceDto(code = "2")), total = 1)

        repository.getProvincesPage(ApiQueryParamDN()).test {
            awaitItem()
            awaitComplete()
        }
        assertEquals(listOf("2"), dao.provincesFlow.value.map { it.provinceCode })
    }

    @Test
    fun `getCitiesByProvincePage caches the network page and serves it offline`() = runTest {
        // Regression: complete-employer-info picks a province, loads its cities, goes offline,
        // re-picks the province — the city sheet must come from the cache, not be empty.
        remoteDataSource.citiesByProvinceResult = CityNameDto(list = listOf(createCityDto(code = "0701")), total = 1)
        val query = ApiQueryParamDN(start = 0, limit = 200, filters = CityByProvinceQuery.filters("07"))
        repository.getCitiesByProvincePage("07", query).test {
            awaitItem()
            awaitComplete()
        }

        remoteDataSource.shouldThrowError = true
        repository.getCitiesByProvincePage("07", query).test {
            val page = awaitItem()
            assertEquals(listOf("0701"), page.items.map { it.cityCode })
            assertTrue(page.isFromCache)
            awaitComplete()
        }
    }

    @Test
    fun `getCitiesByProvincePage matches the cached province ignoring leading zeros`() = runTest {
        dao.citiesFlow.value = listOf(CityEntity(cityCode = "0701", provinceCode = "07", cityName = "city-0701"))
        remoteDataSource.shouldThrowError = true

        repository.getCitiesByProvincePage("7", ApiQueryParamDN(filters = CityByProvinceQuery.filters("7"))).test {
            assertEquals(listOf("0701"), awaitItem().items.map { it.cityCode })
            awaitComplete()
        }
    }

    @Test
    fun `getCitiesByProvincePage only returns the requested province from the cache`() = runTest {
        dao.citiesFlow.value = listOf(
            CityEntity(cityCode = "0701", provinceCode = "07", cityName = "a"),
            CityEntity(cityCode = "0801", provinceCode = "08", cityName = "b"),
        )
        remoteDataSource.shouldThrowError = true

        repository.getCitiesByProvincePage("08", ApiQueryParamDN(filters = CityByProvinceQuery.filters("08"))).test {
            assertEquals(listOf("0801"), awaitItem().items.map { it.cityCode })
            awaitComplete()
        }
    }

    @Test
    fun `a fresh first page replaces the province's cached cities but not other provinces`() = runTest {
        dao.citiesFlow.value = listOf(
            CityEntity(cityCode = "0799", provinceCode = "07", cityName = "stale"),
            CityEntity(cityCode = "0801", provinceCode = "08", cityName = "other-province"),
        )
        remoteDataSource.citiesByProvinceResult = CityNameDto(list = listOf(createCityDto(code = "0701")), total = 1)

        repository.getCitiesByProvincePage("07", ApiQueryParamDN(start = 0, limit = 200, filters = CityByProvinceQuery.filters("07"))).collect {}

        assertEquals(setOf("0701", "0801"), dao.citiesFlow.value.map { it.cityCode }.toSet())
    }

    @Test
    fun `a later page appends to the cache instead of replacing it`() = runTest {
        dao.citiesFlow.value = listOf(CityEntity(cityCode = "0701", provinceCode = "07", cityName = "page-0"))
        remoteDataSource.citiesByProvinceResult = CityNameDto(list = listOf(createCityDto(code = "0702")), total = 2)

        repository.getCitiesByProvincePage("07", ApiQueryParamDN(start = 1, limit = 1, filters = CityByProvinceQuery.filters("07"))).collect {}

        assertEquals(setOf("0701", "0702"), dao.citiesFlow.value.map { it.cityCode }.toSet())
    }

    @Test
    fun `a search's first page replaces only the cached cities matching that search`() = runTest {
        dao.citiesFlow.value = listOf(
            CityEntity(cityCode = "0799", provinceCode = "07", cityName = "tehran-stale"),
            CityEntity(cityCode = "0798", provinceCode = "07", cityName = "karaj"),
        )
        remoteDataSource.cityNameResult = CityNameDto(
            list = listOf(CityDto(cityCode = "0701", provinceCode = "07", cityName = "tehran")),
            total = 1,
        )

        repository.getCitiesPage(ApiQueryParamDN(start = 0, filters = CityListQuery.filters(cityName = "tehran"))).collect {}

        assertEquals(setOf("0701", "0798"), dao.citiesFlow.value.map { it.cityCode }.toSet())
    }

    @Test
    fun `getProvincesPage first page replaces the cached province list`() = runTest {
        dao.provincesFlow.value = listOf(ProvinceEntity("99", "stale", null, null))
        remoteDataSource.provinceNameResult = ProvinceNameDto(list = listOf(createProvinceDto(code = "2")), total = 1)

        repository.getProvincesPage(ApiQueryParamDN(start = 0)).collect {}

        assertEquals(listOf("2"), dao.provincesFlow.value.map { it.provinceCode })
    }

    private fun createProvinceDto(code: String) = ProvinceDto(
        provinceCode = code,
        provinceName = "province-$code",
        status = null,
        statusStartDate = null,
    )

    private fun createCityDto(code: String) = CityDto(
        cityCode = code,
        provinceCode = "07",
        cityName = "city-$code",
    )

    // Fakes
    private class FakeRemoteDataSource : CommonRemoteDataSource {
        var provinceNameResult = ProvinceNameDto(list = emptyList(), total = 0)
        var cityNameResult = CityNameDto(list = emptyList(), total = 0)
        var citiesByProvinceResult = CityNameDto(list = emptyList(), total = 0)
        var shouldThrowError = false

        override suspend fun getCityName(cityNameRequest: ApiQueryParamDN): CityNameDto {
            if (shouldThrowError) throw RuntimeException("Remote failure")
            return cityNameResult
        }

        override suspend fun getProvinceName(provinceNameRequest: ApiQueryParamDN): ProvinceNameDto {
            if (shouldThrowError) throw RuntimeException("Remote failure")
            return provinceNameResult
        }

        override suspend fun getCitiesByProvince(query: ApiQueryParamDN): CityNameDto {
            if (shouldThrowError) throw RuntimeException("Remote failure")
            return citiesByProvinceResult
        }

        override suspend fun getInsuranceTypes(query: ApiQueryParamDN): ListData<InsuranceTypeDTO>? =
            throw NotImplementedError("not used by these tests")

        override suspend fun getMainMenu(versionCode: String, forceUpdate: Boolean): List<MainServiceDto> =
            throw NotImplementedError("not used by these tests")

        override suspend fun getBeneficiary(query: ApiQueryParamDN): ListData<BeneficiaryDTO> =
            throw NotImplementedError("not used by these tests")

        override suspend fun getRecipientList(query: ApiQueryParamDN): ListData<RecipientDTO> =
            throw NotImplementedError("not used by these tests")

        override suspend fun getRegistrationDeclarationForm(): HttpStatement =
            throw NotImplementedError("not used by these tests")

        override suspend fun getJobTitle(query: ApiQueryParamDN): ListData<JobTitleDTO>? =
            throw NotImplementedError("not used by these tests")

        override suspend fun checkInsuredInfo(): UserInsuredInfoDTO =
            throw NotImplementedError("not used by these tests")
    }

    private data class CitySliceArgs(val cityName: String?, val provinceCode: String?, val limit: Int, val offset: Int)

    private class FakeDao : CityProvinceDao {
        val provincesFlow = MutableStateFlow<List<ProvinceEntity>>(emptyList())
        val citiesFlow = MutableStateFlow<List<CityEntity>>(emptyList())
        var lastCitySlice: CitySliceArgs? = null

        override suspend fun getCitiesSlice(
            cityName: String?,
            provinceCode: String?,
            limit: Int,
            offset: Int,
        ): List<CityEntity> {
            lastCitySlice = CitySliceArgs(cityName, provinceCode, limit, offset)
            return citiesFlow.value
                .filter { cityName == null || it.cityName?.contains(cityName) == true }
                // Mirrors the DAO's LTRIM(provinceCode, '0') comparison.
                .filter { provinceCode == null || it.provinceCode?.trimStart('0') == provinceCode.trimStart('0') }
                .drop(offset)
                .take(limit)
        }

        override suspend fun upsertCity(city: CityEntity) {
            citiesFlow.value = citiesFlow.value.filterNot { it.cityCode == city.cityCode } + city
        }

        override suspend fun upsertCities(cities: List<CityEntity>) {
            cities.forEach { upsertCity(it) }
        }

        override fun getCity(cityCode: String): Flow<CityEntity?> =
            MutableStateFlow(citiesFlow.value.firstOrNull { it.cityCode == cityCode })

        override fun getCitiesByProvinceCode(provinceCode: String): Flow<List<CityEntity>> =
            MutableStateFlow(citiesFlow.value.filter { it.provinceCode == provinceCode })

        override suspend fun clearCitiesMatching(cityName: String?, provinceCode: String?) {
            citiesFlow.value = citiesFlow.value.filterNot { it.matches(cityName, provinceCode) }
        }

        override suspend fun replaceCitiesMatching(cityName: String?, provinceCode: String?, cities: List<CityEntity>) {
            clearCitiesMatching(cityName, provinceCode)
            upsertCities(cities)
        }

        // Mirrors the DAO's WHERE: LIKE '%name%' and LTRIM(provinceCode, '0') equality.
        private fun CityEntity.matches(cityName: String?, provinceCode: String?): Boolean =
            (cityName == null || this.cityName?.contains(cityName) == true) &&
                (provinceCode == null || this.provinceCode?.trimStart('0') == provinceCode.trimStart('0'))

        override suspend fun clearCitiesByProvinceCode(provinceCode: String) {
            citiesFlow.value = citiesFlow.value.filterNot { it.provinceCode == provinceCode }
        }

        override suspend fun replaceCitiesForProvince(provinceCode: String, cities: List<CityEntity>) {
            clearCitiesByProvinceCode(provinceCode)
            upsertCities(cities)
        }

        override suspend fun upsertProvince(province: ProvinceEntity) {
            provincesFlow.value = provincesFlow.value.filterNot { it.provinceCode == province.provinceCode } + province
        }

        override suspend fun upsertProvinces(provinces: List<ProvinceEntity>) {
            provinces.forEach { upsertProvince(it) }
        }

        override fun getProvince(provinceCode: String): Flow<ProvinceEntity?> =
            MutableStateFlow(provincesFlow.value.firstOrNull { it.provinceCode == provinceCode })

        override fun getAllProvinces(): Flow<List<ProvinceEntity>> = provincesFlow

        override suspend fun clearProvinces() {
            provincesFlow.value = emptyList()
        }

        override suspend fun replaceAllProvinces(provinces: List<ProvinceEntity>) {
            clearProvinces()
            upsertProvinces(provinces)
        }
    }
}
