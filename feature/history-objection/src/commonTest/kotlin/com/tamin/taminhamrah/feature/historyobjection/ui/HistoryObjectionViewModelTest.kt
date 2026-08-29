package com.tamin.taminhamrah.feature.historyobjection.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.historyobjection.fake.FakeCommonRepository
import com.tamin.taminhamrah.feature.historyobjection.fake.FakeHistoryObjectionRepository
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.useCases.common.CheckUserTypeUseCase
import com.tamin.taminhamrah.useCases.historyObjection.CheckHistoryObjectionStatusNotExistUseCase
import com.tamin.taminhamrah.useCases.historyObjection.ConfirmHistoryObjectionNotExistUseCase
import com.tamin.taminhamrah.useCases.historyObjection.DeleteHistoryObjectionNotExistRequestUseCase
import com.tamin.taminhamrah.useCases.historyObjection.FinalConfirmHistoryObjectionNotExistUseCase
import com.tamin.taminhamrah.useCases.historyObjection.GetHistoryObjectionNotExistRequestsUseCase
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryObjectionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeHistoryObjectionRepository
    private lateinit var commonRepository: FakeCommonRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeHistoryObjectionRepository()
        commonRepository = FakeCommonRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel(): HistoryObjectionViewModel = HistoryObjectionViewModel(
        checkUserTypeUseCase = CheckUserTypeUseCase(commonRepository),
        checkHistoryObjectionStatusNotExistUseCase = CheckHistoryObjectionStatusNotExistUseCase(repository),
        getHistoryObjectionNotExistRequestsUseCase = GetHistoryObjectionNotExistRequestsUseCase(repository),
        deleteHistoryObjectionNotExistRequestUseCase = DeleteHistoryObjectionNotExistRequestUseCase(repository),
        confirmHistoryObjectionNotExistUseCase = ConfirmHistoryObjectionNotExistUseCase(repository),
        finalConfirmHistoryObjectionNotExistUseCase = FinalConfirmHistoryObjectionNotExistUseCase(repository),
    )

    @Test
    fun deleteConfirmed_withValidData_deletesAndReloadsList() = runTest(testDispatcher) {
        val existing = NotExistRequestDN(requestNumber = "1837708", rowIndex = "1", branchName = "پاکدشت")
        repository.notExistRequestsResult = listOf(existing)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(HistoryObjectionIntent.Load)
            val loaded = awaitUntil { !it.isLoading && it.notExistRequests.isNotEmpty() }
            assertEquals(1, loaded.notExistRequests.size)

            repository.notExistRequestsResult = emptyList()
            viewModel.sendIntent(HistoryObjectionIntent.OnDeleteConfirmed("1837708", "1"))

            val afterDelete = awaitUntil { !it.isLoading && it.notExistRequests.isEmpty() }
            assertEquals("1837708", repository.lastDeleteRequestNumber)
            assertEquals("1", repository.lastDeleteRowIndex)
            assertTrue(afterDelete.notExistRequests.isEmpty())
            assertEquals(false, afterDelete.isDeleting)
            assertNull(afterDelete.deleteConfirmationRequestNumber)
        }
    }

    @Test
    fun deleteConfirmed_withoutRowIndex_showsErrorAndDoesNotCallUseCase() = runTest(testDispatcher) {
        val existing = NotExistRequestDN(requestNumber = "1837708", rowIndex = null, branchName = "پاکدشت")
        repository.notExistRequestsResult = listOf(existing)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(HistoryObjectionIntent.Load)
            awaitUntil { !it.isLoading && it.notExistRequests.isNotEmpty() }

            viewModel.sendIntent(HistoryObjectionIntent.OnDeleteConfirmed("1837708", null))
            val errored = awaitUntil { it.error != null }

            assertNull(repository.lastDeleteRequestNumber)
            assertEquals(1, errored.notExistRequests.size)
        }
    }

    @Test
    fun deleteConfirmed_whenDeleteFails_setsErrorAndKeepsListUntouched() = runTest(testDispatcher) {
        val existing = NotExistRequestDN(requestNumber = "1837708", rowIndex = "1", branchName = "پاکدشت")
        repository.notExistRequestsResult = listOf(existing)
        repository.shouldThrowOnDelete = true
        repository.deleteError = RuntimeException("network down")
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(HistoryObjectionIntent.Load)
            awaitUntil { !it.isLoading && it.notExistRequests.isNotEmpty() }

            viewModel.sendIntent(HistoryObjectionIntent.OnDeleteConfirmed("1837708", "1"))
            val errored = awaitUntil { it.error != null }

            assertEquals(false, errored.isDeleting)
            assertEquals(1, errored.notExistRequests.size)
        }
    }

    @Test
    fun submitConfirmed_whenConfirmReturnsTrue_callsFinalConfirmAndShowsTrackingNumber() = runTest(testDispatcher) {
        repository.confirmNotExistResult = true
        repository.finalConfirmResult = "61520081914"
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(HistoryObjectionIntent.OnDescriptionChanged("توضیح تست"))
            awaitUntil { it.description == "توضیح تست" }

            viewModel.sendIntent(HistoryObjectionIntent.OnSubmitClicked)
            awaitUntil { it.showSubmitConfirmationDialog }

            viewModel.sendIntent(HistoryObjectionIntent.OnSubmitConfirmed)
            val succeeded = awaitUntil { it.trackingNumber != null }

            assertEquals("توضیح تست", repository.lastConfirmDescription)
            assertEquals("61520081914", succeeded.trackingNumber)
            assertEquals(false, succeeded.showSubmitConfirmationDialog)
            assertEquals(false, succeeded.isSubmitting)
        }
    }

    @Test
    fun submitConfirmed_whenConfirmReturnsFalse_showsErrorAndDoesNotCallFinalConfirm() = runTest(testDispatcher) {
        repository.confirmNotExistResult = false
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(HistoryObjectionIntent.OnSubmitClicked)
            awaitUntil { it.showSubmitConfirmationDialog }

            viewModel.sendIntent(HistoryObjectionIntent.OnSubmitConfirmed)
            val errored = awaitUntil { it.error != null }

            assertNull(errored.trackingNumber)
            assertEquals(false, errored.isSubmitting)
        }
    }

    @Test
    fun load_withAnonymousUser_showsAccessDeniedAndSkipsListLoad() = runTest(testDispatcher) {
        commonRepository.userTypeResult = UserTypeInfoDN(userType = UserType.ANONYMOUS)
        repository.notExistRequestsResult = listOf(
            NotExistRequestDN(requestNumber = "1837708", rowIndex = "1", branchName = "پاکدشت")
        )
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(HistoryObjectionIntent.Load)
            val denied = awaitUntil { it.accessDeniedReason != null }

            assertEquals(HistoryObjectionUiState.AccessDeniedReason.Anonymous, denied.accessDeniedReason)
            assertTrue(denied.notExistRequests.isEmpty())
        }
    }

    @Test
    fun load_withPensionerUser_showsAccessDeniedWithServerMessage() = runTest(testDispatcher) {
        commonRepository.userTypeResult = UserTypeInfoDN(userType = UserType.PENSIONER, message = "پیام سرور")
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(HistoryObjectionIntent.Load)
            val denied = awaitUntil { it.accessDeniedReason != null }

            assertEquals(
                HistoryObjectionUiState.AccessDeniedReason.Pensioner("پیام سرور"),
                denied.accessDeniedReason,
            )
        }
    }

    @Test
    fun load_withInsuredUser_proceedsToLoadList() = runTest(testDispatcher) {
        commonRepository.userTypeResult = UserTypeInfoDN(userType = UserType.INSURED)
        val existing = NotExistRequestDN(requestNumber = "1837708", rowIndex = "1", branchName = "پاکدشت")
        repository.notExistRequestsResult = listOf(existing)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(HistoryObjectionIntent.Load)
            val loaded = awaitUntil { !it.isLoading && it.notExistRequests.isNotEmpty() }

            assertNull(loaded.accessDeniedReason)
            assertEquals(1, loaded.notExistRequests.size)
        }
    }

    private suspend fun ReceiveTurbine<HistoryObjectionUiState>.awaitUntil(
        predicate: (HistoryObjectionUiState) -> Boolean,
    ): HistoryObjectionUiState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}
