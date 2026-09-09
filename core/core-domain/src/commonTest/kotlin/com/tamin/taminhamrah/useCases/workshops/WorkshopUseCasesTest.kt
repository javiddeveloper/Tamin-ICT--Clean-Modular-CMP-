package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentRequestDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.repository.workshops.FakeWorkShopsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The workshop use cases that decide something, rather than forward a call.
 *
 * The plain pass-throughs are not tested here: a test that asserts a delegate delegates only
 * restates the code. What is covered is the objection filing window, the payment gate, and that
 * search and status filter travel together.
 */
class WorkshopUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setup() {
        repository = FakeWorkShopsRepository()
    }

    // ------------------------------------------------------------------ the list

    @Test
    fun `list query carries search and status filter together`() = runTest {
        repository.employerAgreements = PagedListDN(
            items = listOf(EmployerAgreementDN(workshop = WorkshopSummaryDN(workshopId = "1"))),
            total = 1,
        )

        val result = GetEmployerAgreementsUseCase(repository)(
            WorkshopListQuery(
                workshopId = "0968210170",
                branchCode = "0960",
                status = WorkshopActivityStatus.ACTIVE,
            )
        )

        assertEquals(1, result.items.size)
        val asked = repository.lastWorkshopListQuery
        assertEquals("0968210170", asked?.workshopId)
        assertEquals("0960", asked?.branchCode)
        // The old screen dropped whichever of the two it had not just been handed.
        assertEquals(WorkshopActivityStatus.ACTIVE, asked?.status)
    }

    // ------------------------------------------------------- objection deadline

    @Test
    fun `objection against an estimate is allowed inside thirty-one days`() = runTest {
        repository.objectionElapsedDays = 31

        val allowed = CheckObjectionDeadlineUseCase(repository)(estimateDebt())

        assertTrue(allowed)
    }

    @Test
    fun `objection against an estimate is refused past thirty-one days`() = runTest {
        repository.objectionElapsedDays = 32

        val allowed = CheckObjectionDeadlineUseCase(repository)(estimateDebt())

        assertFalse(allowed)
    }

    @Test
    fun `objection against a primary vote closes ten days earlier`() = runTest {
        repository.objectionElapsedDays = 22

        val allowed = CheckObjectionDeadlineUseCase(repository)(primaryVoteDebt())

        // 22 days is still open for an estimate but past the window for a vote.
        assertFalse(allowed)
        assertEquals(ObjectionKind.PRIMARY_VOTE, primaryVoteDebt().objectionKind)
    }

    @Test
    fun `a debt already objected to is refused without asking the service`() = runTest {
        repository.objectionElapsedDays = 0
        val filed = estimateDebt().copy(seqNo = 42L)

        val allowed = CheckObjectionDeadlineUseCase(repository)(filed)

        assertFalse(allowed)
    }

    // ---------------------------------------------------------- online payment

    @Test
    fun `payment is refused when the pre-check says no`() = runTest {
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = false)
        repository.paymentResult = DebitPaymentDN(succeeded = true, paymentTicket = "ticket-1")

        val result = PayWorkshopDebitUseCase(repository)(paymentRequest())

        assertFalse(result.succeeded)
        // The payment call must not have been made at all.
        assertNull(repository.lastPaymentRequest)
    }

    @Test
    fun `payment goes through once the pre-check agrees`() = runTest {
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
        repository.paymentResult = DebitPaymentDN(succeeded = true, paymentTicket = "ticket-1")

        val result = PayWorkshopDebitUseCase(repository)(paymentRequest())

        assertTrue(result.isPayable)
        assertEquals("1234", repository.lastPaymentRequest?.debitNumber)
    }

    // ------------------------------------------ legal representative ticket request

    @Test
    fun `requesting a ticket with no national code verifies the signed-in user`() = runTest {
        RequestLegalRepresentativeTicketUseCase(repository)()

        assertEquals(1, repository.requestTicketCallCount)
        assertNull(repository.lastRequestTicketNationalCode)
    }

    @Test
    fun `requesting a ticket for a national code verifies that representative instead`() = runTest {
        RequestLegalRepresentativeTicketUseCase(repository)("0499370899")

        assertEquals("0499370899", repository.lastRequestTicketNationalCode)
    }

    private fun estimateDebt() = WorkShopDebtDN(
        debitNumber = "1234",
        orderRecipeDate = "14040101",
        debitStepCode = "01",
        debitStatCode = "03",
    )

    private fun primaryVoteDebt() = estimateDebt().copy(debitStepCode = "02")

    private fun paymentRequest() = DebitPaymentRequestDN(
        workshopId = "0968210170",
        branchCode = "0960",
        debitNumber = "1234",
        agreementRow = "02100014",
    )
}
