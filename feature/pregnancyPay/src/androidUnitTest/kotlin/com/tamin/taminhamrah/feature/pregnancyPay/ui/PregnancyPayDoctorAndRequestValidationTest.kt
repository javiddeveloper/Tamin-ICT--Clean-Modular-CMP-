package com.tamin.taminhamrah.feature.pregnancyPay.ui

import com.tamin.taminhamrah.feature.pregnancyPay.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.pregnancyPay.fake.FakePregnancyPayRepository
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayIntent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayOptionUi
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.CalculatePregnancyPayEstimateUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyMainInfoUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyStatusListUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyTypeListUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.SendPregnancyPayRequestUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Covers `PregnancyPayViewModel.validateCrossFieldRules` — the pregnancy-type/request-type
 * mismatch rule and the three request-type-specific rest-day caps — plus the baby-birth-to-rest-
 * start-date sanity check. These messages are built with a suspend `getString(Res.string...)` call
 * that is not wrapped in a try/catch (unlike `loadInitialData`'s), so — per the documented
 * getString-in-ViewModel JVM-test hazard (`.claude/rules/ui-design-system.md`) — this needs
 * Robolectric for a real Android context, same setup as `OrotezProtezDocumentUploadTest`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PregnancyPayDoctorAndRequestValidationTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var pregnancyPayRepository: FakePregnancyPayRepository

    private fun createViewModel(): PregnancyPayViewModel = PregnancyPayViewModel(
        getPregnancyMainInfoUseCase = GetPregnancyMainInfoUseCase(pregnancyPayRepository),
        getPregnancyStatusListUseCase = GetPregnancyStatusListUseCase(pregnancyPayRepository),
        getPregnancyTypeListUseCase = GetPregnancyTypeListUseCase(pregnancyPayRepository),
        uploadImageUseCase = UploadImageUseCase(FakeContractsRepository()),
        sendPregnancyPayRequestUseCase = SendPregnancyPayRequestUseCase(pregnancyPayRepository),
        calculatePregnancyPayEstimateUseCase = CalculatePregnancyPayEstimateUseCase(pregnancyPayRepository),
    )

    /**
     * Advances to [PregnancyPayStep.DoctorAndRequest] with a rest-day count of [restDays] and a
     * baby-birth-to-rest-start difference of [babyBirthDiffDays], then fills in a valid doctor +
     * request type and clicks Next. `restStartDateTimeStamp` is fixed at 0 so both counts are exact
     * multiples of a day.
     */
    private fun advanceAndSubmitDoctorAndRequestStep(
        viewModel: PregnancyPayViewModel,
        restDays: Long,
        babyBirthDiffDays: Long,
        pregnancyTypeId: String = "1",
        requestTypeId: String,
    ) {
        viewModel.sendIntent(PregnancyPayIntent.OnLandingStartClicked)
        viewModel.sendIntent(PregnancyPayIntent.OnRestStartDatePicked(millis = 0L, label = "۱۴۰۵/۰۱/۰۱"))
        viewModel.sendIntent(
            PregnancyPayIntent.OnRestEndDatePicked(millis = restDays * MILLIS_PER_DAY, label = "۱۴۰۵/۰۲/۰۱"),
        )
        viewModel.sendIntent(PregnancyPayIntent.OnNextFromBranchAndRestClicked)
        viewModel.sendIntent(
            PregnancyPayIntent.OnBabyBirthDatePicked(millis = babyBirthDiffDays * MILLIS_PER_DAY, label = "۱۴۰۵/۰۱/۰۲"),
        )
        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyStatusPicked(PregnancyPayOptionUi(id = "1", label = "بارداری طبیعی")),
        )
        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyTypePicked(PregnancyPayOptionUi(id = pregnancyTypeId, label = "نوع")),
        )
        viewModel.sendIntent(PregnancyPayIntent.OnChildNationalCodeChanged(1, "0011122233"))
        if (pregnancyTypeId == "2") viewModel.sendIntent(PregnancyPayIntent.OnChildNationalCodeChanged(2, "0022233344"))
        if (pregnancyTypeId == "3") {
            viewModel.sendIntent(PregnancyPayIntent.OnChildNationalCodeChanged(2, "0022233344"))
            viewModel.sendIntent(PregnancyPayIntent.OnChildNationalCodeChanged(3, "0033344455"))
        }
        viewModel.sendIntent(PregnancyPayIntent.OnNextFromPregnancyAndNewbornClicked)

        viewModel.sendIntent(
            PregnancyPayIntent.OnRequestTypePicked(PregnancyPayOptionUi(id = requestTypeId, label = "نوع درخواست")),
        )
        viewModel.sendIntent(PregnancyPayIntent.OnDoctorNameChanged("دکتر رضایی"))
        viewModel.sendIntent(PregnancyPayIntent.OnDoctorCodeChanged("12345"))

        viewModel.sendIntent(PregnancyPayIntent.OnNextFromDoctorAndRequestClicked)
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        pregnancyPayRepository = FakePregnancyPayRepository().apply {
            mainInfoResult = PregnancyPayTestData.femaleMainInfo
            pregnancyStatusListResult = PregnancyPayTestData.pregnancyStatusOptions
            pregnancyTypeListResult = PregnancyPayTestData.pregnancyTypeOptions
        }
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun upToOneYearRequest_withNonTripletPregnancy_failsTypeMismatchAndStaysOnStep() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 10,
            babyBirthDiffDays = 0,
            pregnancyTypeId = "1",
            requestTypeId = REQUEST_TYPE_UP_TO_ONE_YEAR,
        )

        assertEquals(PregnancyPayStep.DoctorAndRequest, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.doctorAndRequestError)
    }

    @Test
    fun upToOneYearRequest_withTripletPregnancyWithinDayCap_advancesToDocuments() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 300,
            babyBirthDiffDays = 0,
            pregnancyTypeId = "3",
            requestTypeId = REQUEST_TYPE_UP_TO_ONE_YEAR,
        )

        assertEquals(PregnancyPayStep.Documents, viewModel.uiState.value.currentStep)
        assertNull(viewModel.uiState.value.doctorAndRequestError)
    }

    @Test
    fun sixMonthsRequest_restDaysOverCap_blocksAdvanceWithError() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 200, // > SIX_MONTHS_DAY_CAP (186)
            babyBirthDiffDays = 0,
            requestTypeId = REQUEST_TYPE_SIX_MONTHS,
        )

        assertEquals(PregnancyPayStep.DoctorAndRequest, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.doctorAndRequestError)
    }

    @Test
    fun sixMonthsRequest_restDaysWithinCap_advancesToDocuments() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 100,
            babyBirthDiffDays = 0,
            requestTypeId = REQUEST_TYPE_SIX_MONTHS,
        )

        assertEquals(PregnancyPayStep.Documents, viewModel.uiState.value.currentStep)
    }

    @Test
    fun sixToNineMonthsRequest_restDaysOverCap_blocksAdvanceWithError() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 280, // > NINE_MONTHS_DAY_CAP (276)
            babyBirthDiffDays = 0,
            requestTypeId = REQUEST_TYPE_SIX_TO_NINE_MONTHS,
        )

        assertEquals(PregnancyPayStep.DoctorAndRequest, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.doctorAndRequestError)
    }

    @Test
    fun sixToNineMonthsRequest_restDaysWithinCap_advancesToDocuments() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 200,
            babyBirthDiffDays = 0,
            requestTypeId = REQUEST_TYPE_SIX_TO_NINE_MONTHS,
        )

        assertEquals(PregnancyPayStep.Documents, viewModel.uiState.value.currentStep)
    }

    @Test
    fun upToOneYearRequest_tripletWithRestDaysOverCap_blocksAdvanceWithError() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 400, // > TWELVE_MONTHS_DAY_CAP (365)
            babyBirthDiffDays = 0,
            pregnancyTypeId = "3",
            requestTypeId = REQUEST_TYPE_UP_TO_ONE_YEAR,
        )

        assertEquals(PregnancyPayStep.DoctorAndRequest, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.doctorAndRequestError)
    }

    @Test
    fun babyBirthTooFarFromRestStart_blocksAdvanceWithError() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 10,
            babyBirthDiffDays = 70, // > BABY_BIRTH_TO_REST_START_MAX_DIFF_DAYS (63)
            requestTypeId = REQUEST_TYPE_SIX_MONTHS,
        )

        assertEquals(PregnancyPayStep.DoctorAndRequest, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.doctorAndRequestError)
    }

    @Test
    fun babyBirthWithinAllowedDiff_advancesToDocuments() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        advanceAndSubmitDoctorAndRequestStep(
            viewModel,
            restDays = 10,
            babyBirthDiffDays = 60,
            requestTypeId = REQUEST_TYPE_SIX_MONTHS,
        )

        assertEquals(PregnancyPayStep.Documents, viewModel.uiState.value.currentStep)
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
        const val REQUEST_TYPE_SIX_MONTHS = "1"
        const val REQUEST_TYPE_SIX_TO_NINE_MONTHS = "2"
        const val REQUEST_TYPE_UP_TO_ONE_YEAR = "3"
    }
}
