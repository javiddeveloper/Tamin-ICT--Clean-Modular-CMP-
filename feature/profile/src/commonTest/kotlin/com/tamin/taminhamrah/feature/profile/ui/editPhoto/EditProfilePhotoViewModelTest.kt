package com.tamin.taminhamrah.feature.profile.ui.editPhoto

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoEvent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoIntent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.PhotoDialogState
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

private class FakeProfileUserRepository : UserRepository {
    var relationError: Throwable? = null
    var sendImageError: Throwable? = null
    var lastSentBranchCode: String? = null
    var lastSentSerialId: String? = null

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {
        relationError?.let { throw it }
        emit(
            TaminRelationDN(
                firstName = "سعید",
                lastName = "نامی",
                nationalId = "0020939111",
                insuranceId = "12345678",
                brhCode = "0010",
            )
        )
    }

    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flowOf(
        SubdominantDN(
            list = listOf(
                SubdominantItemDN(
                    id = 101L,
                    firstName = "زهره",
                    lastName = "تابانی",
                    nationalCode = "0061777943",
                    relationDescription = "همسر",
                ),
                SubdominantItemDN(
                    id = 102L,
                    firstName = "آرش",
                    lastName = "تابانی",
                    nationalCode = "0024551902",
                    relationDescription = "فرزند پسر",
                ),
            ),
            total = "2",
        )
    )

    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flow {
        sendImageError?.let { throw it }
        lastSentBranchCode = branchCode
        lastSentSerialId = serialId
        emit("OK")
    }

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {}
    override suspend fun getUserProfileImage(): Flow<String> = flowOf("")
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flowOf(false)
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow {}
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flowOf("")
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = flowOf(emptyList())
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flowOf(emptyList())
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = flowOf(emptyList())
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = flowOf(emptyList())
    override suspend fun downloadDocument(url: String): PdfDownloadDN = PdfDownloadDN()
    override suspend fun getUserProfile(): Flow<UserProfileDN> = flow {}
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = flowOf(CurrentUserDN())
    override suspend fun registerBankAccount(accountNumber: String, bankCode: String, accountTypeCode: String, startDateMillis: Long): Flow<String?> = flowOf(null)
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flowOf(emptyList())
}
