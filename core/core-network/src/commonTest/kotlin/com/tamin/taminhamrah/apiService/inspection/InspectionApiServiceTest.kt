package com.tamin.taminhamrah.apiService.inspection

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.dataSource.inspection.InspectionRemoteDataSourceImpl
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilderImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.HttpErrorCopy
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.InspectionTestData
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.readRemaining
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray

class InspectionApiServiceTest : BaseApiTest() {

    @Test
    fun getAllInsurance_returnsInspectionPerformedList() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = InspectionTestData.inspectionPerformedListSuccess
        )

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createInspectionApiService()

        val response = apiService.getAllInsurance(emptyMap())

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(1, response.data?.total)
        assertEquals("تست", response.data?.list?.first()?.activityDesc)
    }

    @Test
    fun getAllManager_returnsInspectionPerformedList() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = InspectionTestData.inspectionPerformedListSuccess
        )

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createInspectionApiService()

        val response = apiService.getAllManager(emptyMap())

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(1, response.data?.total)
        assertEquals("تست", response.data?.list?.first()?.activityDesc)
    }

    @Test
    fun getBranches_returnsBranchesList() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = InspectionTestData.inspectionBranchesListSuccess
        )

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createInspectionApiService()

        val response = apiService.getBranches(emptyMap())

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(1, response.data?.total)
        assertEquals("یک تهران", response.data?.list?.first()?.name)
    }

    @Test
    fun getJobs_returnsJobsList() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = InspectionTestData.inspectionJobsListSuccess
        )

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createInspectionApiService()

        val response = apiService.getJobs(emptyMap())

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(1, response.data?.total)
        assertEquals("قرص سازی", response.data?.list?.first()?.jobDescription)
    }

    @Test
    fun submitInspectionRequest_returnsSubmittedId() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = InspectionTestData.inspectionSubmitSuccess
        )

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createInspectionApiService()

        val response = apiService.submitInspectionRequest(SubmitInspectionRequestDTO())

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(12345L, response.data?.request?.id)
    }

    @Test
    fun getInspectionReportPDF_returnsHttpResponse() = runTest {
        val ktorfit: Ktorfit = createMockKtorfit("")
        val apiService = ktorfit.createInspectionApiService()

        val response = apiService.getInspectionReportPDF("0130980012641")

        assertEquals(200, response.execute().status.value)
    }

    /** The report is drained status-checked: a failed download is a mapped error, never bytes for the viewer. */
    @Test
    fun getInspectionReportPDF_whenTheDownloadFails_throwsTheMappedError() = runTest {
        val apiService = createMockKtorfit(
            content = REPORT_NOT_FOUND,
            status = HttpStatusCode.NotFound,
        ).createInspectionApiService()
        val dataSource = InspectionRemoteDataSourceImpl(apiService, ApiQueryBuilderImpl(), ErrorParserImpl())

        val error = assertFailsWith<TaminApiException> { dataSource.getInspectionReportPDF("0130980012641") }

        assertEquals(HttpErrorCopy.NOT_FOUND, error.subtitle)
    }

    @Test
    fun getInspectionReportPDF_whenTheDownloadSucceeds_returnsTheFileBytes() = runTest {
        val pdf = byteArrayOf(0x25, 0x50, 0x44, 0x46)
        val apiService = createMockKtorfit(content = pdf, contentType = ContentType.Application.Pdf)
            .createInspectionApiService()
        val dataSource = InspectionRemoteDataSourceImpl(apiService, ApiQueryBuilderImpl(), ErrorParserImpl())

        val channel = assertNotNull(dataSource.getInspectionReportPDF("0130980012641").pdf?.pdf)

        assertContentEquals(pdf, channel.readRemaining().readByteArray())
    }

    private companion object {
        const val REPORT_NOT_FOUND =
            """{"status":404,"family":"CLIENT_ERROR","reason":"Not Found","data":null}"""
    }
}
