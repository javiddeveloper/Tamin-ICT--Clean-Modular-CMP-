package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.repository.common.FakeCommonRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetInsuranceTypesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeCommonRepository
    private lateinit var useCase: GetInsuranceTypesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeCommonRepository()
        useCase = GetInsuranceTypesUseCase(repository)
    }

    @Test
    fun `invoke should return insurance types from repository`() = runTest {
        val expectedTypes = listOf(
            InsuranceTypeDN(insuranceTypeCode = "01", insuranceTypeDesc = "اجباري", status = "1"),
        )
        repository.insuranceTypesResult = expectedTypes

        useCase().test {
            assertEquals(expectedTypes, awaitItem())
            awaitComplete()
        }

        assertNull(repository.lastInsuranceTypeSearch)
    }

    @Test
    fun `invoke should forward the search text to repository`() = runTest {
        useCase("قالي").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("قالي", repository.lastInsuranceTypeSearch)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.getInsuranceTypesError = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
