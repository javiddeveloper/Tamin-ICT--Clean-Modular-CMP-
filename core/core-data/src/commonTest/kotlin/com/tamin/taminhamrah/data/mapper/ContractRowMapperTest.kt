package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractDTO
import com.tamin.taminhamrah.model.workshop.WorkshopContractInfoDTO
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The wire → domain half of ردیف‌های پیمان.
 *
 * The point of interest is that the two services name the same two concepts differently — ردیف پیمان
 * is `pymseq` on one and `contractRow` on the other, تاریخ تعهد is `startdate` against `startDate` —
 * and that both spellings are load-bearing. A formatter or a well-meaning rename that "corrects"
 * either one makes the field silently deserialise to null, which is exactly the failure these
 * assertions catch.
 */
class ContractRowMapperTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Test
    fun agreementRowReadsContractRowFromPymseq() {
        val dto = EmployerAgreementDTO(
            contractRow = "4",
            startDate = "14030322",
            email = "tavakoli.edu@mail.com",
            mobile = "09124456678",
            workshop = EmployerWorkshopDTO(
                workshopId = "0968210170",
                branchCode = "0010",
                workshopName = "آموزشگاه کامپیوتر توکلی",
                lastAddress = "تهران، خیابان انقلاب، پلاک ۱۲",
            ),
        )

        val domain = dto.toDomain()

        assertEquals("4", domain.contractRow)
        assertEquals("14030322", domain.startDate)
        assertEquals("tavakoli.edu@mail.com", domain.email)
        assertEquals("09124456678", domain.mobile)
        assertEquals("0968210170", domain.workshop.workshopId)
        assertEquals("تهران، خیابان انقلاب، پلاک ۱۲", domain.workshop.address)
    }

    /**
     * The abbreviated, lower-case wire names are the contract.
     *
     * Decoded from a literal payload rather than a constructed DTO, because a constructor call
     * cannot catch a renamed `@SerialName` — only a real decode can.
     */
    @Test
    fun agreementRowDecodesTheServersOwnSpelling() {
        val payload = """
            {
              "pymseq": "6",
              "startdate": "14040407",
              "emailaddr": "karan.school@mail.com",
              "mobileno": "09143018372",
              "workshop": { "workshopId": "9028212822", "branchCode": "0210" }
            }
        """.trimIndent()

        val dto = json.decodeFromString<EmployerAgreementDTO>(payload)

        assertEquals("6", dto.contractRow)
        assertEquals("14040407", dto.startDate)
        assertEquals("karan.school@mail.com", dto.email)
        assertEquals("09143018372", dto.mobile)
    }

    /** The sibling endpoint spells both fields differently, and its own spelling must decode too. */
    @Test
    fun leanRowDecodesCamelCaseContractRowAndStartDate() {
        val payload = """
            {
              "contractRow": "3",
              "startDate": "14030120",
              "workshop": {
                "workshopId": "9007441260",
                "branchCode": "0421",
                "workshopName": "شرکت راه‌سازی البرز شرق"
              }
            }
        """.trimIndent()

        val dto = json.decodeFromString<WorkshopContractDTO>(payload)
        val domain = dto.toDomain()

        assertEquals("3", domain.contractRow)
        assertEquals("14030120", domain.startDate)
        assertEquals("9007441260", domain.workshopId)
        assertEquals("0421", domain.branchCode)
        assertEquals("شرکت راه‌سازی البرز شرق", domain.workshopName)
    }

    /**
     * The lean endpoint sends `character` and `workshopStatus` as bare strings where the agreement
     * endpoint sends objects. That is why it has its own nested DTO, and this proves the payload
     * that would throw against the other one decodes here.
     */
    @Test
    fun leanRowDecodesScalarCharacterAndStatus() {
        val payload = """
            {
              "contractRow": "8",
              "startDate": "14040516",
              "workshop": {
                "workshopId": "9007441260",
                "workshopName": "شرکت راه‌سازی البرز شرق",
                "character": "01",
                "workshopStatus": "02"
              }
            }
        """.trimIndent()

        val domain = json.decodeFromString<WorkshopContractDTO>(payload).toDomain()

        assertEquals("8", domain.contractRow)
        assertEquals("9007441260", domain.workshopId)
    }

    /** Anything the service omits collapses to blank once, here, so no screen repeats `?: ""`. */
    @Test
    fun absentFieldsBecomeBlankNotNull() {
        val agreement = EmployerAgreementDTO().toDomain()
        assertEquals("", agreement.contractRow)
        assertEquals("", agreement.workshop.workshopId)

        val lean = WorkshopContractDTO(workshop = WorkshopContractInfoDTO()).toDomain()
        assertEquals("", lean.contractRow)
        assertEquals("", lean.startDate)
        assertEquals("", lean.workshopName)
    }
}
