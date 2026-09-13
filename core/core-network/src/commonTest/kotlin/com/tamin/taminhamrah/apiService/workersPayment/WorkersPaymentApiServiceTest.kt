package com.tamin.taminhamrah.apiService.workersPayment

import com.tamin.taminhamrah.apiService.BaseApiTest
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitRequestDTO
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.util.ApiTestUtils
import com.tamin.taminhamrah.util.WorkersPaymentTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class WorkersPaymentApiServiceTest : BaseApiTest() {

    @Test
    fun `getWorkersPaymentInfo parses the aggregate totals and list rows`() = runTest {
        val json =
            ApiTestUtils.createJsonResponse(dataJson = WorkersPaymentTestData.paymentInfoSuccess)

        val response =
            createMockKtorfit(json).createWorkersPaymentApiService().getWorkersPaymentInfo()

        assertEquals(200, response.status)
        assertEquals("SUCCESSFUL", response.family)
        val data = assertNotNull(response.data)
        assertEquals(8940000L, data.totalAmount)
        assertEquals(1341000L, data.totalPenalty)
        assertEquals(7599000L, data.totalPremium)
        assertEquals(1, data.total)

        val row = assertNotNull(data.list).first()
        assertEquals("1404", row.year)
        assertEquals("05", row.month)
        assertEquals(false, row.pay)
        assertEquals(true, row.payable)
        assertEquals(7599000L, row.amount)
        assertEquals("1341000", row.amountFines)       // String on the wire
        assertEquals(2312000L, row.salary)             // @SerialName("dastMoazd")
        assertEquals("1404050114040531", row.fromDateToDate)
    }

    @Test
    fun `payWorkersDebit parses the positional string list`() = runTest {
        val json =
            ApiTestUtils.createJsonResponse(dataJson = WorkersPaymentTestData.payDebitSuccess)

        val response = createMockKtorfit(json).createWorkersPaymentApiService().payWorkersDebit(
            body = WorkersPayDebitRequestDTO(
                amount = 8940000L,
                dates = listOf("14040501"),
                fromToDate = listOf("1404050114040531"),
            ),
            redirectUrl = "mytamin://workers_payment_callback",
        )

        assertEquals(200, response.status)
        val data = assertNotNull(response.data)
        assertEquals(3, data.list.size)
        assertEquals("https://tfh.tamin.ir/view/#/payment/T-1", data.list[0])
        assertEquals("T-1", data.list[1])
        assertEquals("enc-payment-info", data.list[2])
    }

    @Test
    fun `payWorkersDebit tolerates an empty data list`() = runTest {
        val json = ApiTestUtils.createJsonResponse(dataJson = """{ "total": 0, "list": [] }""")

        val response = createMockKtorfit(json).createWorkersPaymentApiService().payWorkersDebit(
            body = WorkersPayDebitRequestDTO(amount = 1L),
            redirectUrl = "cb",
        )

        assertEquals(200, response.status)
        assertNotNull(response.data)
        assertNull(response.data?.list?.getOrNull(0))
    }

    @Test
    fun `inspectTicket exposes the bare success message through extractMessage`() = runTest {
        val json =
            ApiTestUtils.createJsonResponse(dataJson = WorkersPaymentTestData.inspectTicketSuccess)

        val response = createMockKtorfit(json).createWorkersPaymentApiService()
            .inspectTicket(ticket = "T-1", paymentInfo = null)

        assertEquals(200, response.status)
        assertEquals("پرداخت با موفقیت انجام شد.", response.extractMessage())
    }

    @Test
    fun `inspectTicket falls back to the reason field when data is null`() = runTest {
        val json = ApiTestUtils.createJsonResponse(dataJson = "null", reason = "OK-DONE")

        val response = createMockKtorfit(json).createWorkersPaymentApiService()
            .inspectTicket(ticket = "T-1", paymentInfo = "enc")

        assertEquals("OK-DONE", response.extractMessage())
    }
}
