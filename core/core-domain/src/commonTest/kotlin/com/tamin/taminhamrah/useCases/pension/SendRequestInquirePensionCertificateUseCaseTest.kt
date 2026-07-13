package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendRequestInquirePensionCertificateUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: SendRequestInquirePensionCertificateUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = SendRequestInquirePensionCertificateUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return success message from repository`() = runTest {
        val expectedResult = InquirePensionCertificateDN(
            message = "درخواست شما با موفقیت ثبت شد"
        )
        pensionRepository.inquirePensionCertificateResult = expectedResult

        useCase.invoke(emptyList()).test {
            val result = awaitItem()
            assertEquals("درخواست شما با موفقیت ثبت شد", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("API error")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        useCase.invoke(emptyList()).test {
            val actualException = awaitError()
            assertEquals("API error", actualException.message)
        }
    }
}
