package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.FakeCityProvinceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCitiesByProvincePageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeCityProvinceRepository
    private lateinit var useCase: GetCitiesByProvincePageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeCityProvinceRepository()
        useCase = GetCitiesByProvincePageUseCase(repository)
    }

    @Test
    fun `invoke should return cities page for the given province from repository`() = runTest {
        val expectedCities = listOf(
            CityDN(cityCode = "1158", cityName = "اصفهان", provinceCode = "04"),
        )
        repository.citiesByProvinceResult = expectedCities

        useCase("04", ApiQueryParamDN()).test {
            assertEquals(expectedCities, awaitItem().items)
            awaitComplete()
        }

        assertEquals("04", repository.lastCitiesByProvinceCode)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("04", ApiQueryParamDN()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
