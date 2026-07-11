package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.treatment.fake.FakeTreatmentRepository
import com.tamin.taminhamrah.feature.treatment.fake.TreatmentTestData
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsIntent
import com.tamin.taminhamrah.useCases.treatment.GetMedicalAuthoritiesUseCase
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
class MedicalConfirmationsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var viewModel: MedicalConfirmationsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTreatmentRepository()
        viewModel = MedicalConfirmationsViewModel(
            getMedicalAuthoritiesUseCase = GetMedicalAuthoritiesUseCase(repository)
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun testLoadList_populatesMedicalAuthorities() = runTest(testDispatcher) {
        repository.medicalAuthoritiesResult = listOf(TreatmentTestData.medicalAuthority())

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(ConfirmationsIntent.LoadList)

            var state = awaitItem()
            while (state.medicalAuthorities.isEmpty()) state = awaitItem()

            assertEquals(1, state.medicalAuthorities.size)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun testLoadList_whenRepositoryFails_emitsError() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(ConfirmationsIntent.LoadList)

            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
        }
    }
}
