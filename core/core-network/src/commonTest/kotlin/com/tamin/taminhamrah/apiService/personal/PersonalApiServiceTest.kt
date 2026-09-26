package com.tamin.taminhamrah.apiService.personal

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorRequestDTO
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeRequest
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.personal.InsuredDocDTO
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.PersonalTestData
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PersonalApiServiceTest : BaseApiTest() {

    @Test
    fun `getPersonalInfo should parse girl survivor personal payload`() = runTest {
        val ktorfit = createMockKtorfit(
            ApiTestUtils.createJsonResponse(PersonalTestData.girlSurvivorPersonalSuccess)
        )
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.getPersonalInfo()

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals("0071234567", response.data.insuranceId)
        assertEquals("09121234567", response.data.mobileNumber)
        assertEquals("زهرا", response.data.personal?.firstName)
        assertEquals("محمدی", response.data.personal?.lastName)
        assertEquals("0012345678", response.data.personal?.nationalId)
        assertEquals("02", response.data.personal?.gender?.genderCode)
        assertEquals("456789", response.data.personal?.idCardNumber)
        assertEquals("1234567890", response.data.personal?.contacts?.firstOrNull()?.zipCode)
        assertEquals("تهران، خیابان آزادی، پلاک ۱۲", response.data.personal?.contacts?.firstOrNull()?.address)
    }

    @Test
    fun `getPersonalInfo should parse live survivor-request personal payload with relationWithTamins objects`() =
        runTest {
            val ktorfit = createMockKtorfit(PersonalTestData.survivorRequestPersonalLiveSuccess)
            val apiService = ktorfit.createPersonalApiService()

            val response = apiService.getPersonalInfo()

            assertEquals(200, response.status)
            assertNotNull(response.data)
            assertEquals("0012886024", response.data.insuranceId)
            assertEquals("علي", response.data.personal?.firstName)
            assertEquals("عيسي زاده", response.data.personal?.lastName)
            assertEquals("6360110032", response.data.personal?.nationalId)
            assertEquals("01", response.data.personal?.gender?.genderCode)
            assertEquals("1234567890", response.data.personal?.contacts?.firstOrNull()?.zipCode)
        }

    @Test
    fun `getDisabilityDependentInfo should parse tendencyCode and genderCode from nested baseTendency`() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "list": [
                        {
                            "relationWithTamin": {
                                "personal": {
                                    "firstName": "منصوره",
                                    "lastName": "آزادی",
                                    "nationalId": "0073160997",
                                    "gender": {
                                        "genderCode": "02",
                                        "genderDesc": "زن"
                                    }
                                },
                                "relationWithTamin": {
                                    "baseTendency": {
                                        "tendencyCode": "100",
                                        "tendencyDescription": "همسر"
                                    }
                                }
                            }
                        }
                    ]
                }
            }
        """.trimIndent()
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.getDisabilityDependentInfo(emptyMap())

        val dependent = response.data?.list?.firstOrNull()?.relationWithTamin
        assertEquals("منصوره", dependent?.personal?.firstName)
        assertEquals("02", dependent?.personal?.gender?.genderCode)
        assertEquals("100", dependent?.tendencyInfo?.baseTendency?.tendencyCode)
        assertEquals("همسر", dependent?.tendencyInfo?.baseTendency?.tendencyDescription)
    }

    @Test
    fun `checkGirlSurvivorConditions should surface bare Persian 500 body`() = runTest {
        val message = "اطلاعاتی از حکم مستمری یا فوت فرد مورد نظر شما یافت نشد."
        val ktorfit = createMockKtorfit(
            content = message,
            status = HttpStatusCode.InternalServerError,
        )
        val apiService = ktorfit.createPersonalApiService()

        val exception = assertFailsWith<TaminErrorUriException> {
            apiService.checkGirlSurvivorConditions("6360110032", "04", "")
                .extractMessage()
        }

        assertEquals(message, exception.serverMessage)
    }

    @Test
    fun `checkGirlSurvivorConditions should handle null data and return success status`() = runTest {
        val ktorfit = createMockKtorfit(PersonalTestData.girlSurvivorConditionSuccess)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.checkGirlSurvivorConditions(
            nationalCode = "0012345678",
            relation = "04",
            pensionerId = "1234567890",
        )

        assertEquals(200, response.status)
        assertNull(response.data)
        assertEquals("OK", response.extractMessage())
    }

    @Test
    fun `checkGirlSurvivorConditions should surface ineligible problem envelope`() = runTest {
        val ktorfit = createMockKtorfit(PersonalTestData.girlSurvivorConditionIneligible)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.checkGirlSurvivorConditions("0012345678", "04", "1234567890")

        assertEquals(400, response.status)
        assertEquals(response.problems?.isNotEmpty(), true)
        assertEquals("فرد مشمول تعهدنامه فرزندان دختر نیست", response.problems?.firstOrNull()?.errorMsg)
    }

    @Test
    fun `getGirlSurvivorReport should stream pdf bytes`() = runTest {
        val ktorfit = createMockKtorfit(
            content = PersonalTestData.girlSurvivorReportPdfBytes,
            contentType = ContentType.Application.Pdf,
        )
        val apiService = ktorfit.createPersonalApiService()

        val statement = apiService.getGirlSurvivorReport(
            address = "تهران",
            tel = "02166778899",
            postalCode = "1234567890",
            fatherName = "علی",
            birthDate = 631152000000,
            insuranceId = "0071234567",
            parentCode = "0012345678",
            pensionerId = "",
        )

        statement.execute { response ->
            assertEquals(200, response.status.value)
            assertEquals(
                PersonalTestData.girlSurvivorReportPdfBytes.toList(),
                response.readRawBytes().toList(),
            )
        }
    }

    @Test
    fun `getGirlSurvivorReport should return error status`() = runTest {
        val ktorfit = createMockKtorfit(
            content = ByteArray(0),
            status = HttpStatusCode.InternalServerError,
            contentType = ContentType.Application.Pdf,
        )
        val apiService = ktorfit.createPersonalApiService()

        val statement = apiService.getGirlSurvivorReport(
            address = "a",
            tel = "0",
            postalCode = "1",
            fatherName = null,
            birthDate = null,
            insuranceId = null,
            parentCode = "001",
            pensionerId = "002",
        )

        statement.execute { response ->
            assertEquals(500, response.status.value)
        }
    }

    @Test
    fun `confirmGirlSurvivor should return success string data`() = runTest {
        val ktorfit = createMockKtorfit(PersonalTestData.girlSurvivorConfirmSuccess)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.confirmGirlSurvivor(sampleConfirmRequest())

        assertEquals(200, response.status)
        assertEquals("درخواست با موفقیت ثبت شد", response.extractMessage())
    }

    @Test
    fun `confirmGirlSurvivor should fall back to reason when data is null`() = runTest {
        val ktorfit = createMockKtorfit(PersonalTestData.girlSurvivorConfirmNullData)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.confirmGirlSurvivor(sampleConfirmRequest(pensionId = "1234567890"))

        assertEquals(200, response.status)
        assertNull(response.data)
        assertEquals("OK", response.extractMessage())
    }

    @Test
    fun `submitFinalSurvivorPension should handle string data`() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESSFUL",
                "reason": "OK",
                "data": "Operation successful"
            }
        """.trimIndent()
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.submitFinalSurvivorPension(1, SubmitFinalSurvivorPensionRequest(1))

        assertEquals(200, response.status)
        assertEquals("Operation successful", response.extractMessage())
    }

    @Test
    fun `getFinalSurvivorPensionPDF should return http statement`() = runTest {
        val ktorfit = createMockKtorfit("")
        val apiService = ktorfit.createPersonalApiService()

        val statement = apiService.getFinalSurvivorPensionPDF()
        statement.execute { response ->
            assertEquals(200, response.status.value)
        }
    }

    @Test
    fun `getFinalSurvivorPensionPDF should return error status`() = runTest {
        val ktorfit = createMockKtorfit("", status = HttpStatusCode.InternalServerError)
        val apiService = ktorfit.createPersonalApiService()

        val statement = apiService.getFinalSurvivorPensionPDF()
        statement.execute { response ->
            assertEquals(500, response.status.value)
        }
    }

    @Test
    fun `putInsuredRegistrationDocList should return success string`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PersonalTestData.insuredDocSuccess
        )
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.putInsuredRegistrationDocList(
            "123",
            listOf(InsuredDocDTO(null, null, null))
        )

        assertEquals(200, response.status)
        assertEquals("Success", response.data)
    }

    @Test
    fun `getRequestSummary should return new insured summary`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = PersonalTestData.requestSummarySuccess
        )
        val ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createPersonalApiService()

        val response = apiService.getRequestSummary("req_123")

        assertEquals(200, response.status)
        assertEquals("REF123", response.data?.refCode)
        assertEquals("0000000000", response.data?.nationalId)
    }

    private fun sampleConfirmRequest(
        nationalCode: String? = "9988776655",
        pensionId: String? = null,
    ) = ConfirmGirlSurvivorRequestDTO(
        address = "تهران، خیابان آزادی، پلاک ۱۲",
        age = "33",
        birthDate = 631152000000,
        childInsuranceId = "0071234567",
        childNationalId = "0012345678",
        deathDate = null,
        deathType = null,
        dependencyType = DependencyTypeRequest(code = "04"),
        firstName = "زهرا",
        gender = "02",
        idNumber = "456789",
        insuranceNumber = "0071234567",
        lastName = "محمدی",
        mobileNumber = "09121234567",
        nationalCode = nationalCode,
        pensionId = pensionId,
        phoneNumber = "02166778899",
        status = "0",
    )
}
