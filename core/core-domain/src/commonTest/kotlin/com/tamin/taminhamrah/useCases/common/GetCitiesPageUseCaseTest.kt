package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.FakeCityProvinceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCitiesPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeCityProvinceRepository
    private lateinit var useCase: GetCitiesPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeCityProvinceRepository()
        useCase = GetCitiesPageUseCase(repository)
    }

    @Test
    fun `invoke should return cities page filtered by name from repository`() = runTest {
        val expectedCities = listOf(
            CityDN(cityCode = "1158", cityName = "اصفهان", provinceCode = "04"),
        )
        repository.citiesResult = expectedCities
        val query = ApiQueryParamDN(
            filters = listOf(ApiFilterDN(FilterProperty.CITY_NAME, "اصفهان", FilterOperator.CONTAINS)),
        )

        useCase(query).test {
            assertEquals(expectedCities, awaitItem().items)
            awaitComplete()
        }

        assertEquals("اصفهان", repository.lastCitiesSearch)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(ApiQueryParamDN()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
