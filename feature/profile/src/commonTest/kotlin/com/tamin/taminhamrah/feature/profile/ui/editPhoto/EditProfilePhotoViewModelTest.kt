package com.tamin.taminhamrah.feature.profile.ui.editPhoto

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoEvent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoIntent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.PhotoDialogState
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfilePhotoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeUserRepository: FakeProfileUserRepository
    private lateinit var fakeCityProvinceRepository: FakeProfileCityProvinceRepository
    private lateinit var viewModel: EditProfilePhotoViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeProfileUserRepository()
        fakeCityProvinceRepository = FakeProfileCityProvinceRepository()
        viewModel = createViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): EditProfilePhotoViewModel {
        return EditProfilePhotoViewModel(
            identityInfoUseCase = IdentityInfoUseCase(fakeUserRepository, fakeCityProvinceRepository),
            taminRelationUseCase = TaminRelationUseCase(fakeUserRepository),
            subdominantUseCase = SubdominantUseCase(fakeUserRepository),
            sendImageRequestUseCase = SendImageRequestUseCase(fakeUserRepository),
        )
    }

    @Test
    fun initialData_loadedCorrectly() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("سعید نامی", state.userName)
        assertEquals("0020939111", state.nationalCode)
        assertEquals("12345678", state.insuranceNumber)
        assertEquals("0010", state.branchCode)
        assertEquals(2, state.dependants.size)
        assertEquals("زهره تابانی", state.dependants[0].fullName)
        assertEquals("همسر", state.dependants[0].relationDescription)
    }

    @Test
    fun serialNumberChanged_limitsToTenChars_andClearsError() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("2P11253499EXTRA"))
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("2P11253499", state.serialNumber)
        assertFalse(state.isSerialError)
    }

    @Test
    fun serialNumberChanged_normalizesAlphanumericPersianDigitsAndLowercaseLetters() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        // Lowercase letter 'p' normalized to 'P', Persian digits normalized to ASCII digits, non-alphanumeric stripped
        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("۲p ۱۱۲۵-۳۴۹۹"))
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("2P11253499", state.serialNumber)
    }

    @Test
    fun dependantModeToggled_updatesState_andClearsSelectionOnToggleOff() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(true))
        testScheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isDependantMode)

        val dep = viewModel.uiState.value.dependants[0]
        viewModel.sendIntent(EditProfilePhotoIntent.DependantSelected(dep))
        testScheduler.advanceUntilIdle()
        assertEquals(dep, viewModel.uiState.value.selectedDependant)

        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(false))
        testScheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isDependantMode)
        assertNull(viewModel.uiState.value.selectedDependant)
    }

    @Test
    fun submit_emptySerial_showsValidationError() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("   "))
        viewModel.sendIntent(EditProfilePhotoIntent.Submit)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSerialError)
        val dialog = state.dialogState as? PhotoDialogState.ValidationError
        assertNotNull(dialog)
        assertEquals(Res.string.profile_photo_error_empty_serial, dialog.message)
    }

    @Test
    fun submit_dependantModeWithoutSelection_showsValidationError() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("123456789"))
        testScheduler.advanceUntilIdle()
        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(true))
        testScheduler.advanceUntilIdle()
        viewModel.sendIntent(EditProfilePhotoIntent.Submit)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isDependantError)
        val dialog = state.dialogState as? PhotoDialogState.ValidationError
        assertNotNull(dialog)
        assertEquals(Res.string.profile_photo_error_select_dependant, dialog.message)
    }

    @Test
    fun submit_mainUser_success() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("123456789"))
        testScheduler.advanceUntilIdle()
        viewModel.sendIntent(EditProfilePhotoIntent.Submit)
        testScheduler.advanceUntilIdle()

        assertEquals("0010", fakeUserRepository.lastSentBranchCode)
        assertEquals("123456789", fakeUserRepository.lastSentSerialId)

        val state = viewModel.uiState.value
        assertEquals(PhotoDialogState.Success("OK"), state.dialogState)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun submit_dependantUser_passesDependantNationalId_success() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("123456789"))
        testScheduler.advanceUntilIdle()
        viewModel.sendIntent(EditProfilePhotoIntent.DependantModeToggled(true))
        testScheduler.advanceUntilIdle()
        val dep = viewModel.uiState.value.dependants[0]
        viewModel.sendIntent(EditProfilePhotoIntent.DependantSelected(dep))
        testScheduler.advanceUntilIdle()
        viewModel.sendIntent(EditProfilePhotoIntent.Submit)
        testScheduler.advanceUntilIdle()

        assertEquals("0010", fakeUserRepository.lastSentBranchCode)
        assertEquals("0061777943", fakeUserRepository.lastSentSerialId)

        val state = viewModel.uiState.value
        assertEquals(PhotoDialogState.Success("OK"), state.dialogState)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun submit_serverError_showsErrorDialog() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()
        fakeUserRepository.shouldThrowError = true
        fakeUserRepository.error = RuntimeException("سرور در دسترس نیست")

        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("123456789"))
        testScheduler.advanceUntilIdle()
        viewModel.sendIntent(EditProfilePhotoIntent.Submit)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        val dialog = state.dialogState as? PhotoDialogState.ServerError
        assertNotNull(dialog)
        assertEquals("سرور در دسترس نیست", dialog.message)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun dismissDialog_onSuccess_emitsNavigateBackOnSuccess() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.sendIntent(EditProfilePhotoIntent.SerialNumberChanged("123456789"))
        testScheduler.advanceUntilIdle()
        viewModel.sendIntent(EditProfilePhotoIntent.Submit)
        testScheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(EditProfilePhotoIntent.DismissDialog)
            testScheduler.advanceUntilIdle()

            val event = awaitItem()
            assertEquals(EditProfilePhotoEvent.NavigateBackOnSuccess, event)
            assertNull(viewModel.uiState.value.dialogState)
        }
    }

    @Test
    fun onBackClicked_emitsNavigateBack() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(EditProfilePhotoIntent.OnBackClicked)
            testScheduler.advanceUntilIdle()

            val event = awaitItem()
            assertEquals(EditProfilePhotoEvent.NavigateBack, event)
        }
    }

    @Test
    fun openGuide_showsGuideDialog() = runTest(testDispatcher) {
        testScheduler.advanceUntilIdle()

        viewModel.sendIntent(EditProfilePhotoIntent.OpenGuide)
        testScheduler.advanceUntilIdle()

        assertEquals(PhotoDialogState.Guide, viewModel.uiState.value.dialogState)
    }
}

