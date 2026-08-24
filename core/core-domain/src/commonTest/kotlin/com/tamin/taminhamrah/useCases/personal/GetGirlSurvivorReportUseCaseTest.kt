package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.girlSurvivor.GirlSurvivorReportParamsDN
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetGirlSurvivorReportUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalRepository
    private lateinit var useCase: GetGirlSurvivorReportUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalRepository()
        useCase = GetGirlSurvivorReportUseCase(repository)
    }

    @Test
    fun `invoke should return report pdf from repository`() = runTest {
        val expectedPdf = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
        repository.girlSurvivorReportResult = expectedPdf

        useCase(sampleParams()).test {
            assertEquals(expectedPdf, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("report failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(sampleParams()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    private fun sampleParams() = GirlSurvivorReportParamsDN(
        address = "تهران",
        tel = "02166778899",
        postalCode = "1234567890",
        fatherName = "علی",
        birthDate = 631152000000,
        insuranceId = "0071234567",
        parentCode = "0012345678",
        pensionerId = "",
    )
}
