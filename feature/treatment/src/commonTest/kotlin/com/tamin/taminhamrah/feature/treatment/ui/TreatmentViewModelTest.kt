package com.tamin.taminhamrah.feature.treatment.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.treatment.fake.FakeCityProvinceRepository
import com.tamin.taminhamrah.feature.treatment.fake.FakeFeatureManager
import com.tamin.taminhamrah.feature.treatment.fake.FakeTreatmentRepository
import com.tamin.taminhamrah.feature.treatment.fake.FakeUserRepository
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDependantUnderEighteenUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDeservedTreatmentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/**
 * Tests the dashboard [TreatmentViewModel], which owns only patient selection,
 * flow navigation and the deserved/dependant lists. Sub-flow ViewModels are tested
 * separately. Shared test doubles live in the `fake` package so every treatment/health
 * ViewModel test reuses the same fixtures.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TreatmentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var userRepository: FakeUserRepository
    private lateinit var viewModel: TreatmentViewModel
    private lateinit var featureManager: FakeFeatureManager

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTreatmentRepository()
        featureManager = FakeFeatureManager()
        userRepository = FakeUserRepository()
        viewModel = buildViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = TreatmentViewModel(
        getDeservedTreatmentUseCase = GetDeservedTreatmentUseCase(repository),
        getDependantUnderEighteenUseCase = GetDependantUnderEighteenUseCase(repository),
        identityInfoUseCase = IdentityInfoUseCase(userRepository, FakeCityProvinceRepository()),
        featureManager = featureManager
    )

    @Test
    fun testInitTreatmentFlow_loadsDeservedAndDependants() = runTest(testDispatcher) {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(null, initialState.selectedNationalCode)

            viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)

            var state = awaitItem()
            while (state.isLoading || state.selectedNationalCode == null || state.deservedList.isEmpty()) {
                state = awaitItem()
            }

            assertEquals("1234567890", state.selectedNationalCode)
            assertEquals("Seyed Rahmatollah", state.selectedPatientName)
            assertEquals(1, state.deservedList.size)
            assertEquals("Seyed Rahmatollah", state.deservedList.first().fullName)

            assertEquals(1, state.dependantList.size)
            assertEquals("9876543210", state.dependantList.first().nationalId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testSelectPatient_updatesSelectedPatientState() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.sendIntent(TreatmentIntent.SelectPatient("9876543210", "Child Name"))

            val state = awaitItem()
            assertEquals("9876543210", state.selectedNationalCode)
            assertEquals("Child Name", state.selectedPatientName)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun testInitTreatmentFlow_emitsMainUserNationalCodeImmediately() = runTest(testDispatcher) {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(null, initialState.mainUserNationalCode)

            viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)

            var state = awaitItem()
            while (state.mainUserNationalCode == null) {
                state = awaitItem()
            }
            assertEquals("1234567890", state.mainUserNationalCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testInitTreatmentFlow_whenDeservedFails_emitsError() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem() // initial state
            viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)

            var state = awaitItem()
            while (state.error == null) {
                state = awaitItem()
            }
            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testInitTreatmentFlow_whenNoUserIdAndNoIdentity_emitsUserNotFoundError() = runTest(testDispatcher) {
        userRepository = FakeUserRepository().apply {
            identityResult = identityResult.copy(nationalId = null)
        }
        viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem() // initial state
            viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)

            var state = awaitItem()
            while (state.error == null) {
                state = awaitItem()
            }
            assertEquals("اطلاعات کاربری یافت نشد.", state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun openRecords_whenFeatureEnabled_emitsNavigation() = runTest(testDispatcher) {
        featureManager.status = FeatureStatus.Enabled
        viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(TreatmentIntent.OpenRecords(RecordTab.MEDICINE))
            val event = assertIs<TreatmentEvent.NavigateToRecords>(awaitItem())
            assertEquals(RecordTab.MEDICINE, event.tab)
        }
    }

    /**
     * A flag lookup that fails must not lock the person out — the gate opens rather than closing,
     * which is the whole point of the fallback in `openRecords`.
     */
    @Test
    fun openRecords_whenFlagLookupFails_navigatesAnyway() = runTest(testDispatcher) {
        featureManager.error = RuntimeException("flags unreachable")
        viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(TreatmentIntent.OpenRecords(RecordTab.MEDICINE))
            val event = assertIs<TreatmentEvent.NavigateToRecords>(awaitItem())
            assertEquals(RecordTab.MEDICINE, event.tab)
        }
    }

    /**
     * An identity lookup that fails leaves no national code, and the flow stops with the same
     * message an empty one produces rather than loading a nameless patient.
     */
    @Test
    fun testInitTreatmentFlow_whenIdentityLookupFails_emitsUserNotFoundError() = runTest(testDispatcher) {
        userRepository = FakeUserRepository().apply {
            identityError = RuntimeException("identity unreachable")
        }
        viewModel = buildViewModel()

        viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("اطلاعات کاربری یافت نشد.", viewModel.uiState.value.error)
    }

    @Test
    fun openRecords_whenFeatureDisabled_explainsInsteadOfNavigating() = runTest(testDispatcher) {
        featureManager.status = FeatureStatus.Disabled("سرویس غیرفعال است")
        viewModel.sendIntent(TreatmentIntent.InitTreatmentFlow)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(TreatmentIntent.OpenRecords(RecordTab.MEDICINE))
            // The gate must say why rather than silently doing nothing.
            val event = assertIs<TreatmentEvent.ShowMessage>(awaitItem())
            assertEquals("سرویس غیرفعال است", event.message)
        }
    }

}
