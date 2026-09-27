package com.tamin.taminhamrah.feature.profile.ui.editPhoto

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.profile.fake.FakeProfileUserRepository
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoEvent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoIntent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.PhotoDialogState
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import taminx.core.core_ui.Res
import taminx.core.core_ui.profile_photo_error_empty_serial
import taminx.core.core_ui.profile_photo_error_select_dependant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfilePhotoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeUserRepository: FakeProfileUserRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeProfileUserRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = EditProfilePhotoViewModel(
        taminRelationUseCase = TaminRelationUseCase(fakeUserRepository),
        subdominantUseCase = SubdominantUseCase(fakeUserRepository),
        sendImageRequestUseCase = SendImageRequestUseCase(fakeUserRepository),
    )

    @Test
    fun initialData_fillsCardFromRelationAndLoadsDependants() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertEquals("سعید نامی", state.userName)
        assertEquals("0020939111", state.nationalCode)
        assertEquals("12345678", state.insuranceNumber)
        assertEquals("0010", state.branchCode)
        assertEquals(2, state.dependants.size)
        assertEquals("همسر", state.dependants[0].relationDescription)
        assertFalse(state.isDependantsLoading)
        assertNull(state.dialogState)
    }

    @Test
    fun relationFailure_showsErrorDialog_andStillLoadsDependants() = runTest(testDispatcher) {
        fakeUserRepository.relationError = RuntimeException("سرویس در دسترس نیست")
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertEquals(PhotoDialogState.ServerError("سرویس در دسترس نیست"), state.dialogState)
        assertEquals(2, state.dependants.size)
    }

    @Test
    fun dependantModeToggledOff_clearsSelection() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(true))
        viewModel.sendIntent(EditProfilePhotoIntent.DependantSelected(viewModel.uiState.value.dependants[0]))

        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(false))

        assertFalse(viewModel.uiState.value.isDependantMode)
        assertNull(viewModel.uiState.value.selectedDependant)
    }

    @Test
    fun submit_emptySerialInDependantMode_flagsBothFields_andNamesTheSerialFirst() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(true))

        viewModel.sendIntent(EditProfilePhotoIntent.Submit)

        val state = viewModel.uiState.value
        assertTrue(state.isSerialError)
        assertTrue(state.isDependantError)
        assertEquals(PhotoDialogState.ValidationError(Res.string.profile_photo_error_empty_serial), state.dialogState)
        assertNull(fakeUserRepository.lastSentSerialId)
    }

    @Test
    fun submit_dependantModeWithoutSelection_showsDependantError() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("1G50497996"))
        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(true))

        viewModel.sendIntent(EditProfilePhotoIntent.Submit)

        val state = viewModel.uiState.value
        assertFalse(state.isSerialError)
        assertTrue(state.isDependantError)
        assertEquals(PhotoDialogState.ValidationError(Res.string.profile_photo_error_select_dependant), state.dialogState)
        assertNull(fakeUserRepository.lastSentSerialId)
    }

    @Test
    fun submit_mainUser_sendsSerial_andShowsFixedSuccess() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("1G50497996"))

        viewModel.sendIntent(EditProfilePhotoIntent.Submit)

        assertEquals("0010", fakeUserRepository.lastSentBranchCode)
        assertEquals("1G50497996", fakeUserRepository.lastSentSerialId)
        assertEquals(PhotoDialogState.Success, viewModel.uiState.value.dialogState)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun submit_dependant_sendsTheirNationalCodeInsteadOfTheSerial() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("1G50497996"))
        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(true))
        viewModel.sendIntent(EditProfilePhotoIntent.DependantSelected(viewModel.uiState.value.dependants[0]))

        viewModel.sendIntent(EditProfilePhotoIntent.Submit)

        assertEquals("0061777943", fakeUserRepository.lastSentSerialId)
        assertEquals(PhotoDialogState.Success, viewModel.uiState.value.dialogState)
    }

    @Test
    fun submit_serverError_showsItsMessage_andEndsSubmitting() = runTest(testDispatcher) {
        fakeUserRepository.sendImageError = RuntimeException("سرور در دسترس نیست")
        val viewModel = createViewModel()
        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("1G50497996"))

        viewModel.sendIntent(EditProfilePhotoIntent.Submit)

        assertEquals(PhotoDialogState.ServerError("سرور در دسترس نیست"), viewModel.uiState.value.dialogState)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun dismissSuccess_navigatesBack() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("1G50497996"))
        viewModel.sendIntent(EditProfilePhotoIntent.Submit)

        viewModel.events.test {
            viewModel.sendIntent(EditProfilePhotoIntent.DismissDialog)

            assertEquals(EditProfilePhotoEvent.NavigateBack, awaitItem())
            assertNull(viewModel.uiState.value.dialogState)
        }
    }

    @Test
    fun dismissError_keepsTheForm() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(EditProfilePhotoIntent.Submit)

        viewModel.events.test {
            viewModel.sendIntent(EditProfilePhotoIntent.DismissDialog)

            expectNoEvents()
            assertNull(viewModel.uiState.value.dialogState)
        }
    }

    @Test
    fun submit_calledTwiceInARow_onlySendsOnce() {
        // StandardTestDispatcher orders work like a real main thread; UnconfinedTestDispatcher runs
        // every step eagerly and passes even when the guard reads the reduced uiState.
        val standardDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(standardDispatcher)
        runTest(standardDispatcher) {
            val gate = CompletableDeferred<Unit>()
            fakeUserRepository.sendImageGate = gate
            val viewModel = createViewModel()
            viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("1G50497996"))
            advanceUntilIdle()

            viewModel.sendIntent(EditProfilePhotoIntent.Submit)
            viewModel.sendIntent(EditProfilePhotoIntent.Submit)
            // The first request suspends on the gate while the second reaches the guard.
            advanceUntilIdle()
            gate.complete(Unit)
            advanceUntilIdle()

            assertEquals(1, fakeUserRepository.sendImageCallCount)
            assertEquals(PhotoDialogState.Success, viewModel.uiState.value.dialogState)
        }
    }

    @Test
    fun onBackClicked_navigatesBack() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(EditProfilePhotoIntent.OnBackClicked)

            assertEquals(EditProfilePhotoEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun openGuide_showsGuideDialog() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.sendIntent(EditProfilePhotoIntent.OpenGuide)

        assertEquals(PhotoDialogState.Guide, viewModel.uiState.value.dialogState)
    }
}
