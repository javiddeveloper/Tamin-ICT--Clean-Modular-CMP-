package com.tamin.taminhamrah.feature.retirementPension.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.retirementPension.fake.FakeCityProvinceRepository
import com.tamin.taminhamrah.feature.retirementPension.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.retirementPension.fake.FakeHistoryRepository
import com.tamin.taminhamrah.feature.retirementPension.fake.FakePensionRepository
import com.tamin.taminhamrah.feature.retirementPension.fake.FakePersonalRepository
import com.tamin.taminhamrah.feature.retirementPension.fake.FakeUserRepository
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDialog
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionEvent
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionIntent
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionUiState
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementScreen
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementStep
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.retirement.JobDN
import com.tamin.taminhamrah.model.pension.retirement.RETIREMENT_REQUEST_STATUS_CREATED
import com.tamin.taminhamrah.model.pension.retirement.RetirementBranchInfoPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementIdentityPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementInsuredPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.WorkDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.pension.AuthenticationAndGetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.pension.CheckRetirementStatusUseCase
import com.tamin.taminhamrah.useCases.pension.CreateRetirementRequestUseCase
import com.tamin.taminhamrah.useCases.pension.GetAuthenticationCodeUseCase
import com.tamin.taminhamrah.useCases.pension.GetRetirementRequestInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
import com.tamin.taminhamrah.useCases.pension.SendRetirementDocumentUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetInsuredActiveBranchUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RetirementPensionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var userRepository: FakeUserRepository
    private lateinit var cityProvinceRepository: FakeCityProvinceRepository
    private lateinit var personalRepository: FakePersonalRepository
    private lateinit var historyRepository: FakeHistoryRepository
    private lateinit var contractsRepository: FakeContractsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        pensionRepository = FakePensionRepository()
        userRepository = FakeUserRepository()
        cityProvinceRepository = FakeCityProvinceRepository()
        personalRepository = FakePersonalRepository()
        historyRepository = FakeHistoryRepository()
        contractsRepository = FakeContractsRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): RetirementPensionViewModel {
        return RetirementPensionViewModel(
            identityInfoUseCase = IdentityInfoUseCase(userRepository, cityProvinceRepository),
            getPersonalInfoUseCase = GetPersonalInfoUseCase(personalRepository),
            getInsuredActiveBranchUseCase = GetInsuredActiveBranchUseCase(userRepository),
            getUserAgeUseCase = GetUserAgeUseCase(pensionRepository),
            checkRetirementStatusUseCase = CheckRetirementStatusUseCase(pensionRepository),
            getAuthenticationCodeUseCase = GetAuthenticationCodeUseCase(pensionRepository),
            authenticationAndGetPersonalInfoUseCase = AuthenticationAndGetPersonalInfoUseCase(pensionRepository),
            getRetirementRequestInfoUseCase = GetRetirementRequestInfoUseCase(pensionRepository),
            getTalfighInfosUseCase = GetTalfighInfosUseCase(historyRepository),
            getDastmozdInfosUseCase = GetDastmozdInfosUseCase(historyRepository),
            createRetirementRequestUseCase = CreateRetirementRequestUseCase(pensionRepository),
            uploadImageUseCase = UploadImageUseCase(contractsRepository),
            sendRetirementDocumentUseCase = SendRetirementDocumentUseCase(pensionRepository),
        )
    }

    @Test
    fun init_loadsIntroData_setsInsuredAndAgeEligibility_above42() = runTest(testDispatcher) {
        pensionRepository.userAgeResult = AgeDN(age = "45,3,5", birthDate = "1359/01/01")

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isIntroLoading)
        assertTrue(state.isAgeEligible)
        assertNotNull(state.insured)
        assertEquals("علی محمدی", state.insured?.fullName)
        assertEquals("12345678", state.insured?.insuranceNumber)
        assertEquals("45,3,5", state.rawAge)
    }

    @Test
    fun init_whenAgeUnder42_marksAgeIneligible_andShowsAgeGateDialog() = runTest(testDispatcher) {
        // Under 42 years old (e.g. 41)
        pensionRepository.userAgeResult = AgeDN(age = "41,0,0", birthDate = "1363/01/01")

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isAgeEligible)
        assertEquals(RetirementDialog.AgeGate, state.dialog)
        assertEquals(RetirementScreen.Intro, state.screen)
    }

    @Test
    fun init_whenAgeExactly42_isAgeEligibleIsTrue() = runTest(testDispatcher) {
        pensionRepository.userAgeResult = AgeDN(age = "42,0,0", birthDate = "1362/01/01")

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isAgeEligible, "Age 42 must be eligible per old_android statutory minimum")
    }

    @Test
    fun startRequest_whenEligible_navigatesToFormRulesStep() = runTest(testDispatcher) {
        pensionRepository.userAgeResult = AgeDN(age = "50,0,0", birthDate = "1354/01/01")

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(RetirementPensionIntent.StartRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(RetirementScreen.Form, state.screen)
        assertEquals(RetirementStep.Rules, state.step)
    }

    @Test
    fun rulesStep_viewRules_emitsOpenRulesDocumentEvent() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(RetirementPensionIntent.ViewRules)
            testDispatcher.scheduler.advanceUntilIdle()

            val event = awaitItem()
            assertEquals(RetirementPensionEvent.OpenRulesDocument, event)
        }
    }

    @Test
    fun rulesStep_consentGating_and_nextStep() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(RetirementPensionIntent.StartRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        // NextStep without consent fails
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.validationAttempted)
        assertEquals(RetirementFormError.Consent, viewModel.uiState.value.visibleFormError)
        assertEquals(RetirementStep.Rules, viewModel.uiState.value.step)

        // Give consent
        viewModel.sendIntent(RetirementPensionIntent.ConsentChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.consentAccepted)

        // Now NextStep advances to Authentication
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RetirementStep.Authentication, viewModel.uiState.value.step)
    }

    @Test
    fun authenticationStep_requestOtp_success() = runTest(testDispatcher) {
        pensionRepository.authenticationCodeResult = AuthenticationTicketDN(mobileNumber = "09129998877")

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(RetirementPensionIntent.StartRequest)
        viewModel.sendIntent(RetirementPensionIntent.ConsentChanged(true))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(RetirementPensionIntent.RequestOtp)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.otpSent)
        assertEquals("09129998877", state.otpMobile)
    }

    @Test
    fun authenticationStep_verifyOtp_success_prefillsIdentityAndAdvances() = runTest(testDispatcher) {
        pensionRepository.authenticationCodeResult = AuthenticationTicketDN(mobileNumber = "09121112233")
        pensionRepository.authenticationAndGetPersonalInfoResult = sampleRetirementPersonal(ticketCode = 5555L)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(RetirementPensionIntent.StartRequest)
        viewModel.sendIntent(RetirementPensionIntent.ConsentChanged(true))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(RetirementPensionIntent.RequestOtp)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            // Enter 6 digit OTP
            viewModel.sendIntent(RetirementPensionIntent.OtpChanged("123456"))
            testDispatcher.scheduler.advanceUntilIdle()

            val event = awaitItem()
            assertEquals(RetirementPensionEvent.AuthenticationSucceeded, event)

            val state = viewModel.uiState.value
            assertTrue(state.otpVerified)
            assertFalse(state.otpInvalid)
            assertEquals(123456L, state.ticketCode)
            assertEquals("حسن", state.identity?.fatherName)
            assertEquals("02188776655", state.phoneNumber)
            assertEquals("تهران، خیابان ولیعصر، پلاک ۱۰", state.address)
        }
    }

    @Test
    fun authenticationStep_verifyOtp_ticketNotFound_marksInvalid() = runTest(testDispatcher) {
        pensionRepository.authenticationCodeResult = AuthenticationTicketDN(mobileNumber = "09121112233")
        pensionRepository.authenticationAndGetPersonalInfoResult = RetirementPersonalDN(
            branch = null,
            branchName = null,
            insuranceId = null,
            mobileNumber = null,
            organizationId = null,
            personal = null,
            provinceName = null,
            work = null,
            strAge = null,
            verificationResult = "ticketNotFound",
        )

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(RetirementPensionIntent.StartRequest)
        viewModel.sendIntent(RetirementPensionIntent.ConsentChanged(true))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(RetirementPensionIntent.RequestOtp)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(RetirementPensionIntent.OtpChanged("999999"))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.otpVerified)
        assertTrue(state.otpInvalid)
    }

    @Test
    fun identityStep_addressValidation_rejectsInvalidAndAcceptsValid() = runTest(testDispatcher) {
        pensionRepository.authenticationAndGetPersonalInfoResult = sampleRetirementPersonal(ticketCode = 1111L)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(RetirementPensionIntent.StartRequest)
        viewModel.sendIntent(RetirementPensionIntent.ConsentChanged(true))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        viewModel.sendIntent(RetirementPensionIntent.OtpChanged("123456"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Move to Identity step
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(RetirementStep.Identity, viewModel.uiState.value.step)

        // Set address with Latin letters
        viewModel.sendIntent(RetirementPensionIntent.AddressChanged("Tehran Azadi Street No 10"))
        viewModel.sendIntent(RetirementPensionIntent.IdentityConfirmedChanged(true))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(RetirementFormError.AddressInvalid, viewModel.uiState.value.visibleFormError)
        assertEquals(RetirementStep.Identity, viewModel.uiState.value.step)

        // Set valid Persian address
        viewModel.sendIntent(RetirementPensionIntent.AddressChanged("تهران، خیابان آزادی، کوچه مریم، پلاک ۱۰"))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        // Advances to Workshop step
        assertEquals(RetirementStep.Workshop, viewModel.uiState.value.step)
    }

    @Test
    fun toForm_buildsExactRequestPayloadWithAllFieldsPopulated() {
        val state = RetirementPensionUiState(
            activityType = "کارمند امور مالی",
            address = "  تهران، خیابان کارگر، پلاک ۵  ",
            rawAge = "52,4,10",
            identity = RetirementIdentityPR(
                birthDateEpoch = 123456789L,
                fatherName = "محمد",
                firstName = "رضا",
                gender = "مرد",
                genderCode = "01",
                birthDate = "1350/01/01",
                idNumber = "1234",
                issuePlace = "تهران",
                lastName = "صادقی",
                mobileNumber = "09121112233",
                nationalCode = "0012345678",
            ),
            branch = RetirementBranchInfoPR(
                branchCode = "0100",
                branchName = "شعبه ۱ تهران",
            ),
            insured = RetirementInsuredPR(
                fullName = "رضا صادقی",
                insuranceNumber = "99887766",
                nationalCode = "0012345678",
                ageYears = "۵۲",
                ageMonths = "۴",
                branchName = "شعبه ۱ تهران",
            ),
            employerName = "شرکت پارس نوین",
            otpMobile = "09121112233",
            phoneNumber = "02188776655",
            workshopAddress = "  تهران، جاده مخصوص، خیابان دهم  ",
            workshopCode = "1024300719",
            workshopName = "پارس نوین",
        )

        val form = state.toForm()

        assertEquals("کارمند امور مالی", form.activityType)
        assertEquals("تهران، خیابان کارگر، پلاک ۵", form.address)
        assertEquals("52,4,10", form.age)
        assertEquals(123456789L, form.birthDate)
        assertEquals("0100", form.branchCode)
        assertEquals("محمد", form.fatherName)
        assertEquals("رضا", form.firstName)
        assertEquals("01", form.gender)
        assertEquals("1234", form.idNumber)
        assertEquals("99887766", form.insuranceNumber)
        assertEquals("تهران", form.issuePlace)
        assertEquals("صادقی", form.lastName)
        assertEquals("شرکت پارس نوین", form.managerName)
        assertEquals("09121112233", form.mobileNumber)
        assertEquals("0012345678", form.nationalCode)
        assertEquals("02188776655", form.phoneNumber)
        assertEquals(RETIREMENT_REQUEST_STATUS_CREATED, form.status)
        assertEquals("تهران، جاده مخصوص، خیابان دهم", form.workshopAddress)
        assertEquals("1024300719", form.workshopCode)
        assertEquals("پارس نوین", form.workshopName)
    }

    @Test
    fun finalStep_submission_callsCreateRetirementRequestWithExpectedPayload() = runTest(testDispatcher) {
        pensionRepository.authenticationAndGetPersonalInfoResult = sampleRetirementPersonal(ticketCode = 7777L)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // 1. Rules
        viewModel.sendIntent(RetirementPensionIntent.StartRequest)
        viewModel.sendIntent(RetirementPensionIntent.ConsentChanged(true))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)

        // 2. Authentication
        viewModel.sendIntent(RetirementPensionIntent.OtpChanged("123456"))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)

        // 3. Identity
        viewModel.sendIntent(RetirementPensionIntent.PhoneChanged("02188776655"))
        viewModel.sendIntent(RetirementPensionIntent.AddressChanged("تهران، خیابان آزادی، پلاک ۱۰"))
        viewModel.sendIntent(RetirementPensionIntent.IdentityConfirmedChanged(true))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)

        // 4. Workshop
        viewModel.sendIntent(RetirementPensionIntent.WorkshopNameChanged("کارگاه تست"))
        viewModel.sendIntent(RetirementPensionIntent.WorkshopCodeChanged("1024300719"))
        viewModel.sendIntent(RetirementPensionIntent.WorkshopAddressChanged("تهران، جاده مخصوص، پلاک ۱"))
        viewModel.sendIntent(RetirementPensionIntent.WorkshopConfirmedChanged(true))
        viewModel.sendIntent(RetirementPensionIntent.NextStep)

        // 5. History
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(RetirementPensionIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        // Advancing from History step creates the retirement request and transitions to IdentityDocuments
        assertEquals("12345", viewModel.uiState.value.requestId)
        assertEquals(RetirementStep.IdentityDocuments, viewModel.uiState.value.step)
        assertEquals(123456L, pensionRepository.lastCreateRequestTicketCode)
        assertNotNull(pensionRepository.lastCreateRequestForm)
        assertEquals(RETIREMENT_REQUEST_STATUS_CREATED, pensionRepository.lastCreateRequestForm?.status)
        assertEquals("02188776655", pensionRepository.lastCreateRequestForm?.phoneNumber)
    }

    private fun sampleRetirementPersonal(ticketCode: Long) = RetirementPersonalDN(
        branch = "0100",
        branchName = "شعبه ۱ تهران",
        insuranceId = "12345678",
        mobileNumber = "09121112233",
        organizationId = "1",
        personal = PersonalDN(
            firstName = "علی",
            lastName = "محمدی",
            fatherName = "حسن",
            nationalId = "0012345678",
            ssn = "11111111",
            genderDesc = "مرد",
            genderCode = "01",
            dateOfBirth = 100000000L,
            idCardNumber = "1234",
            contactAddress = "تهران، خیابان ولیعصر، پلاک ۱۰",
            contactPhoneNumber = "02188776655",
        ),
        provinceName = "تهران",
        work = WorkDN(
            job = JobDN(
                jobCode = "101",
                jobDescription = "کارشناس نرم‌افزار",
                status = "1",
                statusDate = "1402/01/01",
            ),
            workshopId = "1024300719",
        ),
        strAge = "50,0,0",
        verificationResult = ticketCode.toString(),
    )
}
