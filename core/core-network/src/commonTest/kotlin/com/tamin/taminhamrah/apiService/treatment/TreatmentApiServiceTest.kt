package com.tamin.taminhamrah.apiService.treatment

import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.TreatmentTestData
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TreatmentApiServiceTest {

    private lateinit var interceptedUrl: String
    private lateinit var interceptedMethod: String
    private lateinit var responseContent: String
    private var responseBytes: ByteArray? = null

    private fun createApiService(): TreatmentApiService {
        val mockEngine = MockEngine { request ->
            interceptedUrl = request.url.toString()
            interceptedMethod = request.method.value
            val response = responseBytes
            if (response != null) {
                respond(
                    content = response,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())
                )
            } else {
                respond(
                    content = responseContent,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
            defaultRequest {
                url("https://eservices.tamin.ir/api/")
            }
        }

        val ktorfit = Ktorfit.Builder()
            .httpClient(httpClient)
            .build()

        return ktorfit.createTreatmentApiService()
    }

    @BeforeTest
    fun setup() {
        interceptedUrl = ""
        interceptedMethod = ""
        responseContent = ""
        responseBytes = null
    }

    @Test
    fun testGetDeservedTreatment() = runTest {
        responseContent = ApiTestUtils.createJsonResponse(TreatmentTestData.deservedTreatmentSuccess)
        val apiService = createApiService()
        val result = apiService.getDeservedTreatment("6319889391")

        assertEquals("GET", interceptedMethod)
        assertEquals("https://eservices.tamin.ir/api/booklet-req/lackEntitlement/6319889391", interceptedUrl)
        val data = result.extractData()
        assertNotNull(data)
        assertEquals(1, data.list?.size)
        val firstItem = data.list?.firstOrNull()
        assertNotNull(firstItem)
        assertEquals("رضا", firstItem.firstName)
        assertEquals("احمدی", firstItem.lastName)
        assertEquals("1234567890", firstItem.nationalId)
    }

    @Test
    fun testGetDependantUnderEighteen() = runTest {
        responseContent = ApiTestUtils.createJsonResponse(TreatmentTestData.dependantsSuccess)
        val apiService = createApiService()
        val result = apiService.getDependantUnderEighteen(
            nationalCode = "6319889391",
            parameters = emptyMap()
        )

        assertEquals("GET", interceptedMethod)
        assertEquals("https://eservices.tamin.ir/api/patient-history/get-dependent-children/6319889391", interceptedUrl)
        val data = result.extractData()
        assertNotNull(data)
        assertEquals(1, data.list?.size)
        val firstItem = data.list?.firstOrNull()
        assertNotNull(firstItem)
        assertEquals("سارا", firstItem.relationWithTamin?.personal?.firstName)
        assertEquals("0987654321", firstItem.relationWithTamin?.personal?.nationalId)
    }

    @Test
    fun testGetTreatmentCosts() = runTest {
        responseContent = ApiTestUtils.createJsonResponse(TreatmentTestData.costsSuccess)
        val apiService = createApiService()
        val result = apiService.getTreatmentCosts(emptyMap())

        assertEquals("GET", interceptedMethod)
        assertEquals("https://eservices.tamin.ir/api/health/tcr-price-certificate", interceptedUrl)
        val data = result.extractData()
        assertNotNull(data)
        assertEquals(2, data.list?.size)
        val firstItem = data.list?.firstOrNull()
        assertNotNull(firstItem)
        assertEquals(1, firstItem.repId)
        assertEquals("رضا احمدی", firstItem.nameFamil)
        assertEquals("پرداخت شده", firstItem.payStatusDesc)
    }

    @Test
    fun testGetTreatmentCostsPDF() = runTest {
        val pdfBytes = byteArrayOf(7, 8, 9)
        responseBytes = pdfBytes
        val apiService = createApiService()
        val executed = apiService.getTreatmentCostsPDF("1").execute()

        assertEquals("GET", interceptedMethod)
        assertEquals("https://eservices.tamin.ir/api/health/tcr-price-certificate/report/1", interceptedUrl)
        assertEquals(200, executed.status.value)
    }

    @Test
    fun testSendToInboxTreatmentCosts() = runTest {
        responseContent = ApiTestUtils.createJsonResponse(TreatmentTestData.sendToInboxSuccess)
        val apiService = createApiService()
        val result = apiService.sendToInboxTreatmentCosts("1")

        assertEquals("GET", interceptedMethod)
        assertEquals("https://eservices.tamin.ir/api/health/tcr-price-certificate/announcement/1", interceptedUrl)
        assertEquals("SUCCESS", result.extractData())
    }
}
