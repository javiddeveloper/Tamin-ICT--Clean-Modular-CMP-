package com.tamin.taminhamrah.data.repository.workshops

import com.tamin.taminhamrah.data.mapper.toDto
import com.tamin.taminhamrah.data.repository.WorkShopsRepositoryImpl
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.SettlementDocumentDN
import com.tamin.taminhamrah.model.workshop.SettlementRequestDN
import com.tamin.taminhamrah.model.workshop.SettlementRequestDTO
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDTO
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * [WorkShopsRepositoryImpl] for درخواست مفاصاحساب, and the body it puts on the wire.
 *
 * The old app is the only record of this contract, and three of its details break without a sound —
 * the service answers 200 either way: the request is a PUT onto an id the client composes, every date
 * carries a fixed `T19:30` stamp, and `subjectamount2` changes JSON type with the subject.
 */
class WorkShopsRepositorySettlementTest {

    /** The app's own configuration, which does not encode defaults — what the DTO is built around. */
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private lateinit var remote: FakeRemote
    private lateinit var repository: WorkShopsRepositoryImpl

    @BeforeTest
    fun setup() {
        remote = FakeRemote()
        repository = WorkShopsRepositoryImpl(remote)
    }

    @Test
    fun `the request is addressed by five keys joined with TT in the old app order`() = runTest {
        repository.submitSettlementRequest(request(subjectCode = "04"))

        assertEquals("0082810145TT02100001TT0210TT01TT04", remote.lastId)
    }

    @Test
    fun `dates carry the old picker's fixed time on the gregorian day`() = runTest {
        repository.submitSettlementRequest(request())

        val body = assertNotNull(remote.lastRequest)
        assertEquals("2024-03-19T19:30:00.000Z", body.letterDate)
        assertEquals("2023-04-01T19:30:00.000Z", body.startDate)
        assertEquals("2024-03-10T19:30:00.000Z", body.endDate)
    }

    @Test
    fun `amounts travel as digit strings and the total as their sum`() {
        val body = request(amount = 1_000_000, currencyAmount = 50, currencyAmountInRial = 250_000).toDto()

        assertEquals("1000000", body.amount)
        assertEquals("50", body.currencyAmount)
        assertEquals("250000", body.currencyAmountInRial)
        assertEquals(1_250_000.0, body.totalAmount)
    }

    @Test
    fun `documents name their route and any image sets hasLetImage`() {
        val mixed = request(
            documents = listOf(
                SettlementDocumentDN(documentId = "pdf-1", categoryCode = "4", isPdf = true),
                SettlementDocumentDN(documentId = "guid-1", categoryCode = "1", isPdf = false),
            ),
        ).toDto()

        assertEquals(listOf("2", "1"), mixed.documents.map { it.documentType })
        assertEquals(listOf("4", "1"), mixed.documents.map { it.documentCode })
        assertTrue(mixed.hasLetterImage)

        val pdfOnly = request(
            documents = listOf(SettlementDocumentDN("pdf-1", "4", isPdf = true)),
        ).toDto()
        assertFalse(pdfOnly.hasLetterImage)
    }

    @Test
    fun `subjectamount2 is a number for 04 to 07, text for 11 and absent when blank`() {
        val drivers = encode(request(subjectCode = "04", subjectAmount2 = "1200"))
        assertFalse(drivers.getValue("subjectamount2").jsonPrimitive.isString)
        assertEquals(JsonPrimitive(1200L), drivers["subjectamount2"])

        val buildCosts = encode(request(subjectCode = "11", subjectAmount2 = "300"))
        assertEquals(JsonPrimitive("300"), buildCosts["subjectamount2"])

        val none = encode(request(subjectCode = "13", subjectAmount2 = ""))
        assertFalse("subjectamount2" in none)
    }

    /** Gson sent the empty strings as well; a default on the DTO would drop them from the body. */
    @Test
    fun `blank fields are still sent under the services own names`() {
        val body = encode(request(subjectOwner = "", subjectText1 = ""))

        assertEquals(JsonPrimitive(""), body["subjectOwner"])
        assertEquals(JsonPrimitive(""), body["subjecttext1"])
        assertEquals(JsonPrimitive("0082810145"), body["natcodecontract"])
        assertEquals(JsonPrimitive("1"), body["subcontractor"])
        assertEquals(JsonPrimitive("12345"), body["letno"])
        assertEquals(JsonPrimitive("1000"), body["cntamount"])
    }

    @Test
    fun `subjects without a code are dropped`() = runTest {
        remote.subjects = ListData(
            total = 2,
            list = listOf(
                SettlementSubjectDTO(code = "01", description = "عمرانی"),
                SettlementSubjectDTO(code = null, description = "بدون کد"),
            ),
        )

        assertEquals(listOf("01"), repository.getSettlementSubjects().map { it.code })
    }

    private fun encode(request: SettlementRequestDN): JsonObject =
        json.encodeToJsonElement(request.toDto()).jsonObject

    private fun request(
        subjectCode: String = "01",
        subjectOwner: String = "1",
        subjectText1: String = "",
        subjectAmount2: String = "",
        amount: Long = 1000,
        currencyAmount: Long = 0,
        currencyAmountInRial: Long = 0,
        documents: List<SettlementDocumentDN> = emptyList(),
    ) = SettlementRequestDN(
        workshopId = "0082810145",
        contractRow = "02100001",
        branchCode = "0210",
        contractSequence = "01",
        letterNumber = "12345",
        letterDate = "2024-03-19",
        startDate = "2023-04-01",
        endDate = "2024-03-10",
        hasSubcontractor = true,
        amount = amount,
        currencyAmount = currencyAmount,
        currencyAmountInRial = currencyAmountInRial,
        documents = documents,
        subjectCode = subjectCode,
        subjectOwner = subjectOwner,
        subjectText1 = subjectText1,
        subjectText2 = "",
        subjectAmount1 = "",
        subjectAmount2 = subjectAmount2,
        subjectAmount3 = "",
        subjectAmount4 = "",
        subjectImageGuid = "",
    )

    private class FakeRemote : NotUsedWorkShopsRemoteDataSource() {
        var subjects: ListData<SettlementSubjectDTO> = ListData()
        var lastId: String? = null
        var lastRequest: SettlementRequestDTO? = null

        override suspend fun getSettlementSubjects(
            query: ApiQueryParamDN,
        ): ListData<SettlementSubjectDTO> = subjects

        override suspend fun submitSettlementRequest(
            id: String,
            request: SettlementRequestDTO,
        ): String {
            lastId = id
            lastRequest = request
            return ""
        }
    }
}
