package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.FakeCityProvinceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetProvincesPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeCityProvinceRepository
    private lateinit var useCase: GetProvincesPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeCityProvinceRepository()
        useCase = GetProvincesPageUseCase(repository)
    }

    @Test
    fun `invoke should return provinces page from repository`() = runTest {
        val expectedProvinces = listOf(
            ProvinceDN(provinceCode = "04", provinceName = "اصفهان", status = "1", statusStartDate = "19970321"),
        )
        repository.provincesResult = expectedProvinces

        useCase(ApiQueryParamDN()).test {
            assertEquals(expectedProvinces, awaitItem().items)
            awaitComplete()
        }
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
