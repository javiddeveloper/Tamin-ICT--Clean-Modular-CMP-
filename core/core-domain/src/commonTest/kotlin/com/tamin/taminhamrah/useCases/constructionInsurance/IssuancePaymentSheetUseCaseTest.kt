package com.tamin.taminhamrah.useCases.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.constructionInsurance.FakeConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class IssuancePaymentSheetUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeConstructionInsuranceRepository
    private lateinit var useCase: IssuancePaymentSheetUseCase

    @BeforeTest
    fun setup() {
        repository = FakeConstructionInsuranceRepository()
        useCase = IssuancePaymentSheetUseCase(repository)
    }

    @Test
    fun `invoke should return the confirmation message and forward debitNumber`() = runTest {
        repository.issuanceMessageResult = "برگه پرداخت با موفقیت صادر شد."

        useCase("123456789010").test {
            assertEquals("برگه پرداخت با موفقیت صادر شد.", awaitItem())
            awaitComplete()
        }

        assertEquals("123456789010", repository.lastIssuanceDebitNumber)
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
