package com.tamin.taminhamrah.feature.inquiryEducation.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationEvent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationIntent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationStep
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN
import com.tamin.taminhamrah.repository.inquiryEducation.InquiryEducationRepository
import com.tamin.taminhamrah.useCases.inquiryEducation.GetDataForEducationUseCase
import com.tamin.taminhamrah.useCases.inquiryEducation.InquiryEducationCertificateUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeInquiryEducationRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel(): InquiryEducationViewModel = InquiryEducationViewModel(
        getDataForEducationUseCase = GetDataForEducationUseCase(repository),
        inquiryEducationCertificateUseCase = InquiryEducationCertificateUseCase(repository),
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
