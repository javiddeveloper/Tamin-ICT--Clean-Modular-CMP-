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
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.repository.CityProvinceRepository
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Province/city lists are network-only for now (no Room caching) — see [CityProvinceRepositoryImpl].
 * `getCity`/`getProvince` (single-item lookups) still use the DAO and are covered separately.
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

    private class FakeDao : CityProvinceDao {
        val provincesFlow = MutableStateFlow<List<ProvinceEntity>>(emptyList())
        val citiesFlow = MutableStateFlow<List<CityEntity>>(emptyList())

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
