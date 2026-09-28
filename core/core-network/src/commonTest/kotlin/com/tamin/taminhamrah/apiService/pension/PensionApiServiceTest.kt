package com.tamin.taminhamrah.apiService.pension

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSourceImpl
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementSaveDocumentRequest
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilderImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.HttpErrorCopy
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.PensionTestData
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.jsonPrimitive

class PensionApiServiceTest : BaseApiTest() {

    @Test
    fun `getPensionerPayRoll should return payroll data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.payrollSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getPensionerPayRoll("filter-json")

        assertEquals(200, response.status)
        assertEquals(1, response.data?.list?.first()?.id)
        assertEquals("Type A", response.data?.list?.first()?.clpType)
        assertEquals(5000000L, response.data?.list?.first()?.sumAmount)
        assertEquals("1402", response.data?.list?.first()?.hisYear)
    }

    @Test
    fun `getPensionerPayRoll should return error status`() = runTest {
        val family = "SERVER_ERROR"
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = "{}",
            status = 500,
            family = family,
            reason = "Internal Server Error"
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getPensionerPayRoll("filter-json")

        assertEquals(500, response.status)
        assertEquals(family, response.family)
    }

    @Test
    fun `getDisabilityPersonalInfo should return personal info data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.disabilityPersonalInfoSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getDisabilityPersonalInfo()

        assertEquals(200, response.status)
        assertEquals("Ali", response.data?.personal?.firstName)
        assertEquals("Alavi", response.data?.personal?.lastName)
        assertEquals("0012345678", response.data?.personal?.nationalId)
        assertEquals("09121234567", response.data?.mobileNumber)
    }

    @Test
    fun `getUserAge should return age data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.userAgeSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getUserAge(emptyMap())

        assertEquals(200, response.status)
        assertEquals("30", response.data?.age)
        assertEquals("1370/01/01", response.data?.birthDate)
    }

    @Test
    fun `getRetirementRequestInfo should return retirement request data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.retirementRequestInfoSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getRetirementRequestInfo(emptyMap())

        assertEquals(200, response.status)
        assertEquals(1, response.data?.list?.size)
        assertEquals("Ali", response.data?.list?.first()?.firstName)
        assertEquals("Alavi", response.data?.list?.first()?.lastName)
        assertEquals("0012345678", response.data?.list?.first()?.nationalCode)
    }

    @Test
    fun `pensionerPayRollPDF should return http response`() = runTest {
        val ktorfit = createMockKtorfit("")
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.pensionerPayRollPDF(emptyMap())

        assertEquals(200, response.execute().status.value)
    }

    @Test
    fun `authenticationAndGetPersonalInfo should return personal info data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.authenticationAndGetPersonalInfoSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.authenticationAndGetPersonalInfo(123456L)

        assertEquals(200, response.status)
        assertEquals("Ali", response.data?.personal?.firstName)
        assertEquals("Alavi", response.data?.personal?.lastName)
        assertEquals("Engineer", response.data?.work?.job?.jobDescription)
    }

    @Test
    fun `checkRetirementStatus should return retirement status data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.retirementStatusSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.checkRetirementStatus()

        assertEquals(200, response.status)
        assertEquals("123", response.data?.requestId)
        assertEquals("1", response.data?.requestStatusCode)
    }

    @Test
    fun `sendRetirementDocument should return success data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.sendRetirementDocumentSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.sendRetirementDocument("requestId", RetirementSaveDocumentRequest())

        assertEquals(200, response.status)
        assertEquals("Success", response.data)
    }

    @Test
    fun `getAuthenticationCode should return ticket data`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.getAuthenticationCode
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.getAuthenticationCode()

        assertEquals(200, response.status)
        assertEquals("09123456789", response.data?.mobileNumber)
    }

    @Test
    fun `sendEdictPensionerToMyInbox should return success message`() = runTest {
        val successMessage = "عملیات با موفقیت انجام شد"
        val jsonResponse = PensionTestData.sendEdictToInboxSuccess

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.sendEdictPensionerToMyInbox(emptyMap())

        assertEquals(200, response.status)
        assertEquals(
            successMessage,
            response.data?.jsonPrimitive?.content
        )
    }

    @Test
    fun `sendRequestInquirePensionCertificate should return success message`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PensionTestData.inquirePensionCertificateSuccess
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.sendRequestInquirePensionCertificate(emptyMap())

        assertEquals(200, response.status)
        assertEquals("درخواست شما با موفقیت ثبت شد", response.data?.jsonPrimitive?.content)
    }

    @Test
    fun `sendRequestInquirePensionCertificate should return error on 400`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = "null",
            status = 400,
            family = "CLIENT_ERROR",
            reason = "Bad Request"
        )

        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPensionApiService()

        val response = apiService.sendRequestInquirePensionCertificate(emptyMap())

        assertEquals(400, response.status)
        assertEquals("CLIENT_ERROR", response.family)
    }

    /** The edict report drains the same way: a failed download is a mapped error, not the viewer's input. */
    @Test
    fun getEdictReportPDF_whenTheDownloadFails_throwsTheMappedError() = runTest {
        val apiService = createMockKtorfit(
            content = """{"status":404,"family":"CLIENT_ERROR","reason":"Not Found","data":null}""",
            status = HttpStatusCode.NotFound,
        ).createPensionApiService()
        val dataSource = PensionRemoteDataSourceImpl(apiService, ApiQueryBuilderImpl(), ErrorParserImpl())

        val error = assertFailsWith<TaminApiException> { dataSource.getEdictReportPDF(emptyList()) }

        assertEquals(HttpErrorCopy.NOT_FOUND, error.subtitle)
    }
}
