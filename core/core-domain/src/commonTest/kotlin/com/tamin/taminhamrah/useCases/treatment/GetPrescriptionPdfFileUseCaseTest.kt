package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPrescriptionPdfFileUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetPrescriptionPdfFileUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetPrescriptionPdfFileUseCase(repository)
    }

    @Test
    fun `invoke should return pdf file data`() = runTest {
        val expected = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
        repository.getPrescriptionPdfFileResult = expected

        useCase("prescriptionID").test {
            val item = awaitItem()
            assertEquals(expected, item)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("prescriptionID").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit pdf with null stream when prescriptionID is blank`() = runTest {
        val expected = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
        repository.getPrescriptionPdfFileResult = expected

        useCase("").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }
}
