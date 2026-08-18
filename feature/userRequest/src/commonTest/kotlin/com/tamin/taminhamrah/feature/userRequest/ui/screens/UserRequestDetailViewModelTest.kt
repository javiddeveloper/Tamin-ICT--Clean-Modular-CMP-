package com.tamin.taminhamrah.feature.userRequest.ui.screens

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailIntent
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDocumentDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.useCases.userRequest.DownloadUserRequestDocumentUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetShowRequestInfoUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserRequestDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeUserRequestRepository
    private lateinit var viewModel: UserRequestDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeUserRequestRepository()
        viewModel = UserRequestDetailViewModel(
            getShowRequestInfoUseCase = GetShowRequestInfoUseCase(repository),
            downloadUserRequestDocumentUseCase = DownloadUserRequestDocumentUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `load detail calls show request info with list reference id not request header`() = runTest(testDispatcher) {
        repository.showRequestInfoResult = UserRequestDetailsDN(
            deferredInstallment = DeferredInstallmentDetailDN(
                borrowerName = "علی محمدی",
                pensionerFirstName = "کاربر",
                pensionerLastName = "کاربری",
            )
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                UserRequestDetailIntent.LoadDetail(
                    requestId = 491371155L,
                    refCode = "1075558440",
                    requestTypeId = UserRequestTypeIds.DEFERRED_INSTALLMENT,
                    title = "گواهی کسر اقساط معوق",
                    referenceId = "491371155",
                )
            )
            var state = awaitItem()
            while (state.request == null) state = awaitItem()

            assertFalse(state.isLoading)
            assertNotNull(state.request)
            assertEquals("علی محمدی", state.request?.details?.deferredInstallment?.borrowerName)
            assertEquals("491371155", repository.lastReferenceId)
            assertNull(repository.lastRequestId)
        }
    }

    @Test
    fun `load detail falls back to request id when reference id is blank`() = runTest(testDispatcher) {
        repository.showRequestInfoResult = UserRequestDetailsDN()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                UserRequestDetailIntent.LoadDetail(
                    requestId = 491371155L,
                    refCode = "1075558440",
                    requestTypeId = UserRequestTypeIds.DEFERRED_INSTALLMENT,
                    title = "گواهی کسر اقساط معوق",
                    referenceId = "",
                )
            )
            var state = awaitItem()
            while (state.request == null) state = awaitItem()

            assertEquals("491371155", repository.lastReferenceId)
            assertNull(repository.lastRequestId)
        }
    }

    @Test
    fun `load detail surfaces error state when show request info fails`() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        repository.error = RuntimeException("network down")

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                UserRequestDetailIntent.LoadDetail(
                    requestId = 491371155L,
                    refCode = "1075558440",
                    requestTypeId = UserRequestTypeIds.DEFERRED_INSTALLMENT,
                    title = "گواهی کسر اقساط معوق",
                    referenceId = "491371155",
                )
            )
            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertFalse(state.isLoading)
            assertNull(state.request)
            assertEquals("network down", state.error)
        }
    }

    @Test
    fun `download document exposes preview with downloaded image data`() = runTest(testDispatcher) {
        repository.showRequestInfoResult = UserRequestDetailsDN(
            documents = listOf(UserRequestDocumentDN(guid = "doc-guid-1", documentType = "گواهی")),
        )
        repository.downloadResult = "BASE64DATA"

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                UserRequestDetailIntent.LoadDetail(
                    requestId = 1L,
                    refCode = "1075558440",
                    requestTypeId = UserRequestTypeIds.ILL_DAY,
                    title = "غرامت دستمزد ایام بیماری",
                    referenceId = "1",
                )
            )
            var state = awaitItem()
            while (state.request == null) state = awaitItem()

            viewModel.sendIntent(
                UserRequestDetailIntent.DownloadDocument(guid = "doc-guid-1", title = "گواهی")
            )
            state = awaitItem()
            while (state.documentPreview == null) state = awaitItem()

            assertEquals("doc-guid-1", repository.lastDownloadedGuid)
            assertEquals("BASE64DATA", state.documentPreview?.imageData)
            assertEquals("گواهی", state.documentPreview?.title)
            assertNull(state.downloadingDocumentGuid)

            viewModel.sendIntent(UserRequestDetailIntent.DismissDocumentPreview)
            state = awaitItem()
            while (state.documentPreview != null) state = awaitItem()
            assertNull(state.documentPreview)
        }
    }

    @Test
    fun `download document reports blank payload without opening preview`() = runTest(testDispatcher) {
        repository.showRequestInfoResult = UserRequestDetailsDN(
            documents = listOf(UserRequestDocumentDN(guid = "doc-guid-2")),
        )
        repository.downloadResult = ""

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                UserRequestDetailIntent.LoadDetail(
                    requestId = 1L,
                    refCode = "1075558440",
                    requestTypeId = UserRequestTypeIds.ILL_DAY,
                    title = "غرامت دستمزد ایام بیماری",
                    referenceId = "1",
                )
            )
            var state = awaitItem()
            while (state.request == null) state = awaitItem()

            viewModel.sendIntent(
                UserRequestDetailIntent.DownloadDocument(guid = "doc-guid-2", title = "مدرک")
            )

            // Don't rely on intermediate emissions timing; just let coroutines settle.
            advanceUntilIdle()
            val finalState = viewModel.uiState.value

            assertEquals("doc-guid-2", repository.lastDownloadedGuid)
            assertNull(finalState.documentPreview)
            assertNull(finalState.downloadingDocumentGuid)
            assertTrue(finalState.request != null)
        }
    }
}
