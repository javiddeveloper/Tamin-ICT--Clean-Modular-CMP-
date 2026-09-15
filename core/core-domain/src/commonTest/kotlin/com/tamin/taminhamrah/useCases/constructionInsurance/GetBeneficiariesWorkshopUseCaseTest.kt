package com.tamin.taminhamrah.useCases.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.repository.constructionInsurance.FakeConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetBeneficiariesWorkshopUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeConstructionInsuranceRepository
    private lateinit var useCase: GetBeneficiariesWorkshopUseCase

    @BeforeTest
    fun setup() {
        repository = FakeConstructionInsuranceRepository()
        useCase = GetBeneficiariesWorkshopUseCase(repository)
    }

    @Test
    fun `invoke should return beneficiaries from repository`() = runTest {
        val expected = listOf(
            BeneficiaryConstructionDN(
                nationalCode = "0930123450", ownerType = "01", requestNumber = 123L,
                fileNumber = 456L, requestDate = "14020901", name = "علی",
                lastName = "توکلی", mobile = "09123456700",
            )
        )
        repository.beneficiariesResult = expected

        useCase(requestNumber = 123L, fileNumber = 456L, requestDate = "14020901").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals(123L, repository.lastBeneficiariesRequestNumber)
        assertEquals(456L, repository.lastBeneficiariesFileNumber)
        assertEquals("14020901", repository.lastBeneficiariesRequestDate)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.thrownError = expectedException

        useCase(requestNumber = null, fileNumber = null).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
