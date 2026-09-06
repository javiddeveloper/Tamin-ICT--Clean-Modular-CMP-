package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices

import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.AgreementRequestStep
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesErrorSource
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesIntent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesScreen
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.EmployerOnlineServicesViewModel
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementSearch
import com.tamin.taminhamrah.feature.taminServices.inspection.FakeUserRepository
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.EmployerContactInfoDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import com.tamin.taminhamrah.useCases.content.GetLegalDocumentUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementContactInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopContractRowsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopsWithoutContractUseCase
import com.tamin.taminhamrah.useCases.workshops.RequestEmployerAgreementTicketUseCase
import com.tamin.taminhamrah.useCases.workshops.SubmitEmployerAgreementUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The single ViewModel behind the whole خدمات غیرحضوری کارفرمایان flow.
 *
 * Coverage is on the behaviour that isn't just a reducer copy: the landing screen fires identity
 * and agreements concurrently and tags each failure to its own retry key; a "ردیف‌های پیمان" tap
 * switches screen and scopes the rows call to that workshop; and the request wizard threads the
 * profile-seeded contact details all the way through OTP → verify → step-2 content → submit.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EmployerOnlineServicesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var workshops: FakeWorkShopsRepository
    private lateinit var users: FakeUserRepository
    private lateinit var legalDocs: FakeLegalDocumentRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        workshops = FakeWorkShopsRepository()
        users = FakeUserRepository().apply {
            userProfileResult = UserProfileDN(
                entityId = null,
                login = null,
                firstName = "رضا",
                lastName = "کارفرما",
                email = "boss@example.com",
                nationalCode = "0012345678",
                mobile = "09120000000",
            )
        }
        legalDocs = FakeLegalDocumentRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = EmployerOnlineServicesViewModel(
        getUserProfileUseCase = GetUserProfileUseCase(users),
        getEmployerAgreementsUseCase = GetEmployerAgreementsUseCase(workshops),
        getWorkshopContractRowsUseCase = GetWorkshopContractRowsUseCase(workshops),
        requestEmployerAgreementTicketUseCase = RequestEmployerAgreementTicketUseCase(workshops),
        getEmployerAgreementContactInfoUseCase = GetEmployerAgreementContactInfoUseCase(workshops),
        getWorkshopsWithoutContractUseCase = GetWorkshopsWithoutContractUseCase(workshops),
        getLegalDocumentUseCase = GetLegalDocumentUseCase(legalDocs),
        submitEmployerAgreementUseCase = SubmitEmployerAgreementUseCase(workshops),
    )

    // -------------------------------------------------------------------- initial load

    @Test
    fun initialLoad_populatesIdentityAndAgreements() = runTest(testDispatcher) {
        workshops.agreements = PagedListDN(items = listOf(agreementDN("1071410004", "123")), total = 7)

        val viewModel = buildViewModel()
        advanceUntilIdle()
        val state = viewModel.uiState.value

        assertEquals(false, state.isLoading)
        assertEquals("رضا کارفرما", state.agreementsList.identity.fullName)
        assertEquals(1, state.agreementsList.agreements.size)
        assertEquals("1071410004", state.agreementsList.agreements.first().workshopId)
        // The count tile shows the server's grand total, not how many rows came back.
        assertEquals(7, state.agreementsList.agreementCount)
        assertTrue(state.errors.isEmpty())
    }

    @Test
    fun initialLoad_agreementsFailure_isTaggedToItsOwnRetryKeyAndIdentityStillLoads() = runTest(testDispatcher) {
        workshops.failing = FakeWorkShopsRepository.Call.AGREEMENTS

        val viewModel = buildViewModel()
        advanceUntilIdle()
        val failed = viewModel.uiState.value

        // Identity is a separate concurrent call — it is unaffected.
        assertEquals("رضا کارفرما", failed.agreementsList.identity.fullName)
        assertTrue(failed.hasAnyError)
        assertTrue(failed.errors.containsKey(EmployerOnlineServicesErrorSource.AGREEMENTS))
        assertNull(failed.errors[EmployerOnlineServicesErrorSource.IDENTITY])

        // Retry re-issues only that call; once it succeeds the error clears and the list fills.
        workshops.failing = null
        workshops.agreements = PagedListDN(items = listOf(agreementDN("9", "9")), total = 1)
        viewModel.sendIntent(EmployerOnlineServicesIntent.RetrySource(EmployerOnlineServicesErrorSource.AGREEMENTS))
        advanceUntilIdle()

        val retried = viewModel.uiState.value
        assertTrue(retried.errors.isEmpty())
        assertEquals(1, retried.agreementsList.agreements.size)
    }

    // -------------------------------------------------------------------- agreements search & paging

    @Test
    fun searchAgreements_reQueriesTheServerFilteredFromPageOne() = runTest(testDispatcher) {
        workshops.agreements = PagedListDN(
            items = listOf(agreementDN("1071410004", "123"), agreementDN("9999999999", "456")),
            total = 2,
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.agreementsList.agreements.size)

        viewModel.sendIntent(
            EmployerOnlineServicesIntent.SearchAgreements(
                EmployerAgreementSearch(workshopCode = "9999999999", branchCode = "456"),
            ),
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("9999999999", workshops.lastAgreementsQuery?.workshopId)
        assertEquals("456", workshops.lastAgreementsQuery?.branchCode)
        assertEquals(0, workshops.lastAgreementsQuery?.page)
        assertEquals(1, state.agreementsList.agreements.size)
        assertEquals("9999999999", state.agreementsList.agreements.first().workshopId)
        // The count tile reflects the filtered total, not the two-row unfiltered grand total.
        assertEquals(1, state.agreementsList.agreementCount)
        assertEquals("9999999999", state.agreementsList.searchCriteria.workshopCode)
    }

    @Test
    fun clearAgreementsSearch_returnsToTheUnfilteredFirstPage() = runTest(testDispatcher) {
        workshops.agreements = PagedListDN(
            items = listOf(agreementDN("1071410004", "123"), agreementDN("9999999999", "456")),
            total = 2,
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(
            EmployerOnlineServicesIntent.SearchAgreements(EmployerAgreementSearch(workshopCode = "9999999999")),
        )
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.agreementsList.agreements.size)

        viewModel.sendIntent(EmployerOnlineServicesIntent.ClearAgreementsSearch)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(workshops.lastAgreementsQuery?.workshopId)
        assertEquals(2, state.agreementsList.agreements.size)
        assertTrue(state.agreementsList.searchCriteria.isEmpty)
    }

    @Test
    fun loadMoreAgreements_appendsTheNextPageInsteadOfReplacingTheFirst() = runTest(testDispatcher) {
        // 15 rows so the default 10-row page leaves a second page to fetch.
        workshops.agreements = PagedListDN(
            items = (1..15).map { agreementDN(it.toString().padStart(10, '0'), "1") },
            total = 15,
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        val firstPage = viewModel.uiState.value.agreementsList
        assertEquals(10, firstPage.agreements.size)
        assertEquals(false, firstPage.endReached)

        viewModel.sendIntent(EmployerOnlineServicesIntent.LoadMoreAgreements)
        advanceUntilIdle()

        val afterLoadMore = viewModel.uiState.value.agreementsList
        assertEquals(15, afterLoadMore.agreements.size)
        assertEquals(true, afterLoadMore.endReached)
        assertEquals(1, workshops.lastAgreementsQuery?.page)
    }

    // -------------------------------------------------------------------- contract rows drill-down

    @Test
    fun openContractRows_switchesScreenAndScopesTheRowsCallToThatWorkshop() = runTest(testDispatcher) {
        workshops.contractRows = PagedListDN(
            items = listOf(
                WorkshopContractRowDN(contractRow = "02100014", firstName = "علی", lastName = "پیمانکار"),
            ),
            total = 1,
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(
            EmployerOnlineServicesIntent.OpenContractRows(
                workshopId = "0968210170",
                branchCode = "0960",
                workshopName = "کارگاه الف",
                workshopCodeLabel = "۰۹۶۸۲۱۰۱۷۰",
            ),
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(EmployerOnlineServicesScreen.CONTRACT_ROWS, state.currentScreen)
        // Header is carried from the tapped card so it renders before the rows arrive.
        assertEquals("کارگاه الف", state.contractRows.workshopName)
        assertEquals(1, state.contractRows.rows.size)
        assertEquals(Triple("0968210170", "0960", 0), workshops.lastContractRowsArgs)

        // Closing returns to wherever it was opened from — the landing list, here.
        viewModel.sendIntent(EmployerOnlineServicesIntent.CloseContractRows)
        advanceUntilIdle()
        assertEquals(EmployerOnlineServicesScreen.AGREEMENTS_LIST, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun openContractRows_fromStep2_closingReturnsToTheWizardNotTheLandingList() = runTest(testDispatcher) {
        workshops.workshopsWithoutContract = PagedListDN(
            items = listOf(WorkshopWithoutContractDN(workshopId = "5", branchCode = "6", name = "بدون تعهدنامه")),
            total = 1,
        )
        workshops.contractRows = PagedListDN(
            items = listOf(
                WorkshopContractRowDN(contractRow = "02100014", firstName = "علی", lastName = "پیمانکار"),
            ),
            total = 1,
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // Get to step 2 of the wizard first.
        viewModel.sendIntent(EmployerOnlineServicesIntent.OpenAgreementRequest)
        viewModel.sendIntent(EmployerOnlineServicesIntent.UpdateRequestCode("654321"))
        viewModel.sendIntent(EmployerOnlineServicesIntent.VerifyAgreementCode)
        advanceUntilIdle()
        assertEquals(EmployerOnlineServicesScreen.REQUEST_WIZARD, viewModel.uiState.value.currentScreen)

        // Tap a workshop row from within step 2.
        viewModel.sendIntent(
            EmployerOnlineServicesIntent.OpenContractRows(
                workshopId = "5",
                branchCode = "6",
                workshopName = "بدون تعهدنامه",
                workshopCodeLabel = "۵",
            ),
        )
        advanceUntilIdle()
        assertEquals(EmployerOnlineServicesScreen.CONTRACT_ROWS, viewModel.uiState.value.currentScreen)

        // Closing must land back on the wizard (still at پذیرش تعهدنامه), not the landing list.
        viewModel.sendIntent(EmployerOnlineServicesIntent.CloseContractRows)
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(EmployerOnlineServicesScreen.REQUEST_WIZARD, state.currentScreen)
        assertEquals(AgreementRequestStep.ACCEPT_AGREEMENT, state.agreementRequest.step)
    }

    @Test
    fun loadMoreContractRows_appendsTheNextPageInsteadOfReplacingTheFirst() = runTest(testDispatcher) {
        // 15 rows so the default 10-row page leaves a second page to fetch.
        workshops.contractRows = PagedListDN(
            items = (1..15).map { WorkshopContractRowDN(contractRow = it.toString().padStart(3, '0')) },
            total = 15,
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(
            EmployerOnlineServicesIntent.OpenContractRows(
                workshopId = "0968210170",
                branchCode = "0960",
                workshopName = "کارگاه الف",
                workshopCodeLabel = "۰۹۶۸۲۱۰۱۷۰",
            ),
        )
        advanceUntilIdle()

        val firstPage = viewModel.uiState.value.contractRows
        assertEquals(10, firstPage.rows.size)
        assertEquals(false, firstPage.endReached)

        viewModel.sendIntent(EmployerOnlineServicesIntent.LoadMoreContractRows)
        advanceUntilIdle()

        val afterLoadMore = viewModel.uiState.value.contractRows
        assertEquals(15, afterLoadMore.rows.size)
        assertEquals(true, afterLoadMore.endReached)
        assertEquals(Triple("0968210170", "0960", 1), workshops.lastContractRowsArgs)
    }

    // -------------------------------------------------------------------- request wizard

    @Test
    fun requestWizard_seedsContactFromProfileAndThreadsItThroughToSubmit() = runTest(testDispatcher) {
        workshops.contactInfo = EmployerContactInfoDN(
            firstName = "رضا",
            lastName = "کارفرما",
            nationalCode = "0012345678",
            currentMobile = "09120000000",
        )
        workshops.workshopsWithoutContract = PagedListDN(
            items = listOf(WorkshopWithoutContractDN(workshopId = "5", branchCode = "6", name = "بدون تعهدنامه")),
            total = 1,
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // Open — the contact form is seeded from the profile that loaded with the identity.
        viewModel.sendIntent(EmployerOnlineServicesIntent.OpenAgreementRequest)
        advanceUntilIdle()
        assertEquals(EmployerOnlineServicesScreen.REQUEST_WIZARD, viewModel.uiState.value.currentScreen)
        assertEquals("09120000000", viewModel.uiState.value.agreementRequest.mobile)

        // Edit the mobile, then send the OTP — it goes to whatever the field holds now.
        viewModel.sendIntent(EmployerOnlineServicesIntent.UpdateRequestMobile("09121112233"))
        viewModel.sendIntent(EmployerOnlineServicesIntent.RequestAgreementTicket)
        advanceUntilIdle()
        assertEquals("09121112233" to "boss@example.com", workshops.lastTicketRequest)
        assertTrue(viewModel.uiState.value.agreementRequest.ticketRequested)

        // Enter and verify the code -> advances to پذیرش تعهدنامه and loads step-2 content.
        viewModel.sendIntent(EmployerOnlineServicesIntent.UpdateRequestCode("654321"))
        viewModel.sendIntent(EmployerOnlineServicesIntent.VerifyAgreementCode)
        advanceUntilIdle()

        val verified = viewModel.uiState.value.agreementRequest
        assertEquals("654321", workshops.lastVerificationCode)
        assertEquals(AgreementRequestStep.ACCEPT_AGREEMENT, verified.step)
        assertEquals("رضا کارفرما", verified.employerName)
        assertEquals(1, verified.workshopsWithoutContract.size)
        assertEquals("employer-eservices-agreement", legalDocs.lastRequestedId)
        // The document intro is personalised with the verified identity.
        assertTrue(verified.document.intro.contains("رضا کارفرما"))

        // Accept and submit -> the submission carries the edited mobile and the entered code.
        viewModel.sendIntent(EmployerOnlineServicesIntent.SetAgreementAccepted(true))
        viewModel.sendIntent(EmployerOnlineServicesIntent.SubmitAgreement)
        advanceUntilIdle()

        assertEquals("09121112233", workshops.lastSubmission?.mobile)
        assertEquals("boss@example.com", workshops.lastSubmission?.email)
        assertEquals("654321", workshops.lastSubmission?.ticketCode)
        assertTrue(viewModel.uiState.value.agreementRequest.isSubmitted)
    }

    @Test
    fun loadMoreWorkshopsWithoutContract_appendsTheNextPageInsideStep2() = runTest(testDispatcher) {
        // 12 rows so the default 10-row page leaves a second page to fetch.
        workshops.workshopsWithoutContract = PagedListDN(
            items = (1..12).map {
                WorkshopWithoutContractDN(workshopId = it.toString(), branchCode = "1", name = "کارگاه $it")
            },
            total = 12,
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(EmployerOnlineServicesIntent.OpenAgreementRequest)
        viewModel.sendIntent(EmployerOnlineServicesIntent.UpdateRequestCode("654321"))
        viewModel.sendIntent(EmployerOnlineServicesIntent.VerifyAgreementCode)
        advanceUntilIdle()

        val firstPage = viewModel.uiState.value.agreementRequest
        assertEquals(10, firstPage.workshopsWithoutContract.size)
        // The card's count badge shows the server total, not just what has loaded so far.
        assertEquals(12, firstPage.workshopsWithoutContractTotal)
        assertEquals(false, firstPage.workshopsWithoutContractEndReached)

        viewModel.sendIntent(EmployerOnlineServicesIntent.LoadMoreWorkshopsWithoutContract)
        advanceUntilIdle()

        val afterLoadMore = viewModel.uiState.value.agreementRequest
        assertEquals(12, afterLoadMore.workshopsWithoutContract.size)
        assertEquals(true, afterLoadMore.workshopsWithoutContractEndReached)
        assertEquals(1, workshops.lastWorkshopsWithoutContractPage)
    }

    @Test
    fun submitAgreement_failure_doesNotShowSuccessAndAllowsRetryWithoutRedoingValidation() = runTest(testDispatcher) {
        workshops.contactInfo = EmployerContactInfoDN(
            firstName = "رضا",
            lastName = "کارفرما",
            nationalCode = "0012345678",
            currentMobile = "09120000000",
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(EmployerOnlineServicesIntent.OpenAgreementRequest)
        viewModel.sendIntent(EmployerOnlineServicesIntent.RequestAgreementTicket)
        viewModel.sendIntent(EmployerOnlineServicesIntent.UpdateRequestCode("654321"))
        viewModel.sendIntent(EmployerOnlineServicesIntent.VerifyAgreementCode)
        viewModel.sendIntent(EmployerOnlineServicesIntent.SetAgreementAccepted(true))
        advanceUntilIdle()

        // The submit call itself fails.
        workshops.failing = FakeWorkShopsRepository.Call.SUBMIT
        viewModel.sendIntent(EmployerOnlineServicesIntent.SubmitAgreement)
        advanceUntilIdle()

        val failed = viewModel.uiState.value
        // No false "ثبت شد" — the success dialog must not appear on a failed submit.
        assertEquals(false, failed.agreementRequest.isSubmitted)
        assertTrue(failed.errors.containsKey(EmployerOnlineServicesErrorSource.SUBMIT))
        // The user stays exactly where they were — still on step 2, still accepted, code intact.
        assertEquals(AgreementRequestStep.ACCEPT_AGREEMENT, failed.agreementRequest.step)
        assertTrue(failed.agreementRequest.accepted)
        assertEquals("654321", failed.agreementRequest.code)

        // Retrying resubmits directly — no RequestAgreementTicket/VerifyAgreementCode intent is sent
        // again, i.e. the user is not forced back through OTP to recover from a failed submit.
        workshops.failing = null
        viewModel.sendIntent(EmployerOnlineServicesIntent.SubmitAgreement)
        advanceUntilIdle()

        val retried = viewModel.uiState.value
        assertTrue(retried.agreementRequest.isSubmitted)
        assertTrue(retried.errors.isEmpty())
        assertEquals("654321", workshops.lastSubmission?.ticketCode)
    }

    @Test
    fun resendCode_clearsThePreviouslyEnteredCode() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(EmployerOnlineServicesIntent.OpenAgreementRequest)
        viewModel.sendIntent(EmployerOnlineServicesIntent.RequestAgreementTicket)
        advanceUntilIdle()
        viewModel.sendIntent(EmployerOnlineServicesIntent.UpdateRequestCode("111111"))
        advanceUntilIdle()
        assertEquals("111111", viewModel.uiState.value.agreementRequest.code)
        val firstNonce = viewModel.uiState.value.agreementRequest.ticketNonce

        // "ارسال مجدد کد" re-issues the ticket — the stale code must not linger to be mistakenly verified.
        viewModel.sendIntent(EmployerOnlineServicesIntent.RequestAgreementTicket)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.agreementRequest.code)
        assertTrue(state.agreementRequest.ticketNonce > firstNonce)
    }

    @Test
    fun editContact_clearsCodeAndTicketRequestedState() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(EmployerOnlineServicesIntent.OpenAgreementRequest)
        viewModel.sendIntent(EmployerOnlineServicesIntent.RequestAgreementTicket)
        viewModel.sendIntent(EmployerOnlineServicesIntent.UpdateRequestCode("222222"))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.agreementRequest.ticketRequested)

        // "ویرایش اطلاعات" backs out to the contact form — the old ticket/code must not survive it.
        viewModel.sendIntent(EmployerOnlineServicesIntent.EditAgreementContact)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AgreementRequestStep.VALIDATION, state.agreementRequest.step)
        assertEquals(false, state.agreementRequest.ticketRequested)
        assertEquals("", state.agreementRequest.code)
    }

    @Test
    fun verifyCode_failure_tagsVerifyKeyAndDoesNotAdvanceStep() = runTest(testDispatcher) {
        workshops.failing = FakeWorkShopsRepository.Call.CONTACT_INFO
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(EmployerOnlineServicesIntent.OpenAgreementRequest)
        viewModel.sendIntent(EmployerOnlineServicesIntent.UpdateRequestCode("000000"))
        viewModel.sendIntent(EmployerOnlineServicesIntent.VerifyAgreementCode)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.errors.containsKey(EmployerOnlineServicesErrorSource.VERIFY_CODE))
        assertEquals(AgreementRequestStep.VALIDATION, state.agreementRequest.step)
        assertEquals(false, state.agreementRequest.isSubmitting)
    }

    private fun agreementDN(workshopId: String, branchCode: String) = EmployerAgreementDN(
        startDate = "14030101",
        commitmentDate = "14030102",
        email = "boss@example.com",
        mobile = "09120000000",
        workshop = WorkshopSummaryDN(
            workshopId = workshopId,
            branchCode = branchCode,
            name = "کارگاه الف",
            statusCode = "01",
        ),
    )
}
