package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The body of `pay-normal-debit`, which the service is particular about.
 *
 * Every one of these was sent wrongly at least once and answered with `ProxyRuntimeException`, so
 * the shape is pinned here rather than left to the defaults: this client serializes with
 * `encodeDefaults` off, which silently drops any value that happens to equal its declared default.
 *
 * The assertions are on the **serialized JSON**, never on the model — a model-level check passes
 * while the field vanishes at encode time, which is the exact failure this file exists to catch.
 */
class DebitPaymentRequestDTOTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun body(
        agreementRow: String = "09600002",
        deposit: String = "0",
        nationalId: String? = null,
        nationalType: String = "01",
    ) = json.encodeToString(
        DebitPaymentRequestDTO(
            branchCode = "6310",
            workshopId = "9028218513",
            debitNumber = "6310030089235",
            agreementRow = agreementRow,
            deposit = deposit,
            nationalId = nationalId,
            nationalType = nationalType,
        )
    )

    @Test
    fun `the deposit flag is written even when it is off`() {
        val body = body()

        assertTrue(body.contains(""""seporde":"0""""), "seporde must survive encodeDefaults: $body")
        assertTrue(body.contains(""""peymanSequence":"09600002""""), body)
    }

    @Test
    fun `a debt with no agreement row still carries an empty peymanSequence`() {
        val body = body(agreementRow = "")

        assertTrue(body.contains("\"peymanSequence\""), "the key must be present, not dropped: $body")
        assertTrue(body.contains(""""seporde":"0""""), body)
    }

    @Test
    fun `the flag is never spelled as a boolean`() {
        val body = body(deposit = "1")

        assertFalse(body.contains("true"), body)
        assertFalse(body.contains("false"), body)
        assertEquals(true, body.contains(""""seporde":"1""""), body)
    }

    /**
     * The one that `encodeDefaults` would break: a `= null` default on [DebitPaymentRequestDTO]
     * drops the key entirely, and the service refuses a body that is missing it.
     */
    @Test
    fun `a natural-person workshop still sends nationalId as an explicit null`() {
        val body = body(nationalId = null, nationalType = "01")

        assertTrue(body.contains(""""nationalId":null"""), "nationalId must be present: $body")
        assertTrue(body.contains(""""nationalType":"01""""), body)
    }

    @Test
    fun `a legal workshop sends its own national id`() {
        val body = body(nationalId = "10101234567", nationalType = "02")

        assertTrue(body.contains(""""nationalId":"10101234567""""), body)
        assertTrue(body.contains(""""nationalType":"02""""), body)
    }

    /** The whole body, as the service team confirmed it against the web client's own request. */
    @Test
    fun `the body matches the shape the service confirmed`() {
        assertEquals(
            """{"branchCode":"6310","workshopId":"9028218513","debitNumber":"6310030089235",""" +
                """"peymanSequence":"","seporde":"0","nationalId":null,"nationalType":"01"}""",
            body(agreementRow = "", deposit = "0", nationalId = null, nationalType = "01"),
        )
    }
}
