package com.tamin.taminhamrah.apiService.inquiryEducation

import com.tamin.taminhamrah.apiService.BaseApiTest
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InquiryEducationApiServiceTest : BaseApiTest() {

    @Test
    fun getDataForEducation_returnsDependentsList() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "total": 1,
                    "list": [
                        {
                            "relationWithTamin": {
                                "personal": {
                                    "firstName": "علی",
                                    "lastName": "رضایی",
                                    "nationalId": "0012345678",
                                    "gender": { "genderCode": "01" },
                                    "subDominant": { "dateOfExpire": "1735689600000" }
                                },
                                "relationWithTamin": {
                                    "baseTendency": {
                                        "tendencyCode": "101",
                                        "tendencyDescription": "پسر"
                                    }
                                }
                            }
                        }
                    ]
                }
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createInquiryEducationApiService()

        val response = apiService.getDataForEducation()

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(1, response.data?.total)
        assertEquals(1, response.data?.list?.size)
        val personal = response.data?.list?.first()?.relationWithTamin?.personal
        assertEquals("علی", personal?.firstName)
        assertEquals("رضایی", personal?.lastName)
        assertEquals("0012345678", personal?.nationalId)
        assertEquals(
            "101",
            response.data?.list?.first()?.relationWithTamin?.relationWithTamin?.baseTendency?.tendencyCode
        )
    }

    @Test
    fun inquiryEducationCertificate_returnsMessage() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": "گواهی معتبر است"
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createInquiryEducationApiService()

        val response = apiService.inquiryEducationCertificate(
            code = "0012345678",
            educationCode = "EDU123"
        )

        assertEquals(200, response.status)
        assertEquals("گواهی معتبر است", response.data)
    }

    @Test
    fun inquiryEducationCertificate_nullData_isAllowed() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": null
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createInquiryEducationApiService()

        val response = apiService.inquiryEducationCertificate(
            code = "1",
            educationCode = "EDU123"
        )

        assertEquals(200, response.status)
        assertNull(response.data)
    }
}
