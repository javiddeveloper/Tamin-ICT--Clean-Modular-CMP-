package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentInfoDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkDTO
import com.tamin.taminhamrah.model.payment.PaymentStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaymentMapperTest {

    @Test
    fun `a preview keeps the ticket it was asked about when the gateway echoes none`() {
        // A preview with no ticket can be neither cancelled nor re-checked, which is how the old
        // client ended up holding tickets it could not release.
        val preview = PaymentInfoDTO(ticket = null, paymentStatus = "NOT_PAYED")
            .toDomain(requestedTicket = "t-7")

        assertEquals("t-7", preview.ticket)
    }

    @Test
    fun `a preview carries both the asked-for and the settled amount`() {
        val preview = PaymentInfoDTO(
            paymentAmount = 1_250_000L,
            affectiveAmount = 1_250_000L,
            paymentStatus = "SUCCESSFUL",
            refNum = "900000000001",
            traceNo = "123456",
        ).toDomain(requestedTicket = "t-1")

        assertEquals(1_250_000L, preview.amount)
        assertEquals(1_250_000L, preview.settledAmount)
        assertEquals(PaymentStatus.SUCCESSFUL, preview.status)
        assertEquals("900000000001", preview.referenceNumber)
        assertEquals("123456", preview.traceNumber)
    }

    @Test
    fun `a response with nothing in it does not read as a real zero-rial payment`() {
        val preview = PaymentInfoDTO().toDomain(requestedTicket = "t-1")

        assertEquals(PaymentStatus.UNKNOWN, preview.status)
        assertFalse(preview.isPayable)
    }

    @Test
    fun `a refused payment link keeps the gateway's reason and is not openable`() {
        val link = PaymentLinkDTO(
            success = false,
            errorType = "INVALID_PERSON",
            errorDesc = "کد ملی وارد شده مورد قبول نیست",
        ).toDomain()

        assertFalse(link.isOpenable)
        assertEquals("کد ملی وارد شده مورد قبول نیست", link.message)
    }

    @Test
    fun `a success with no URL is not openable`() {
        assertFalse(PaymentLinkDTO(success = true, paymentUrl = "").toDomain().isOpenable)
        assertTrue(PaymentLinkDTO(success = true, paymentUrl = "https://x/pay").toDomain().isOpenable)
    }

    @Test
    fun `the payer type is sent as the gateway's number, in string form`() {
        val request = buildPaymentLinkRequest(PayerType.LEGAL_ENTITY, "10101234567")

        assertEquals("2", request.personType)
        assertEquals("10101234567", request.enteredNationalCode)
    }
}
