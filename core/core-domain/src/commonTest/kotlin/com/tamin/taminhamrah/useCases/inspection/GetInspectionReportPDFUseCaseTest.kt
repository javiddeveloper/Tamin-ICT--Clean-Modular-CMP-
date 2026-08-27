package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetInspectionReportPDFUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeInspectionRepository
    private lateinit var useCase: GetInspectionReportPDFUseCase

    @BeforeTest
    fun setup() {
        repository = FakeInspectionRepository()
        useCase = GetInspectionReportPDFUseCase(repository)
    }

    @Test
    fun invoke_returnsPdfFromRepository() = runTest {
        val expected = PdfDownloadDN(pdf = null)
        repository.reportPdfResult = expected

        val result = useCase("0130980012641")

        assertEquals(expected, result)
        assertEquals("0130980012641", repository.lastReportPdfInspectionNo)
    }

    @Test
    fun invoke_onFailure_propagatesException() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            useCase("0130980012641")
        }
    }
}
