package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The body of `pay-normal-debit`, which the service is particular about.
 *
 * Both of these were sent wrongly once and answered with `ProxyRuntimeException`, so the shape is
 * pinned here rather than left to the defaults: this client serialises with `encodeDefaults` off,
 * which silently drops any value that happens to equal its declared default.
 */
class DebitPaymentRequestDTOTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Test
    fun `the deposit flag is written even when it is off`() {
        val body = json.encodeToString(
            DebitPaymentRequestDTO(
                branchCode = "6310",
                workshopId = "9028212823",
                debitNumber = "6310030089235",
                deposit = "0",
                agreementRow = "09600002",
            )
        )

        assertTrue(body.contains(""""seporde":"0""""), "seporde must survive encodeDefaults: $body")
        assertTrue(body.contains(""""peymanSequence":"09600002""""), body)
    }

    @Test
    fun `a debt with no agreement row carries no peymanSequence at all`() {
        val body = json.encodeToString(
            DebitPaymentRequestDTO(
                branchCode = "6310",
                workshopId = "9028212823",
                debitNumber = "6310030089235",
                deposit = "0",
            )
        )

        assertFalse(body.contains("peymanSequence"), "the field must be absent, not blank: $body")
        assertTrue(body.contains(""""seporde":"0""""), body)
    }

    @Test
    fun `the flag is never spelled as a boolean`() {
        val body = json.encodeToString(
            DebitPaymentRequestDTO(
                branchCode = "6310",
                workshopId = "9028212823",
                debitNumber = "6310030089235",
                deposit = "1",
            )
        )

        assertFalse(body.contains("true"), body)
        assertFalse(body.contains("false"), body)
        assertEquals(true, body.contains(""""seporde":"1""""), body)
    }
}
