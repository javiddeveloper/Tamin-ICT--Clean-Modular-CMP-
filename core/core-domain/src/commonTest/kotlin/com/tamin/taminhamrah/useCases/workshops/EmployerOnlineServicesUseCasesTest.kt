package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementSubmissionDN
import com.tamin.taminhamrah.model.workshop.EmployerContactInfoDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import com.tamin.taminhamrah.repository.workshops.FakeWorkShopsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * خدمات غیرحضوری کارفرما (`employerEservicesAgreement`) use cases.
 *
 * These six are thin pass-throughs onto [com.tamin.taminhamrah.repository.WorkShopsRepository];
 * what is worth asserting is that each one forwards *every* argument it was handed (the mobile that
 * the OTP is sent to, the workshop/branch a drill-down is scoped by, the ticket code the agreement
 * is submitted with) and that a repository failure surfaces unchanged rather than being swallowed.
 */
class EmployerOnlineServicesUseCasesTest : BaseUseCaseTest() {

    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setup() {
        repository = FakeWorkShopsRepository()
    }

    // ------------------------------------------------------------ step 1 — request ticket

    @Test
    fun `request ticket forwards mobile and email and returns the backend message`() = runTest {
        repository.ticketRequestMessage = "کد تایید ارسال شد"

        val message = RequestEmployerAgreementTicketUseCase(repository)(
            mobile = "09121234567",
            email = "boss@example.com",
        )

        assertEquals("کد تایید ارسال شد", message)
        assertEquals("09121234567" to "boss@example.com", repository.lastTicketRequest)
    }

    // ------------------------------------------------------------ step 2 — verify code

    @Test
    fun `contact info use case forwards the verification code and returns the identity block`() = runTest {
        repository.employerContactInfo = EmployerContactInfoDN(
            firstName = "رضا",
            lastName = "کارفرما",
            nationalCode = "0012345678",
            currentMobile = "09120000000",
        )

        val info = GetEmployerAgreementContactInfoUseCase(repository)("123456")

        assertEquals("رضا کارفرما", info.fullName)
        assertEquals("0012345678", info.nationalCode)
        assertEquals("123456", repository.lastContactInfoCode)
    }

    @Test
    fun `workshops-without-contract use case asks for the requested page`() = runTest {
        repository.workshopsWithoutContract = PagedListDN(
            items = listOf(WorkshopWithoutContractDN(workshopId = "1071410004", branchCode = "123")),
            total = 1,
        )

        val page = GetWorkshopsWithoutContractUseCase(repository)(page = 2)

        assertEquals(1, page.items.size)
        assertEquals("1071410004", page.items.first().workshopId)
        assertEquals(2, repository.lastWorkshopsWithoutContractPage)
    }

    // ------------------------------------------------------------ drill-downs

    @Test
    fun `contract rows use case scopes the query by workshop, branch and page`() = runTest {
        repository.workshopContractRows = PagedListDN(
            items = listOf(WorkshopContractRowDN(contractRow = "02100014", firstName = "علی", lastName = "پیمانکار")),
            total = 1,
        )

        val page = GetWorkshopContractRowsUseCase(repository)(
            workshopId = "0968210170",
            branchCode = "0960",
            page = 1,
        )

        assertEquals("علی پیمانکار", page.items.first().fullName)
        assertEquals(Triple("0968210170", "0960", 1), repository.lastContractRowsArgs)
    }


    // ------------------------------------------------------------ step 3 — submit

    @Test
    fun `submit use case passes the submission through untouched and returns the message`() = runTest {
        repository.employerAgreementSubmitMessage = "تعهدنامه ثبت شد"
        val submission = EmployerAgreementSubmissionDN(
            mobile = "09121234567",
            email = "boss@example.com",
            ticketCode = "654321",
        )

        val message = SubmitEmployerAgreementUseCase(repository)(submission)

        assertEquals("تعهدنامه ثبت شد", message)
        assertEquals(submission, repository.lastEmployerAgreementSubmission)
    }

    // ------------------------------------------------------------ error propagation

    @Test
    fun `a repository failure propagates out of the use case unchanged`() = runTest {
        repository.error = IllegalStateException("no connection")

        val error = assertFailsWith<IllegalStateException> {
            GetWorkshopsWithoutContractUseCase(repository)(page = 0)
        }
        assertEquals("no connection", error.message)
    }
}
