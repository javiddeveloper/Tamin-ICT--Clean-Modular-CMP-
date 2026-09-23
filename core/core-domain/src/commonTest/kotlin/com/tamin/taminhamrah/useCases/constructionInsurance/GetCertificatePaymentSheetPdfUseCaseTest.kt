package com.tamin.taminhamrah.useCases.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.constructionInsurance.FakeConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetCertificatePaymentSheetPdfUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeConstructionInsuranceRepository
    private lateinit var useCase: GetCertificatePaymentSheetPdfUseCase

    @BeforeTest
    fun setup() {
        repository = FakeConstructionInsuranceRepository()
        useCase = GetCertificatePaymentSheetPdfUseCase(repository)
    }

    @Test
    fun `invoke should return pdf from repository and forward debitNumber and branchCode`() = runTest {
        val expected = PdfDownloadDN(pdf = null)
        repository.certificatePdfResult = expected

        useCase(debitNumber = "123456789010", branchCode = "6400").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals("123456789010", repository.lastCertificateDebitNumber)
        assertEquals("6400", repository.lastCertificateBranchCode)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.thrownError = expectedException

        useCase(debitNumber = "123", branchCode = "6400").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
