package com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionSurvivor.fake.FakePensionSurvivorPersonalRepository
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoIntent
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.useCases.personal.SaveSurvivorInfoUseCase
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
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SurvivorInfoViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakePensionSurvivorPersonalRepository
    private lateinit var viewModel: SurvivorInfoViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePensionSurvivorPersonalRepository()
        viewModel = SurvivorInfoViewModel(
            saveSurvivorInfoUseCase = SaveSurvivorInfoUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_populatesEditableFieldsFromDraft() = runTest(testDispatcher) {
        viewModel.sendIntent(
            SurvivorInfoIntent.Init(
                survivor = sampleSurvivor(),
                deceasedNationalId = "1234567890",
                address = "Tehran",
                phoneNumber = "02122223333",
                mobileNumber = "09121234567",
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Tehran", state.address)
        assertEquals("02122223333", state.phoneNumber)
        assertEquals("09121234567", state.mobileNumber)
    }

    @Test
    fun save_emitsSavedDraftBeforeNavigateBack() = runTest(testDispatcher) {
        val draft = SurvivorContactDraft(
            address = "Tehran",
            phoneNumber = "02122223333",
            mobileNumber = "09121234567",
        )
        viewModel.sendIntent(
            SurvivorInfoIntent.Init(
                survivor = sampleSurvivor(),
                deceasedNationalId = "1234567890",
                address = draft.address,
                phoneNumber = draft.phoneNumber,
                mobileNumber = draft.mobileNumber,
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(SurvivorInfoIntent.Save)
            testDispatcher.scheduler.advanceUntilIdle()

            val saved = assertIs<SurvivorInfoEvent.Saved>(awaitItem())
            assertEquals("0098765432", saved.nationalId)
            assertEquals(draft, saved.draft)
            assertIs<SurvivorInfoEvent.ShowSuccess>(awaitItem())
            assertIs<SurvivorInfoEvent.NavigateBack>(awaitItem())
        }

        val savedBody = requireNotNull(repository.lastSavedSurvivorInfoBody)
        assertEquals(draft.address, savedBody.address)
        assertEquals(draft.phoneNumber, savedBody.phoneNumber)
        assertEquals(draft.mobileNumber, savedBody.mobileNumber)
    }

    private companion object {
        fun sampleSurvivor() = SurvivorDependentPR(
            firstName = "Sara",
            lastName = "Ahmadi",
            nationalId = "0098765432",
            fatherName = "Hossein",
            idCardNumber = "12345",
            cityOfIssue = "Tehran",
            genderCode = "2",
            genderDesc = "زن",
            dateOfBirth = "631152000000",
            insuranceId = "998877",
            tendencyCode = "1",
        )
    }
}
