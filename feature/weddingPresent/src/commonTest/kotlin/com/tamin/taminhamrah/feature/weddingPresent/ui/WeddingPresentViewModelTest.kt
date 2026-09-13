package com.tamin.taminhamrah.feature.weddingPresent.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentEvent
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentIntent
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import com.tamin.taminhamrah.repository.weddingPresent.WeddingPresentRepository
import com.tamin.taminhamrah.useCases.weddingPresent.GetWeddingPresentInfoUseCase
import com.tamin.taminhamrah.useCases.weddingPresent.SubmitWeddingPresentUseCase
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
import taminx.core.core_ui.error_not_valid_national_id
import taminx.core.core_ui.error_select_check_box
import taminx.core.core_ui.error_updating_infos
import taminx.core.core_ui.message_select_marriage_date
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WeddingPresentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWeddingPresentRepository
    private lateinit var viewModel: WeddingPresentViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWeddingPresentRepository()
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel(): WeddingPresentViewModel = WeddingPresentViewModel(
        getWeddingPresentInfoUseCase = GetWeddingPresentInfoUseCase(repository),
        submitWeddingPresentUseCase = SubmitWeddingPresentUseCase(repository),
    )

    @Test
    fun load_success_setsInfo() = runTest(testDispatcher) {
        assertNotNull(viewModel.uiState.value.info)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun load_failure_thenRetry_loadsInfo() = runTest(testDispatcher) {
        repository.getInfoError = RuntimeException("boom")
        val failingVm = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(failingVm.uiState.value.error)
        assertNull(failingVm.uiState.value.info)

        repository.getInfoError = null
        failingVm.sendIntent(WeddingPresentIntent.Load)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(failingVm.uiState.value.info)
        assertNull(failingVm.uiState.value.error)
    }

    @Test
    fun partnerNationalCode_persianDigits_normalizedToAscii() = runTest(testDispatcher) {
        viewModel.sendIntent(WeddingPresentIntent.PartnerNationalCodeChanged("۰۴۹۹۳۷۰۸۹۹"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("0499370899", viewModel.uiState.value.partnerNationalCode)
        assertNull(viewModel.uiState.value.partnerNationalCodeError)
    }

    @Test
    fun submit_missingMarriageDate_setsFieldError() = runTest(testDispatcher) {
        viewModel.sendIntent(WeddingPresentIntent.PartnerNationalCodeChanged("0499370899"))
        viewModel.sendIntent(WeddingPresentIntent.CommitmentChecked(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(WeddingPresentIntent.Submit)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Res.string.message_select_marriage_date, viewModel.uiState.value.marriageDateError)
        assertNull(repository.lastSubmitRequest)
        assertFalse(viewModel.uiState.value.showSuccessDialog)
    }

    @Test
    fun submit_invalidNationalCode_setsFieldError() = runTest(testDispatcher) {
        viewModel.sendIntent(
            WeddingPresentIntent.MarriageDatePicked(label = "۱۴۰۲/۰۱/۰۱", millis = 1_700_000_000_000L),
        )
        viewModel.sendIntent(WeddingPresentIntent.PartnerNationalCodeChanged("1234567890"))
        viewModel.sendIntent(WeddingPresentIntent.CommitmentChecked(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(WeddingPresentIntent.Submit)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Res.string.error_not_valid_national_id, viewModel.uiState.value.partnerNationalCodeError)
        assertNull(repository.lastSubmitRequest)
    }

    @Test
    fun submit_uncheckedCommitment_showsToast() = runTest(testDispatcher) {
        viewModel.sendIntent(
            WeddingPresentIntent.MarriageDatePicked(label = "۱۴۰۲/۰۱/۰۱", millis = 1_700_000_000_000L),
        )
        viewModel.sendIntent(WeddingPresentIntent.PartnerNationalCodeChanged("0499370899"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(WeddingPresentIntent.Submit)
            val event = awaitItem()
            assertIs<WeddingPresentEvent.ShowToastRes>(event)
            assertEquals(Res.string.error_select_check_box, event.message)
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(repository.lastSubmitRequest)
    }

    @Test
    fun submit_infoNotLoaded_showsLoadErrorNotDateError() = runTest(testDispatcher) {
        repository.getInfoError = RuntimeException("boom")
        val failingVm = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        failingVm.sendIntent(
            WeddingPresentIntent.MarriageDatePicked(label = "۱۴۰۲/۰۱/۰۱", millis = 1_700_000_000_000L),
        )
        failingVm.sendIntent(WeddingPresentIntent.PartnerNationalCodeChanged("0499370899"))
        failingVm.sendIntent(WeddingPresentIntent.CommitmentChecked(true))
        testDispatcher.scheduler.advanceUntilIdle()

        failingVm.sendIntent(WeddingPresentIntent.Submit)
        testDispatcher.scheduler.advanceUntilIdle()

        val error = failingVm.uiState.value.error
        val errorRes = failingVm.uiState.value.errorRes
        assertNull(error)
        assertEquals(Res.string.error_updating_infos, errorRes)
        assertNull(repository.lastSubmitRequest)
    }

    @Test
    fun submit_validForm_showsSuccessDialog() = runTest(testDispatcher) {
        viewModel.sendIntent(
            WeddingPresentIntent.MarriageDatePicked(label = "۱۴۰۲/۰۱/۰۱", millis = 1_700_000_000_000L),
        )
        viewModel.sendIntent(WeddingPresentIntent.PartnerNationalCodeChanged("0499370899"))
        viewModel.sendIntent(WeddingPresentIntent.CommitmentChecked(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(WeddingPresentIntent.Submit)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showSuccessDialog)
        assertEquals("0499370899", repository.lastSubmitRequest?.partnerNationalId)
        assertEquals(1_700_000_000_000L, repository.lastSubmitRequest?.weddingDateTimeStamp)
    }

    @Test
    fun submit_failure_toastsAndKeepsFormWithoutErrorDialog() = runTest(testDispatcher) {
        repository.submitError = RuntimeException("server rejected")
        viewModel.sendIntent(
            WeddingPresentIntent.MarriageDatePicked(label = "۱۴۰۲/۰۱/۰۱", millis = 1_700_000_000_000L),
        )
        viewModel.sendIntent(WeddingPresentIntent.PartnerNationalCodeChanged("0499370899"))
        viewModel.sendIntent(WeddingPresentIntent.CommitmentChecked(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(WeddingPresentIntent.Submit)
            assertIs<WeddingPresentEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorRes)
        assertFalse(viewModel.uiState.value.showSuccessDialog)
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertNotNull(viewModel.uiState.value.info)
    }
}

private class FakeWeddingPresentRepository : WeddingPresentRepository {
    var info: WeddingPresentInfoDN = WeddingPresentInfoDN(
        risuid = "1234567890",
        nationalCode = "0012345678",
        insuranceFirstName = "علی",
        insuranceLastName = "رضایی",
    )
    var getInfoError: Throwable? = null
    var submitError: Throwable? = null
    var lastSubmitRequest: WeddingPresentSubmitRequestDN? = null

    override fun getWeddingPresentInfo(): Flow<WeddingPresentInfoDN> = flow {
        getInfoError?.let { throw it }
        emit(info)
    }

    override fun submitWeddingPresent(request: WeddingPresentSubmitRequestDN): Flow<Unit> = flow {
        lastSubmitRequest = request
        submitError?.let { throw it }
        emit(Unit)
    }

    override fun calculateMarriageAllowance(timeStamp: String): Flow<List<String>> =
        flowOf(listOf("1000", "2000"))
}
