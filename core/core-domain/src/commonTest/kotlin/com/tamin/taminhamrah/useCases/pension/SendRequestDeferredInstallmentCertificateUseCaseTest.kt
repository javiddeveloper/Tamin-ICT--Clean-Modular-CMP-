package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.installment.RequestCertificateDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendRequestDeferredInstallmentCertificateUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: SendRequestDeferredInstallmentCertificateUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = SendRequestDeferredInstallmentCertificateUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return deferred installment certificate from repository`() = runTest {
        val expectedResult = DeferredInstallmentCertificateDN(
            request = RequestCertificateDN(refCode = "REF123")
        )
        pensionRepository.deferredInstallmentCertificateResult = expectedResult

        val request = DeferredInstallmentRequestDN(pensionerId = "123")

        useCase.invoke(request).test {
            val result = awaitItem()
            assertEquals("REF123", result.request?.refCode)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Request failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        val request = DeferredInstallmentRequestDN(pensionerId = "123")

        useCase.invoke(request).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
