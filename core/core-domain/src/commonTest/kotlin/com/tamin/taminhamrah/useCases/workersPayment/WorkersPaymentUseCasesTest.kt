package com.tamin.taminhamrah.useCases.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN
import com.tamin.taminhamrah.repository.workersPayment.FakeWorkersPaymentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * All three workers-payment use cases are thin `suspend operator fun` pass-throughs over
 * [com.tamin.taminhamrah.repository.workersPayment.WorkersPaymentRepository]; these tests assert the
 * input → repository call → output wiring plus error propagation.
 */
class WorkersPaymentUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeWorkersPaymentRepository

    @BeforeTest
    fun setup() {
        repository = FakeWorkersPaymentRepository()
    }

    // ── GetWorkersPaymentInfoUseCase ─────────────────────────────────────────

    @Test
    fun `get info returns the list from the repository`() = runTest {
        val expected = WorkersPaymentInfoListDN(
            totalAmount = 8940000L,
            totalPenalty = 1341000L,
            totalPremium = 7599000L,
            total = 1,
            list = listOf(sampleItem()),
        )
        repository.paymentInfoResult = expected

        val result = GetWorkersPaymentInfoUseCase(repository)()

        assertEquals(expected, result)
    }

    @Test
    fun `get info propagates repository errors`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("info failed")

        val ex = assertFailsWith<RuntimeException> { GetWorkersPaymentInfoUseCase(repository)() }
        assertEquals("info failed", ex.message)
    }

    // ── PayWorkersDebitUseCase ──────────────────────────────────────────────

    @Test
    fun `pay debit forwards params and returns the result`() = runTest {
        val params = WorkersPayDebitParamsDN(
            amount = 8940000L,
            dates = listOf("14040501"),
            fromToDate = listOf("1404050114040531"),
            redirectUrl = "mytamin://workers_payment_callback",
        )
        repository.payDebitResult = WorkersPayDebitResultDN(
            paymentUrl = "https://tfh.tamin.ir/view/#/payment/T-1",
            ticket = "T-1",
            paymentInfo = "enc-info",
        )

        val result = PayWorkersDebitUseCase(repository)(params)

        assertEquals(params, repository.lastPayDebitParams)
        assertEquals("https://tfh.tamin.ir/view/#/payment/T-1", result.paymentUrl)
        assertEquals("T-1", result.ticket)
        assertEquals("enc-info", result.paymentInfo)
    }

    @Test
    fun `pay debit propagates repository errors`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("pay failed")

        val ex = assertFailsWith<RuntimeException> {
            PayWorkersDebitUseCase(repository)(
                WorkersPayDebitParamsDN(amount = 1L, dates = emptyList(), fromToDate = emptyList(), redirectUrl = ""),
            )
        }
        assertEquals("pay failed", ex.message)
    }

    // ── InspectWorkersPaymentTicketUseCase ─────────────────────────────────

    @Test
    fun `inspect ticket forwards both args and returns the backend message`() = runTest {
        repository.inspectTicketResult = "پرداخت با موفقیت انجام شد."

        val message = InspectWorkersPaymentTicketUseCase(repository)("T-9", "enc-info")

        assertEquals("پرداخت با موفقیت انجام شد.", message)
        assertEquals("T-9" to "enc-info", repository.lastInspectTicketParams)
    }

    @Test
    fun `inspect ticket forwards null args`() = runTest {
        InspectWorkersPaymentTicketUseCase(repository)(null, null)

        assertEquals(null to null, repository.lastInspectTicketParams)
    }

    @Test
    fun `inspect ticket propagates repository errors`() = runTest {
        repository.shouldThrowError = true
        repository.error = RuntimeException("inspect failed")

        val ex = assertFailsWith<RuntimeException> {
            InspectWorkersPaymentTicketUseCase(repository)("T-1", null)
        }
        assertEquals("inspect failed", ex.message)
    }

    private fun sampleItem() = WorkersPaymentInfoDN(
        pay = false,
        payable = true,
        fines = true,
        year = "1404",
        month = "05",
        days = "31",
        professional = "7112",
        professionalTitle = "بنّای سفت‌کار",
        rate = "1.9",
        amount = 7599000L,
        amountFines = 1341000L,
        fromDatePersian = "14040501",
        toDatePersian = "14040531",
        fromDateToDate = "1404050114040531",
        monthTitle = "حق بیمه مرداد",
        type = "Premium",
        salary = 2312000L,
        payDay = 5541850L,
        paymentStatus = "",
        paymentDate = null,
        payableDes = "هست",
        fishStatus = "دارد",
        maharatStatus = "دارد",
        bazresiStatus = "دارد",
        kargarStatus = "فعال می‌باشد.",
    )
}
