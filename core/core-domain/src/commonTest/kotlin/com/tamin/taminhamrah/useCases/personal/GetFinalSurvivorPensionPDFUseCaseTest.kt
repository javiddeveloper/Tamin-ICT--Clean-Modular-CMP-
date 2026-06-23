package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFinalSurvivorPensionPDFUseCaseTest : BaseUseCaseTest() {

    private lateinit var personalRepository: FakePersonalRepository
    private lateinit var useCase: GetFinalSurvivorPensionPDFUseCase

    @BeforeTest
    fun setup() {
        personalRepository = FakePersonalRepository()
        useCase = GetFinalSurvivorPensionPDFUseCase(personalRepository)
    }

    @Test
    fun `invoke should return pdf download info from repository`() = runTest {
        val expectedPdf = PdfDownloadDN(
            pdf = InputStreamDN(pdf = null) // Mocking ByteReadChannel as null for test
        )
        personalRepository.pdfDownloadResult = expectedPdf

        useCase.invoke().test {
            val result = awaitItem()
            assertEquals(expectedPdf, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val expectedException = RuntimeException("get pdf failed")
        personalRepository.shouldThrowError = true
        personalRepository.error = expectedException

        useCase.invoke().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
