package com.tamin.taminhamrah.feature.treatment.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.treatment.fake.FakeCityProvinceRepository
import com.tamin.taminhamrah.feature.treatment.fake.FakeTokenStoreManager
import com.tamin.taminhamrah.feature.treatment.fake.FakeTreatmentRepository
import com.tamin.taminhamrah.feature.treatment.fake.FakeUserRepository
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
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

    private lateinit var tokenStoreManager: FakeTokenStoreManager
    private lateinit var repository: FakeTreatmentRepository
    private lateinit var userRepository: FakeUserRepository
    private lateinit var viewModel: TreatmentViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tokenStoreManager = FakeTokenStoreManager()
        repository = FakeTreatmentRepository()
        userRepository = FakeUserRepository()
        viewModel = buildViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = TreatmentViewModel(
        tokenStoreManager = tokenStoreManager,
        getDeservedTreatmentUseCase = GetDeservedTreatmentUseCase(repository),
        getDependantUnderEighteenUseCase = GetDependantUnderEighteenUseCase(repository),
        identityInfoUseCase = IdentityInfoUseCase(userRepository, FakeCityProvinceRepository())
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
        }
    }

    @Test
    fun testInitTreatmentFlow_whenNoUserIdAndNoIdentity_emitsUserNotFoundError() = runTest(testDispatcher) {
        tokenStoreManager = FakeTokenStoreManager(storedUserId = null)
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
        }
    }
}
