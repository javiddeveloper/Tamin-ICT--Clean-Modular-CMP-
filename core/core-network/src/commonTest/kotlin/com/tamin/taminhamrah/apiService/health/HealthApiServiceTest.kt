package com.tamin.taminhamrah.apiService.health

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.health.*
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.HealthTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HealthApiServiceTest : BaseApiTest() {

    private fun createCustomMockKtorfit(
        content: String,
        status: HttpStatusCode = HttpStatusCode.OK,
        contentType: ContentType = ContentType.Application.Json
    ): Ktorfit {
        val mockEngine = MockEngine { _ ->
            respond(
                content = content,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, contentType.toString())
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                })
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }

        return Ktorfit.Builder()
            .httpClient(httpClient)
            .baseUrl("https://api.tamin.ir/")
            .build()
    }

    @Test
    fun `getPatientGeneral should return patient general response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.patientGeneralSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()

        val response = apiService.getPatientGeneral("6319889391")

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        val data = response.extractData()
        assertNotNull(data)
        assertEquals("John", data.patientName)
        assertEquals("Doe", data.patientFamily)
        assertEquals("6319889391", data.patientNatCode)
    }

    @Test
    fun `getPatientSelfDeclarative should return self declarative response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.patientSelfDeclarativeSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()

        val response = apiService.getPatientSelfDeclarative("6319889391", 1)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        val data = response.extractData()
        assertNotNull(data)
        assertEquals("No", data.alcoholDesc)
        assertEquals("Weekly", data.exerciseFreqTitle)
    }

    @Test
    fun `getPatientDrugAllergies should return drug allergies response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.patientDrugAllergiesSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()

        val response = apiService.getPatientDrugAllergies("6319889391", 1)

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        val data = response.extractData()
        assertNotNull(data)
        assertEquals(1, data.list?.size)
        assertEquals("Drug", data.list?.firstOrNull()?.drugName)
    }

    @Test
    fun `getSelfDeclarativeIllness should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getSelfDeclarativeIllness(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientHealthData should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientHealthData("6319889391")
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientDrug should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientDrug(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getDrugDelivery should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getDrugDelivery(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientCommission should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientCommission(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientHospitalize should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientHospitalize(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientVisit should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientVisit(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientLab should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientLab(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getLabDelivery should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getLabDelivery(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientImaging should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientImaging(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getImagingDelivery should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getImagingDelivery(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientSurgeries should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientSurgeries(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPatientPhysio should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPatientPhysio(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `getPhysioDelivery should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyListSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getPhysioDelivery(emptyMap())
        assertNotNull(response.extractData())
    }

    @Test
    fun `updatePatient should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val request = UpdatePatientRequestDTO(
            emergencyAddress = null, emergencyArea = null, emergencyCityID = null, emergencyEmail = null,
            emergencyFamily = null, emergencyMobile = null, emergencyName = null, emergencyRelation = null,
            patientAddress = null, patientArea = null, patientBloodGroup = null, patientCitizenship = null,
            patientCityID = null, patientEmail = null, patientHeight = null, patientID = null,
            patientInsurance = null, patientJob = null, patientMarriage = null, patientMobile = null,
            patientNatCode = null, patientNationality = null, patientWeight = null
        )
        val response = apiService.updatePatient(request)
        assertNotNull(response.extractData())
    }

    @Test
    fun `syncIllnessSelfDeclaratives should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse("\"Success\"")
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val request = SyncIllnessesSelfDecRequestDTO(
            illnessSelfDeclareList = null, natCode = null, patientID = null
        )
        val response = apiService.syncIllnessSelfDeclaratives(request)
        assertEquals("Success", response.extractMessage())
    }

    @Test
    fun `syncDrugAllergies should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse("\"Success\"")
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val request = SyncDrugAllergiesRequestDTO(
            drugAllergyList = null, natCode = null, patientID = null
        )
        val response = apiService.syncDrugAllergies(request)
        assertEquals("Success", response.extractMessage())
    }

    @Test
    fun `addSelfDeclarative should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val request = AddSelfDeclarativeRequestDTO(
            alcoholUse = null, alcoholUseDesc = null, exerciseDesc = null, exerciseFrequency = null,
            natCode = null, patientID = null, smokeDesc = null, smoking = null,
            substanceUse = null, substanceUseDesc = null
        )
        val response = apiService.addSelfDeclarative(request)
        assertNotNull(response.extractData())
    }

    @Test
    fun `updateSelfDeclarative should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val request = UpdateSelfDeclarativeRequestDTO(
            alcoholUse = null, alcoholUseDesc = null, exerciseDesc = null, exerciseFrequency = null,
            objectID = null, patientID = null, smokeDesc = null, smoking = null,
            substanceUse = null, substanceUseDesc = null
        )
        val response = apiService.updateSelfDeclarative(request)
        assertNotNull(response.extractData())
    }


    @Test
    fun `getSelfDeclarableIllnesses should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getSelfDeclarableIllnesses()
        assertNotNull(response.extractData())
    }

    @Test
    fun `getSelfDeclarableIllnessesByGroup should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getSelfDeclarableIllnessesByGroup()
        assertNotNull(response.extractData())
    }

    @Test
    fun `getAllProvinces should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getAllProvinces()
        assertNotNull(response.extractData())
    }

    @Test
    fun `getProvinceCities should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getProvinceCities(1)
        assertNotNull(response.extractData())
    }

    @Test
    fun `getAllergicDrugs should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyObjectSuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getAllergicDrugs()
        assertNotNull(response.extractData())
    }

    @Test
    fun `getGenderTypes should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyArraySuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getGenderTypes()
        assertNotNull(response.extractData())
    }

    @Test
    fun `getMaritalStatus should return response`() = runTest {
        val jsonResponse = "[]"
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getMaritalStatus()
        assertNotNull(response)
    }

    @Test
    fun `getRelationTypes should return response`() = runTest {
        val jsonResponse = HealthTestData.emptyArraySuccess
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getRelationTypes()
        assertNotNull(response)
    }

    @Test
    fun `getIllnessGroups should return response`() = runTest {
        val jsonResponse = ApiTestUtils.createJsonResponse(HealthTestData.emptyArraySuccess)
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getIllnessGroups()
        assertNotNull(response.extractData())
    }

    @Test
    fun `getActFrequencies should return response`() = runTest {
        val jsonResponse = HealthTestData.emptyArraySuccess
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getActFrequencies()
        assertNotNull(response)
    }

    @Test
    fun `getSmokingStatus should return response`() = runTest {
        val jsonResponse = HealthTestData.emptyArraySuccess
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getSmokingStatus()
        assertNotNull(response)
    }

    @Test
    fun `getBloodGroups should return response`() = runTest {
        val jsonResponse =HealthTestData.emptyArraySuccess
        val ktorfit = createCustomMockKtorfit(jsonResponse)
        val apiService = ktorfit.createHealthApiService()
        val response = apiService.getBloodGroups()
        assertNotNull(response)
    }
}
