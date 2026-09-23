package com.tamin.taminhamrah.useCases.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.repository.constructionInsurance.FakeConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetPaymentSheetConstructionInfoUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeConstructionInsuranceRepository
    private lateinit var useCase: GetPaymentSheetConstructionInfoUseCase

    @BeforeTest
    fun setup() {
        repository = FakeConstructionInsuranceRepository()
        useCase = GetPaymentSheetConstructionInfoUseCase(repository)
    }

    @Test
    fun `invoke should return payment sheets from repository`() = runTest {
        val expected = listOf(
            PaymentSheetConstructionFileDN(
                orderNumber = "1", paymentCode = "3600", paymentSheetAmount = 500_000L,
                status = "پرداخت شده", paymentDate = "14021110", buildingRequest = null,
            )
        )
        repository.paymentSheetsResult = expected

        useCase("123456789010").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals("123456789010", repository.lastPaymentSheetDebitNumber)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.thrownError = expectedException

        useCase("123").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
