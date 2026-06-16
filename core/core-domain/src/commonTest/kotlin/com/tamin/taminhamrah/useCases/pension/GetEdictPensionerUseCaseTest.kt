package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetEdictPensionerUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: GetEdictPensionerUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = GetEdictPensionerUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return edict pensioner data from repository`() = runTest {
        val expectedEdict = EdictPensionerDN(
            lastName = "Sattar",
            branchName = "Tehran",
            insuranceId = "12345678",
            title = "Pension Edict"
        )
        pensionRepository.edictPensionerResult = expectedEdict

        val query = ApiQueryParamDN(
            filters = listOf(
                ApiFilterDN(FilterProperty.START_DATE, "14000101", FilterOperator.EQUAL),
                ApiFilterDN(FilterProperty.PENSIONER_ID, "1003406938", FilterOperator.EQUAL)
            )
        )

        useCase.invoke(query).test {
            val result = awaitItem()
            assertNotNull(result)
            assertEquals("Sattar", result.lastName)
            assertEquals("Tehran", result.branchName)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Network error")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        val query = ApiQueryParamDN()

        useCase.invoke(query).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
