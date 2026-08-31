package com.tamin.taminhamrah.feature.inquiryEducation.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationEvent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationIntent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationStep
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.inquiryEducation.InquiryEducationRepository
import com.tamin.taminhamrah.useCases.inquiryEducation.GetDataForEducationUseCase
import com.tamin.taminhamrah.useCases.inquiryEducation.InquiryEducationCertificateUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * ViewModel paths call `getString(Res.string…)`, which needs an Android context.
 * Robolectric provides that context for JVM unit tests (see orotez-protez module).
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class InquiryEducationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeInquiryEducationRepository
    private lateinit var userRepository: FakeUserRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeInquiryEducationRepository()
        userRepository = FakeUserRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel(): InquiryEducationViewModel = InquiryEducationViewModel(
        getDataForEducationUseCase = GetDataForEducationUseCase(repository),
        inquiryEducationCertificateUseCase = InquiryEducationCertificateUseCase(repository),
        getUserProfileUseCase = GetUserProfileUseCase(userRepository),
    )

    @Test
    fun load_autoSelectsSingleSon() = runTest(testDispatcher) {
        repository.dependentsResult = singleSon()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val loaded = awaitUntil { !it.isLoading && it.sons.isNotEmpty() }
            assertEquals("0012345678", loaded.selectedNationalId)
            assertEquals(1, loaded.sons.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun load_multipleSons_noAutoSelect() = runTest(testDispatcher) {
        repository.dependentsResult = multipleSons()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val loaded = awaitUntil { !it.isLoading && it.sons.size == 2 }
            assertNull(loaded.selectedNationalId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submit_withoutSon_setsSonError() = runTest(testDispatcher) {
        repository.dependentsResult = multipleSons()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { !it.isLoading && it.sons.size == 2 }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged("ABCD123456"))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            val state = awaitUntil { it.sonSelectionError != null }
            assertEquals(SON_ERROR, state.sonSelectionError)
            assertNull(state.educationCodeError)
            assertEquals(InquiryEducationStep.Form, state.step)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submit_invalidCode_setsCodeError() = runTest(testDispatcher) {
        repository.dependentsResult = singleSon()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { it.selectedNationalId != null }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged("short"))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            val state = awaitUntil { it.educationCodeError != null }
            assertEquals(CODE_ERROR, state.educationCodeError)
            assertNull(state.sonSelectionError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submit_success_movesToSuccess_withUniversityFromApi() = runTest(testDispatcher) {
        repository.dependentsResult = singleSon()
        repository.certificateResult = InquiryEducationCertificateDN(message = UNIVERSITY_NAME)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { it.selectedNationalId != null }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged(VALID_CODE))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            val state = awaitUntil { it.step == InquiryEducationStep.Success }
            assertEquals(STUDENT_NAME, state.studentName)
            assertEquals("0012345678", state.studentNationalId)
            assertEquals(UNIVERSITY_NAME, state.universityName)
            assertEquals(VALID_CODE, state.inquiryCode)
            assertTrue(state.inquiryDate.isNotBlank())
            assertTrue(state.successMessage.contains(STUDENT_NAME))
            assertTrue(state.successMessage.contains(UNIVERSITY_NAME))
            assertFalse(state.isSubmitting)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submit_blankMessage_movesToFailure_withEmptyCopy() = runTest(testDispatcher) {
        repository.dependentsResult = singleSon()
        repository.certificateResult = InquiryEducationCertificateDN(message = "")
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { it.selectedNationalId != null }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged(VALID_CODE))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            val state = awaitUntil { it.step == InquiryEducationStep.Failure }
            assertEquals(FAILURE_EMPTY, state.failureMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submit_throw_movesToFailure() = runTest(testDispatcher) {
        repository.dependentsResult = singleSon()
        repository.shouldThrowOnCertificate = true
        repository.certificateError = RuntimeException("network error")
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { it.selectedNationalId != null }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged(VALID_CODE))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            val state = awaitUntil { it.step == InquiryEducationStep.Failure }
            assertEquals("network error", state.failureMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun retry_returnsToForm_keepsInputs() = runTest(testDispatcher) {
        repository.dependentsResult = singleSon()
        repository.certificateResult = InquiryEducationCertificateDN(message = "")
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { it.selectedNationalId != null }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged(VALID_CODE))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            awaitUntil { it.step == InquiryEducationStep.Failure }
            viewModel.sendIntent(InquiryEducationIntent.Retry)
            val state = awaitUntil { it.step == InquiryEducationStep.Form && it.failureMessage.isEmpty() }
            assertEquals("0012345678", state.selectedNationalId)
            assertEquals(VALID_CODE, state.educationCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun anotherInquiry_clearsInputs() = runTest(testDispatcher) {
        repository.dependentsResult = singleSon()
        repository.certificateResult = InquiryEducationCertificateDN(message = UNIVERSITY_NAME)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { it.selectedNationalId != null }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged(VALID_CODE))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            awaitUntil { it.step == InquiryEducationStep.Success }
            viewModel.sendIntent(InquiryEducationIntent.AnotherInquiry)
            val state = awaitUntil { it.step == InquiryEducationStep.Form && it.educationCode.isEmpty() }
            assertEquals("0012345678", state.selectedNationalId)
            assertEquals("", state.educationCode)
            assertEquals("", state.universityName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun load_error_staysOnForm() = runTest(testDispatcher) {
        repository.shouldThrowOnDependents = true
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitUntil { !it.isLoading }
            assertEquals(InquiryEducationStep.Form, state.step)
            assertTrue(state.sons.isEmpty())
            assertEquals("", state.failureMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submit_whileSubmitting_doesNotCallCertificateTwice() = runTest(testDispatcher) {
        repository.dependentsResult = singleSon()
        repository.certificateResult = InquiryEducationCertificateDN(message = UNIVERSITY_NAME)
        repository.certificateGate = CompletableDeferred()
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { it.selectedNationalId != null }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged(VALID_CODE))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            awaitUntil { it.isSubmitting }
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            assertEquals(1, repository.certificateCallCount)
            repository.certificateGate?.complete(Unit)
            awaitUntil { it.step == InquiryEducationStep.Success }
            assertEquals(1, repository.certificateCallCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submit_emptySons_selfInquiry_usesSelfCodeAndProfile() = runTest(testDispatcher) {
        repository.dependentsResult = EducationDependentsDN()
        repository.certificateResult = InquiryEducationCertificateDN(message = UNIVERSITY_NAME)
        userRepository.userProfileResult = UserProfileDN(
            entityId = null,
            login = null,
            firstName = "رضا",
            lastName = "احمدی",
            email = null,
            nationalCode = "1122334455",
            mobile = null,
        )
        val viewModel = createViewModel()

        viewModel.uiState.test {
            awaitUntil { !it.isLoading && it.sons.isEmpty() }
            viewModel.sendIntent(InquiryEducationIntent.EducationCodeChanged(VALID_CODE))
            viewModel.sendIntent(InquiryEducationIntent.Submit)
            val state = awaitUntil { it.step == InquiryEducationStep.Success }
            assertEquals("1", repository.lastCode)
            assertEquals(VALID_CODE, repository.lastEducationCode)
            assertEquals("رضا احمدی", state.studentName)
            assertEquals("1122334455", state.studentNationalId)
            assertEquals(UNIVERSITY_NAME, state.universityName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun backToServices_emitsNavigateBack() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(InquiryEducationIntent.BackToServices)
            assertEquals(InquiryEducationEvent.NavigateBack, awaitItem())
        }
    }

    private suspend fun ReceiveTurbine<InquiryEducationUiState>.awaitUntil(
        predicate: (InquiryEducationUiState) -> Boolean,
    ): InquiryEducationUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }

    private companion object {
        const val VALID_CODE = "ABCD123456"
        const val STUDENT_NAME = "امیرحسین محمدی"
        const val UNIVERSITY_NAME = "دانشگاه صنعتی امیرکبیر"
        const val SON_ERROR = "نام فرزند پسر را انتخاب نمایید"
        const val CODE_ERROR = "کد مجوز استعلام معتبر نیست."
        const val FAILURE_EMPTY =
            "متقاضی محترم، اطلاعاتی در مورد اشتغال به تحصیل شما دریافت نگردید. در صورت اطمینان از صحت کد رهگیری وارد شده،برای تعیین تکلیف وضعیت اشتغال به تحصیل به دانشگاه مربوطه مراجعه نمائید."
    }
}

private class FakeUserRepository : UserRepository {
    var userProfileResult: UserProfileDN? = null

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow { }
    override suspend fun getUserProfileImage(): Flow<String> = flowOf("")
    override suspend fun getCurrentUser(): Flow<CurrentUserDN> = flow { }
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> = flow { }
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> = flowOf("")
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> = flow { }
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> =
        flowOf("")
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flow { }
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> =
        flowOf(emptyList())
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> = flowOf(emptyList())
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> =
        flowOf(emptyList())
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> = flowOf(emptyList())
    override suspend fun downloadDocument(url: String): PdfDownloadDN = PdfDownloadDN(pdf = null)
    override suspend fun getUserProfile(): Flow<UserProfileDN> = flow {
        userProfileResult?.let { emit(it) }
    }
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> = flowOf(false)
    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> = flowOf(null)
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flowOf("")
    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flowOf(emptyList())
}

private class FakeInquiryEducationRepository : InquiryEducationRepository {
    var dependentsResult: EducationDependentsDN = EducationDependentsDN()
    var certificateResult: InquiryEducationCertificateDN = InquiryEducationCertificateDN()
    var shouldThrowOnDependents: Boolean = false
    var shouldThrowOnCertificate: Boolean = false
    var certificateError: Throwable = RuntimeException("error")
    var certificateCallCount: Int = 0
    var certificateGate: CompletableDeferred<Unit>? = null
    var lastCode: String? = null
    var lastEducationCode: String? = null

    override fun getDataForEducation(): Flow<EducationDependentsDN> = flow {
        if (shouldThrowOnDependents) throw RuntimeException("load failed")
        emit(dependentsResult)
    }

    override fun inquiryEducationCertificate(
        code: String,
        educationCode: String,
    ): Flow<InquiryEducationCertificateDN> = flow {
        certificateCallCount++
        lastCode = code
        lastEducationCode = educationCode
        if (shouldThrowOnCertificate) throw certificateError
        certificateGate?.await()
        emit(certificateResult)
    }
}

private fun singleSon() = EducationDependentsDN(
    total = 1,
    list = listOf(
        EducationDependentItemDN(
            nationalId = "0012345678",
            fullName = "امیرحسین محمدی",
        ),
    ),
)

private fun multipleSons() = EducationDependentsDN(
    total = 2,
    list = listOf(
        EducationDependentItemDN(nationalId = "0012345678", fullName = "امیرحسین محمدی"),
        EducationDependentItemDN(nationalId = "0087654321", fullName = "علی رضایی"),
    ),
)
