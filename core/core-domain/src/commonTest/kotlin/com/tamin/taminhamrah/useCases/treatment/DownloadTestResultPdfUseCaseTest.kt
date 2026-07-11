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

class DownloadTestResultPdfUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: DownloadTestResultPdfUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = DownloadTestResultPdfUseCase(repository)
    }

    @Test
    fun `invoke should return test result pdf data`() = runTest {
        val expected = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
        repository.downloadTestResultPdfResult = expected

        useCase("patientID", "noteHeadEprescID", "currentUserNationalCode").test {
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

        useCase("patientID", "noteHeadEprescID", "currentUserNationalCode").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should return pdf when all inputs are null`() = runTest {
        val expected = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
        repository.downloadTestResultPdfResult = expected

        useCase(null, null, null).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }
}
