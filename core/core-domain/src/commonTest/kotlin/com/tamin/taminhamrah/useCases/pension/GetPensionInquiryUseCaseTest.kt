package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPensionInquiryUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: GetPensionInquiryUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = GetPensionInquiryUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return pension inquiry list from repository`() = runTest {
        val expectedList = listOf(
            PensionInquiryDN(
                fullName = "Test User",
                nationalId = "1234567890",
                branchName = "Test Branch",
                branchCode = "123",
                insuranceNumber = "456",
                pensionerRisUid = "789",
                pensionerType = "Type",
                paymentDate = "2023-01-01",
                pensionerBaseDate = "2023-01-01",
                statusDesc = "Active",
                sexDesc = "Male",
                pensionEndDate = "",
                paymentAmount = 1000
            )
        )
        pensionRepository.pensionInquiryResult = expectedList

        useCase.invoke(emptyList()).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Test User", result[0].fullName)
            awaitComplete()
        }
    }
    @Test
    fun `invoke should return error`() = runTest {

        val expectedException = RuntimeException("get pension failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException


        useCase.invoke(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }

    }
}
