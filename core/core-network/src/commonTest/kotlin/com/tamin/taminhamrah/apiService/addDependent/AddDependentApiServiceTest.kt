package com.tamin.taminhamrah.apiService.addDependent

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDto
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AddDependentApiServiceTest : BaseApiTest() {

    @Test
    fun getActiveBranches_returnsActiveBranchesList() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": [
                    {
                        "branchCode": "101",
                        "branchName": "شعبه 1 تهران"
                    }
                ]
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createAddDependentApiService()

        val response = apiService.getActiveBranches()

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(1, response.data?.size)
        assertEquals("101", response.data?.first()?.branchCode)
        assertEquals("شعبه 1 تهران", response.data?.first()?.branchName)
    }

    @Test
    fun getFamilyRelationships_returnsFamilyRelationshipsList() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": [
                    {
                        "dependencyCode": "01",
                        "dependencyDesc": "همسر"
                    }
                ]
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createAddDependentApiService()

        val response = apiService.getFamilyRelationships()

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals(1, response.data?.size)
        assertEquals("01", response.data?.first()?.relationCode)
        assertEquals("همسر", response.data?.first()?.relationDesc)
    }

    @Test
    fun inquiryRegistry_returnsRegistryData() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "firstName": "علی",
                    "lastName": "رضایی",
                    "fatherName": "محمد",
                    "birthDate": "1370/01/01"
                }
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createAddDependentApiService()

        val response = apiService.inquiryRegistry(
            dependentNationalId = "0012345678",
            birthDateTimeStamp = "662688000",
            dependencyCode = "01"
        )

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals("علی", response.data?.firstName)
        assertEquals("رضایی", response.data?.lastName)
    }

    @Test
    fun inquiryEducationCode_returnsEducationStatus() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": "تایید شده"
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createAddDependentApiService()

        val response = apiService.inquiryEducationCode(
            nationalId = "0012345678",
            educationCode = "EDU123"
        )

        assertEquals(200, response.status)
        assertEquals("تایید شده", response.data)
    }

    @Test
    fun uploadImage_returnsUploadImageResponse() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "guid": "FILE_9988",
                    "isSuccess": true
                }
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createAddDependentApiService()

        val multipartBody = MultiPartFormDataContent(
            formData {
                append("file", byteArrayOf(1, 2, 3), Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=\"img.png\"")
                })
            }
        )

        val response = apiService.uploadImage(multipartBody)

        assertNotNull(response)
        assertEquals("a4769aa8-b9af-4183-83b9-367dc9f52511", response.guid)
    }

    @Test
    fun addNewDependent_returnsGeneralResponse() = runTest {
        val jsonResponse = """
            {
                "status": 200,
                "family": "SUCCESS",
                "reason": "OK",
                "data": {
                    "isSuccess": true,
                    "message": "کفالت با موفقیت ثبت شد"
                }
            }
        """.trimIndent()

        val ktorfit: Ktorfit = createMockKtorfit(jsonResponse)
        val apiService = ktorfit.createAddDependentApiService()

        val response = apiService.addNewDependent(
            RequestAddDependentDto(
                nationalId = "0012345678"
            )
        )

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertTrue(response.data?.isSuccess == true)
        assertEquals("کفالت با موفقیت ثبت شد", response.data?.message)
    }
}
