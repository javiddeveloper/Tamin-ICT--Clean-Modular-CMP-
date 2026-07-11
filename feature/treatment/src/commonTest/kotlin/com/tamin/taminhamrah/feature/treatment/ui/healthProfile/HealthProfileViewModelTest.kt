package com.tamin.taminhamrah.feature.treatment.ui.healthProfile

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.treatment.fake.FakeHealthRepository
import com.tamin.taminhamrah.feature.treatment.fake.TreatmentTestData
import com.tamin.taminhamrah.feature.treatment.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.useCases.health.GetPatientDrugAllergiesUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientGeneralUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientSelfDeclarativeUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class HealthProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val nationalCode = TreatmentTestData.MAIN_NATIONAL_CODE

    private lateinit var repository: FakeHealthRepository
    private lateinit var viewModel: HealthProfileViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeHealthRepository()
        viewModel = HealthProfileViewModel(
            getPatientGeneralUseCase = GetPatientGeneralUseCase(repository),
            getPatientSelfDeclarativeUseCase = GetPatientSelfDeclarativeUseCase(repository),
            getPatientDrugAllergiesUseCase = GetPatientDrugAllergiesUseCase(repository)
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun testLoadProfile_loadsGeneralAndSelfDeclarative() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(HealthProfileIntent.LoadProfile(nationalCode))

            var state = awaitItem()
            while (state.patientGeneral == null || state.patientSelfDeclarative == null) {
                state = awaitItem()
            }

            assertNotNull(state.patientGeneral)
            assertNotNull(state.patientSelfDeclarative)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun testLoadProfile_loadsDrugAllergies() = runTest(testDispatcher) {
        repository.drugAllergiesResult = listOf(TreatmentTestData.drugAllergy())

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(HealthProfileIntent.LoadProfile(nationalCode))

            var state = awaitItem()
            while (state.patientDrugAllergies.isEmpty()) state = awaitItem()

            assertEquals(1, state.patientDrugAllergies.size)
        }
    }

    @Test
    fun testLoadProfile_whenRepositoryFails_emitsError() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(HealthProfileIntent.LoadProfile(nationalCode))

            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
        }
    }
}
