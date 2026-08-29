package com.tamin.taminhamrah.apiService.inspection

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.InspectionTestData
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
}

