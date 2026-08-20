package com.tamin.taminhamrah.apiService.occurrence

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.occurrence.OccurrenceRequestDTO
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.OccurrenceTestData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OccurrenceApiServiceTest : BaseApiTest() {

    @Test
    fun getPersonalInfo_returnsPersonalInfo() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = OccurrenceTestData.personalInfoSuccess
        )

        val apiService = createMockKtorfit(jsonResponse).createOccurrenceApiService()
        val response = apiService.getPersonalInfo(emptyMap())

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertEquals("0012345678", response.data?.nationalCode)
        assertEquals("علی", response.data?.firstName)
        assertEquals("اصلی", response.data?.insuranceType)
    }

    @Test
    fun getAllWorkshops_returnsListOfRawWorkshopArrays() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = OccurrenceTestData.allWorkshopsSuccess
        )

        val apiService = createMockKtorfit(jsonResponse).createOccurrenceApiService()
        val response = apiService.getAllWorkshops(emptyMap())

        assertEquals(200, response.status)
        val list = assertNotNull(response.data?.list)
        assertEquals(1, list.size)
        assertEquals("1412345", list.first().getOrNull(0)?.jsonPrimitive?.contentOrNull)
        assertEquals("014", list.first().getOrNull(2)?.jsonPrimitive?.contentOrNull)
    }

    @Test
    fun getWorkshopSpec_returnsWorkshopSpec() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = OccurrenceTestData.workshopSpecSuccess
        )

        val apiService = createMockKtorfit(jsonResponse).createOccurrenceApiService()
        val response = apiService.getWorkshopSpec(emptyMap())

        assertEquals(200, response.status)
        assertEquals("1412345", response.data?.workshopCode)
        assertEquals("کارگاه تولیدی الف", response.data?.name)
        assertEquals("ایرانی", response.data?.nation?.nationDesc)
    }

    @Test
    fun getInsuredRelation_returnsInsuredRelation() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = OccurrenceTestData.insuredRelationSuccess
        )

        val apiService = createMockKtorfit(jsonResponse).createOccurrenceApiService()
        val response = apiService.getInsuredRelation(emptyMap())

        assertEquals(200, response.status)
        assertEquals("01", response.data?.insuranceTypeCode)
        assertEquals("شعبه مرکزی", response.data?.branchName)
    }

    @Test
    fun getDocumentTypes_returnsDocumentTypesList() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = OccurrenceTestData.documentTypesSuccess
        )

        val apiService = createMockKtorfit(jsonResponse).createOccurrenceApiService()
        val response = apiService.getDocumentTypes(emptyMap())

        assertEquals(200, response.status)
        val list = assertNotNull(response.data?.list)
        assertEquals(1, list.size)
        assertEquals("1", list.first().docTypeId)
        assertEquals("گزارش حادثه", list.first().docDesc)
    }

    @Test
    fun uploadImage_returnsGuid() = runTest {
        val jsonResponse = OccurrenceTestData.uploadImageSuccess

        val apiService = createMockKtorfit(jsonResponse).createOccurrenceApiService()
        val multipartBody = MultiPartFormDataContent(
            formData {
                append("file", byteArrayOf(1, 2, 3), Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"img.jpg\"")
                })
            }
        )

        val response = apiService.uploadImage(multipartBody)

        assertEquals("a4769aa8-b9af-4183-83b9-367dc9f52511", response.guid)
    }

    @Test
    fun submitOccurrence_returnsTrackingCode() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(
            dataJson = OccurrenceTestData.submitOccurrenceSuccess
        )

        val apiService = createMockKtorfit(jsonResponse).createOccurrenceApiService()
        val response = apiService.submitOccurrence(sampleRequestDTO())

        assertEquals(200, response.status)
        assertEquals("TRACK-123", response.data?.reportRefrenceNumber)
    }

    private fun sampleRequestDTO() = OccurrenceRequestDTO(
        birthDate = "662688000",
        bossFullName = "شرکت الف",
        bossMobileNumber = "02112345678",
        branchCode = "10",
        branchName = "شعبه مرکزی",
        employeeDate = "662688000",
        gender = 1,
        insuranceID = "1234567",
        isuTypeDesc = "اصلی",
        isuTypecode = "01",
        jobDesc = "کارگر",
        marriageStatusCode = 1,
        nationCode = 1,
        occurrenceAddress = "طبقه دوم",
        occurrenceDate = "662688000",
        occurrenceDesc = "توضیحات حادثه",
        occurrenceDocumentList = emptyList(),
        occurrenceResult = 1,
        occurrenceTime = "10:30",
        pFirstName = "علی",
        pLastName = "رضایی",
        pNationalCode = "0012345678",
        reportAddress = "تهران",
        reportJobLocation = "خط تولید",
        reportPostalCode = "1234567891",
        reportTelephone = "02112345679",
        reporterType = "1",
        rwworkfinish = "16:00",
        rwworkstart = "08:00",
        vehicle = "شخصی",
        workshopAddress = "تهران",
        workshopBranchCode = "014",
        workshopCode = "1412345",
        workshopName = "کارگاه الف",
        workshopPostalCode = "1234567890",
        workshopTelephone = "02112345678",
    )
}
