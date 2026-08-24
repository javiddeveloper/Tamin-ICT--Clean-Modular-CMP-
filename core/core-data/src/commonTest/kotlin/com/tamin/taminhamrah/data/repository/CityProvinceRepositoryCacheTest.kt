package com.tamin.taminhamrah.data.repository

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
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * `proxy/models/province` is not reliably up — it answered 503 while other endpoints were fine.
 * Provinces and cities barely change and are already written to Room, so a stale list beats an
 * empty picker; the failure should only surface when there is nothing cached to fall back on.
 */
class CityProvinceRepositoryCacheTest {

    private val tehranProvince = ProvinceDto(
        provinceCode = "07",
        provinceName = "تهران",
        status = null,
        statusStartDate = null,
    )

    private fun repository(
        remote: FakeCommonRemoteDataSource,
        dao: FakeCityProvinceDao,
    ) = CityProvinceRepositoryImpl(commonRemoteDataSource = remote, cityProvinceDao = dao)

    @Test
    fun `a 503 on provinces still serves the cached list`() = runTest {
        val dao = FakeCityProvinceDao(
            provinces = listOf(ProvinceEntity("07", "تهران", null, null)),
        )
        val remote = FakeCommonRemoteDataSource(error = IllegalStateException("503"))

        val provinces = repository(remote, dao).getProvinces().toList().last()

        assertEquals(listOf("07"), provinces.map { it.provinceCode })
    }

    @Test
    fun `a 503 on provinces with an empty cache reports the failure`() = runTest {
        val remote = FakeCommonRemoteDataSource(error = IllegalStateException("503"))

        assertFailsWith<IllegalStateException> {
            repository(remote, FakeCityProvinceDao()).getProvinces().toList()
        }
    }

    @Test
    fun `a successful province load replaces what was cached`() = runTest {
        val dao = FakeCityProvinceDao(
            provinces = listOf(ProvinceEntity("99", "کهنه", null, null)),
        )
        val remote = FakeCommonRemoteDataSource(provinces = listOf(tehranProvince))

        val emissions = repository(remote, dao).getProvinces().toList()

        // Cache first so the picker fills immediately, then the server's answer.
        assertEquals(listOf("99"), emissions.first().map { it.provinceCode })
        assertEquals(listOf("07"), emissions.last().map { it.provinceCode })
    }

    @Test
    fun `a failed city load still serves the province's cached cities`() = runTest {
        val dao = FakeCityProvinceDao(
            cities = listOf(CityEntity("0701", "07", "تهران")),
        )
        val remote = FakeCommonRemoteDataSource(error = IllegalStateException("503"))

        val cities = repository(remote, dao).getCities(provinceCode = "07").toList().last()

        assertEquals(listOf("0701"), cities.map { it.cityCode })
    }
}

private class FakeCityProvinceDao(
    provinces: List<ProvinceEntity> = emptyList(),
    cities: List<CityEntity> = emptyList(),
) : CityProvinceDao {
    private val provinceRows = MutableStateFlow(provinces)
    private val cityRows = MutableStateFlow(cities)

    override suspend fun upsertCity(city: CityEntity) {
        cityRows.value = cityRows.value.filterNot { it.cityCode == city.cityCode } + city
    }

    override fun getCity(cityCode: String): Flow<CityEntity?> =
        cityRows.map { all -> all.firstOrNull { it.cityCode == cityCode } }

    override suspend fun upsertProvince(province: ProvinceEntity) {
        provinceRows.value =
            provinceRows.value.filterNot { it.provinceCode == province.provinceCode } + province
    }

    override fun getProvince(provinceCode: String): Flow<ProvinceEntity?> =
        provinceRows.map { all -> all.firstOrNull { it.provinceCode == provinceCode } }

    override fun getAllProvinces(): Flow<List<ProvinceEntity>> = provinceRows

    override fun getCitiesByProvinceCode(provinceCode: String): Flow<List<CityEntity>> =
        cityRows.map { all -> all.filter { it.provinceCode == provinceCode } }

    override suspend fun upsertCities(cities: List<CityEntity>) {
        cities.forEach { upsertCity(it) }
    }

    override suspend fun clearCitiesByProvinceCode(provinceCode: String) {
        cityRows.value = cityRows.value.filterNot { it.provinceCode == provinceCode }
    }

    override suspend fun upsertProvinces(provinces: List<ProvinceEntity>) {
        provinces.forEach { upsertProvince(it) }
    }

    override suspend fun clearProvinces() {
        provinceRows.value = emptyList()
    }
}

private class FakeCommonRemoteDataSource(
    private val provinces: List<ProvinceDto> = emptyList(),
    private val cities: List<CityDto> = emptyList(),
    private val error: Exception? = null,
) : CommonRemoteDataSource {

    override suspend fun getProvinceName(provinceNameRequest: ApiQueryParamDN): ProvinceNameDto {
        error?.let { throw it }
        return ProvinceNameDto(list = provinces, total = provinces.size)
    }

    override suspend fun getCityName(cityNameRequest: ApiQueryParamDN): CityNameDto {
        error?.let { throw it }
        return CityNameDto(list = cities, total = cities.size)
    }

    override suspend fun getCitiesByProvince(query: ApiQueryParamDN): CityNameDto {
        error?.let { throw it }
        return CityNameDto(list = cities, total = cities.size)
    }

    override suspend fun getInsuranceTypes(query: ApiQueryParamDN): ListData<InsuranceTypeDTO>? = unused()

    override suspend fun getMainMenu(versionCode: String, forceUpdate: Boolean): List<MainServiceDto> = unused()
    override suspend fun getBeneficiary(query: ApiQueryParamDN): ListData<BeneficiaryDTO> = unused()
    override suspend fun getRecipientList(query: ApiQueryParamDN): ListData<RecipientDTO> = unused()
    override suspend fun getRegistrationDeclarationForm(): HttpStatement = unused()
    override suspend fun getJobTitle(query: ApiQueryParamDN): ListData<JobTitleDTO>? = unused()
}

private fun <T> unused(): T = error("not part of the city/province cache path under test")
