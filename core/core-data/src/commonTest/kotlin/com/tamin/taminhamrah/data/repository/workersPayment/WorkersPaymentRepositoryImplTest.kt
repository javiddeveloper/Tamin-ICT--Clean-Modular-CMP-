package com.tamin.taminhamrah.data.repository.workersPayment

import com.tamin.taminhamrah.dataSource.workersPayment.WorkersPaymentRemoteDataSource
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitRequestDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDataDTO
import com.tamin.taminhamrah.repository.workersPayment.WorkersPaymentRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * [WorkersPaymentRepositoryImpl] is a network-only repository (no Room cache) — it just delegates to
 * [WorkersPaymentRemoteDataSource] and runs the DTO→DN mappers. These tests pin the mapping
 * behaviour (aggregate totals, `amountFines` string→Long parse, the positional `payDebit` list) and
 * error propagation.
 */
class WorkersPaymentRepositoryImplTest {

    private lateinit var remote: FakeRemoteDataSource
    private lateinit var repository: WorkersPaymentRepository

    @BeforeTest
    fun setup() {
        remote = FakeRemoteDataSource()
        repository = WorkersPaymentRepositoryImpl(remote)
    }

    // ── getWorkersPaymentInfo ───────────────────────────────────────────────

    @Test
    fun `getWorkersPaymentInfo maps the data envelope and parses amountFines`() = runTest {
        remote.paymentInfoResult = WorkersPaymentInfoDataDTO(
            totalAmount = 300L,
            totalPenalty = 100L,
            totalPremium = 200L,
            total = 1,
            list = listOf(
                WorkersPaymentInfoDTO(
                    pay = false,
                    payable = true,
                    fines = true,
                    year = "1404",
                    month = "05",
                    amount = 200L,
                    // wire field is a String, often padded, sometimes null
                    amountFines = " 100 ",
                    fromDatePersian = "14040501",
                    fromDateToDate = "1404050114040531",
                    monthTitle = "حق بیمه مرداد",
                    salary = 2312000L,
                ),
            ),
        )

        val result = repository.getWorkersPaymentInfo()

        assertEquals(300L, result.totalAmount)
        assertEquals(100L, result.totalPenalty)
        assertEquals(200L, result.totalPremium)
        assertEquals(1, result.total)
        assertEquals(1, result.list.size)

        val item = result.list.first()
        assertEquals(200L, item.amount)
        assertEquals(100L, item.amountFines)      // trimmed + parsed from " 100 "
        assertEquals(300L, item.totalPayable)     // amount + amountFines
        assertEquals(2312000L, item.salary)       // dastMoazd
        assertTrue(item.payable)
        assertEquals("1404050114040531", item.fromDateToDate)
    }

    @Test
    fun `getWorkersPaymentInfo tolerates a null list and null totals`() = runTest {
        remote.paymentInfoResult = WorkersPaymentInfoDataDTO(list = null)

        val result = repository.getWorkersPaymentInfo()

        assertTrue(result.list.isEmpty())
        assertEquals(0L, result.totalAmount)
        assertEquals(0L, result.totalPenalty)
        assertEquals(0L, result.totalPremium)
    }

    @Test
    fun `getWorkersPaymentInfo maps a non-numeric amountFines to null`() = runTest {
        remote.paymentInfoResult = WorkersPaymentInfoDataDTO(
            list = listOf(WorkersPaymentInfoDTO(amount = 200L, amountFines = "N/A")),
        )

        val item = repository.getWorkersPaymentInfo().list.first()

        assertNull(item.amountFines)
        assertEquals(200L, item.totalPayable)     // amount + (null -> 0)
    }

    // ── payWorkersDebit ────────────────────────────────────────────────────

