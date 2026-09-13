package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.AssignerContractDTO
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDTO
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The wire → domain half of واگذارندگان.
 *
 * Two things are worth defending here and both have burnt this codebase before:
 *
 * 1. the two parties of a پیمان are **not** interchangeable — `assigner` is the signed-in employer
 *    and `employer` is the پیمانکار, and swapping them puts the user's own workshop on every card;
 * 2. the مبانی محاسباتی payload is spelled in the service's own lower-case abbreviations
 *    (`letno`, `senddate`, `cntamount`, `dataDetail`), and a rename that "tidies" one makes the
 *    field silently deserialize to null.
 *
 * Both are decoded from literal payloads rather than constructed DTOs, because a constructor call
 * cannot catch a renamed `@SerialName` — only a real decode can.
 */
class AssignerContractMapperTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Test
    fun assignerContractKeepsTheTwoPartiesApart() {
        val payload = """
            {
              "contractRow": "1",
              "contractSequence": "3",
              "contractNumber": "44122",
              "contractDate": "14010210",
              "contractEndDate": "14030601",
              "contractSubject": "خدمات نظافت و پشتیبانی",
              "assigner": {
                "workshopId": "0968210170",
                "workshopName": "آموزشگاه کامپیوتر توکلی",
                "nationalId": "4231098876",
                "address": "تهران، خیابان انقلاب، پلاک ۱۲",
                "branch": { "code": "0010", "organizationName": "شعبه ۱۰ تهران" }
              },
              "employer": {
                "workshopId": "9028212822",
                "workshopName": "دبستان کارن ۲",
                "nationalId": "1022334455",
                "address": "بجنورد، خیابان طالقانی",
                "branch": { "code": "0210", "organizationName": "شعبه ۲ بجنورد" }
              }
            }
        """.trimIndent()

        val domain = json.decodeFromString<AssignerContractDTO>(payload).toDomain()

        assertEquals("1", domain.contractRow)
        assertEquals("3", domain.contractSequence)
        assertEquals("44122", domain.contractNumber)
        assertEquals("14010210", domain.contractDate)
        assertEquals("14030601", domain.contractEndDate)
        assertEquals("خدمات نظافت و پشتیبانی", domain.contractSubject)

        // واگذارنده — you.
        assertEquals("0968210170", domain.assigner.workshopId)
        assertEquals("آموزشگاه کامپیوتر توکلی", domain.assigner.workshopName)
        assertEquals("شعبه ۱۰ تهران", domain.assigner.branchName)

        // پیمانکار — the counterparty, and the side every card and every drill-down key uses.
        assertEquals("9028212822", domain.employer.workshopId)
        assertEquals("دبستان کارن ۲", domain.employer.workshopName)
        // `brchCode` on the bases request comes from exactly here, off the پیمانکار's branch.
        assertEquals("0210", domain.employer.branchCode)
        assertEquals("شعبه ۲ بجنورد", domain.employer.branchName)
    }

    /**
     * درخواست مفاصاحساب is addressed with the پیمان's own top-level `branch`, which is what the old app
     * reads there; a payload without one falls back to the پیمانکار's rather than leaving the id short.
     */
    @Test
    fun contractBranchPrefersTheTopLevelBranch() {
        val withOwn = json.decodeFromString<AssignerContractDTO>(
            """{"branch":{"code":"0310"},"employer":{"branch":{"code":"0210"}}}""",
        ).toDomain()
        assertEquals("0310", withOwn.branchCode)

        val withoutOwn = json.decodeFromString<AssignerContractDTO>(
            """{"employer":{"branch":{"code":"0210"}}}""",
        ).toDomain()
        assertEquals("0210", withoutOwn.branchCode)
    }

    /** A پیمان the service sent without either party still maps, with blanks rather than nulls. */
    @Test
    fun assignerContractSurvivesMissingParties() {
        val domain = json.decodeFromString<AssignerContractDTO>("""{"contractRow":"2"}""").toDomain()

        assertEquals("2", domain.contractRow)
        assertEquals("", domain.contractSequence)
        assertEquals("", domain.contractEndDate)
        assertEquals("", domain.employer.workshopId)
        assertEquals("", domain.assigner.branchCode)
    }

    @Test
    fun computationalBaseDecodesTheServersOwnSpelling() {
        val payload = """
            {
              "letno": "12044",
              "senddate": 1670000000000,
              "cntamount": 84000000,
              "contract": { "contractRow": "1", "contractSequence": "3" },
              "dataDetail": [
                { "documentId": "a1", "documentType": "1", "documentCode": "1" },
                { "documentId": "b2", "documentType": "2", "documentCode": "4" }
              ]
            }
        """.trimIndent()

        val domain = json.decodeFromString<ComputationalBaseDTO>(payload).toDomain()

        assertEquals("12044", domain.letterNumber)
        assertEquals(1670000000000L, domain.sendDate)
        assertEquals(84000000L, domain.amount)
        assertEquals(2, domain.documents.size)
    }

    /**
     * `documentType` decides the endpoint, and only `"1"` is an image.
     *
     * The old app branches on this in two places and treats everything else as a PDF, so the
     * fallback is PDF rather than an "unknown" the screen would have no viewer for.
     */
    @Test
    fun documentKindFollowsDocumentType() {
        val payload = """
            {
              "letno": "1",
              "dataDetail": [
                { "documentId": "a", "documentType": "1", "documentCode": "1" },
                { "documentId": "b", "documentType": "2", "documentCode": "3" },
                { "documentId": "c", "documentType": null, "documentCode": "9" }
              ]
            }
        """.trimIndent()

        val documents = json.decodeFromString<ComputationalBaseDTO>(payload).toDomain().documents

        assertEquals(BaseDocumentKind.IMAGE, documents[0].kind)
        assertEquals(BaseDocumentKind.PDF, documents[1].kind)
        assertEquals(BaseDocumentKind.PDF, documents[2].kind)
        assertEquals("9", documents[2].categoryCode)
    }

    /** An amount the service did not report stays null, so the mapper can dash it rather than print ۰. */
    @Test
    fun missingAmountStaysNull() {
        val domain = json.decodeFromString<ComputationalBaseDTO>("""{"letno":"7"}""").toDomain()

        assertEquals(null, domain.amount)
        assertEquals(null, domain.sendDate)
        assertTrue(domain.documents.isEmpty())
    }
}
