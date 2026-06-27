package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPensionerPayRollPDFUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: GetPensionerPayRollPDFUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = GetPensionerPayRollPDFUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return pdf from repository`() = runTest {
        val expectedPdf = PdfDownloadDN(pdf = null)
        pensionRepository.payRollPDFResult = expectedPdf

        useCase.invoke(emptyList()).test {
            val result = awaitItem()
            assertEquals(expectedPdf, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val expectedException = RuntimeException("get pdf failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        useCase.invoke(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