    @Test
    fun `payWorkersDebit sends the body without redirectUrl and maps the positional list`() = runTest {
        remote.payDebitResult = WorkersPayDebitDTO(
            total = 3,
            list = listOf("https://tfh.tamin.ir/view/#/payment/T-1", "T-1", "extra-index-2"),
        )
        val params = WorkersPayDebitParamsDN(
            amount = 8940000L,
            dates = listOf("14040501"),
            fromToDate = listOf("1404050114040531"),
            redirectUrl = "mytamin://workers_payment_callback",
        )

        val result = repository.payWorkersDebit(params)

        val body = assertNotNull(remote.lastPayDebitRequest)
        assertEquals(8940000L, body.amount)
        assertEquals(listOf("14040501"), body.dates)
        assertEquals(listOf("1404050114040531"), body.fromToDate)
        // redirectUrl travels as the `?type=` query, never in the body
        assertEquals("mytamin://workers_payment_callback", remote.lastRedirectUrl)

        assertEquals("https://tfh.tamin.ir/view/#/payment/T-1", result.paymentUrl) // list[0]
        assertEquals("T-1", result.ticket)                                          // list[1]
        assertEquals("extra-index-2", result.paymentInfo)                           // list[3] absent, falls back to list[2]
    }

    @Test
    fun `payWorkersDebit reads paymentInfo from list index 3`() = runTest {
        remote.payDebitResult = WorkersPayDebitDTO(list = listOf("url", "ticket", "idx2", "idx3"))

        val result = repository.payWorkersDebit(
            WorkersPayDebitParamsDN(amount = 1L, dates = emptyList(), fromToDate = emptyList(), redirectUrl = ""),
        )

        assertEquals("idx3", result.paymentInfo)
    }

    // ── inspectTicket ─────────────────────────────────────────────────────

    @Test
    fun `inspectTicket delegates straight to the remote data source`() = runTest {
        remote.inspectResult = "verified"

        val message = repository.inspectTicket("T-1", "enc-info")

        assertEquals("verified", message)
        assertEquals("T-1" to "enc-info", remote.lastInspectParams)
    }

    // ── error propagation ─────────────────────────────────────────────────

    @Test
    fun `getWorkersPaymentInfo propagates remote errors`() = runTest {
        remote.shouldThrowError = true

        assertFailsWith<RuntimeException> { repository.getWorkersPaymentInfo() }
    }

    @Test
    fun `payWorkersDebit propagates remote errors`() = runTest {
        remote.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            repository.payWorkersDebit(
                WorkersPayDebitParamsDN(amount = 1L, dates = emptyList(), fromToDate = emptyList(), redirectUrl = ""),
            )
        }
    }

    @Test
    fun `inspectTicket propagates remote errors`() = runTest {
        remote.shouldThrowError = true

        assertFailsWith<RuntimeException> { repository.inspectTicket("T-1", null) }
    }

    private class FakeRemoteDataSource : WorkersPaymentRemoteDataSource {
        var shouldThrowError = false
        var error: Throwable = RuntimeException("Remote failure")

        var paymentInfoResult = WorkersPaymentInfoDataDTO()
        var payDebitResult = WorkersPayDebitDTO()
        var inspectResult = "ok"

        var lastPayDebitRequest: WorkersPayDebitRequestDTO? = null
        var lastRedirectUrl: String? = null
        var lastInspectParams: Pair<String?, String?>? = null

        override suspend fun getWorkersPaymentInfo(): WorkersPaymentInfoDataDTO {
            if (shouldThrowError) throw error
            return paymentInfoResult
        }

        override suspend fun payWorkersDebit(
            request: WorkersPayDebitRequestDTO,
            redirectUrl: String,
        ): WorkersPayDebitDTO {
            lastPayDebitRequest = request
            lastRedirectUrl = redirectUrl
            if (shouldThrowError) throw error
            return payDebitResult
        }

        override suspend fun inspectTicket(ticket: String?, paymentInfo: String?): String {
            lastInspectParams = ticket to paymentInfo
            if (shouldThrowError) throw error
            return inspectResult
        }
    }
}