private class FakeProfileUserRepository : UserRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var lastSentBranchCode: String? = null
    var lastSentSerialId: String? = null

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {
        if (shouldThrowError) throw error
        emit(
            IdentityInfoDN(
                cityOfBirthId = null,
                cityOfIssueId = null,
                countryId = null,
                dateOfBirth = null,
                fatherName = null,
                firstName = "سعید",
                gender = null,
                id = 1,
                idCardNumber = null,
                idCardSerial1 = null,
                idCardSerial2 = null,
                lastName = "نامی",
                nationalId = "0020939111",
                ssn = null
            )
        )
    }

    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow {
        if (shouldThrowError) throw error
        emit(
            TaminRelationDN(
                firstName = "سعید",
                lastName = "نامی",
                nationalId = "0020939111",
                insuranceId = "12345678",
                brhCode = "0010"
            )
        )
    }

    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flow {
        if (shouldThrowError) throw error
        emit(
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
                    )
                ),
                total = "2"
            )
        )
    }

    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flow {
        if (shouldThrowError) throw error
        lastSentBranchCode = branchCode
        lastSentSerialId = serialId
        emit("OK")
    }

    override suspend fun getUserProfileImage(): Flow<String> = flowOf("")
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flowOf(false)
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow {}
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> = flowOf("")
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> = flowOf(emptyList())
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flowOf(emptyList())
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> = flowOf(emptyList())
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN>> = flowOf(emptyList())
    override suspend fun downloadDocument(url: String): com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN = com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN()
    override suspend fun getUserProfile(): Flow<com.tamin.taminhamrah.model.user.UserProfileDN> = flow {}
    override suspend fun getCurrentUser(): Flow<com.tamin.taminhamrah.model.user.CurrentUserDN> = flowOf(com.tamin.taminhamrah.model.user.CurrentUserDN())
    override suspend fun registerBankAccount(accountNumber: String, bankCode: String, accountTypeCode: String, startDateMillis: Long): Flow<String?> = flowOf(null)
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flowOf(emptyList())
}

private class FakeProfileCityProvinceRepository : CityProvinceRepository {
    override fun getCity(cityId: String): Flow<CityDN> = flow {}
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flow {}
    override fun getProvinces(): Flow<List<ProvinceDN>> = flowOf(emptyList())
    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = flowOf(emptyList())
    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = flowOf(CityListResultDN(emptyList()))
}
