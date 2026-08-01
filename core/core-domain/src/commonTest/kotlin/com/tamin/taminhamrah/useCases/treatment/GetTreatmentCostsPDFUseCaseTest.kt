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

class GetTreatmentCostsPDFUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetTreatmentCostsPDFUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetTreatmentCostsPDFUseCase(repository)
    }

    @Test
    fun `invoke should return treatment costs pdf data`() = runTest {
        val expected = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
        repository.getTreatmentCostsPDFResult = expected

        useCase("repId").test {
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

        useCase("repId").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit pdf with null stream when repId is blank`() = runTest {
        val expected = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
        repository.getTreatmentCostsPDFResult = expected

        useCase("").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }
}
