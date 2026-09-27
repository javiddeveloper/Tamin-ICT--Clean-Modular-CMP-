package com.tamin.taminhamrah.feature.userRequest.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.userRequest.ui.contract.RequestStatusTab
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsEvent
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestsIntent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.FakeUserRequestRepository
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.userRequest.GetSmartGuideListUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestErrorsUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestTypesUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestsPageUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserRequestsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeUserRequestRepository
    private lateinit var viewModel: UserRequestsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeUserRequestRepository()
        viewModel = UserRequestsViewModel(
            getUserRequestsPageUseCase = GetUserRequestsPageUseCase(repository),
            getUserRequestTypesUseCase = GetUserRequestTypesUseCase(repository),
            getUserRequestErrorsUseCase = GetUserRequestErrorsUseCase(repository),
            getSmartGuideListUseCase = GetSmartGuideListUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `open errors with empty result shows info dialog and keeps sheet closed`() = runTest(testDispatcher) {
        repository.requestErrorsResult = emptyList()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(UserRequestsIntent.OpenErrors(requestId = 42L, title = "خطاها"))

            var state = awaitItem()
            while (state.infoDialogMessage == null) state = awaitItem()

            assertFalse(state.isErrorsOpen)
            assertFalse(state.isLoadingErrors)
            assertNotNull(state.infoDialogMessage)
            assertEquals(42L, repository.lastErrorsRequestId)
        }
    }

    @Test
    fun `open errors with results opens the errors sheet`() = runTest(testDispatcher) {
        repository.requestErrorsResult = listOf(
            RequestErrorDN(
                id = 1L,
                errorMessage = "مدرک ناقص است",
                errorType = null,
                errorStatus = null,
                creationTime = null,
            )
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(UserRequestsIntent.OpenErrors(requestId = 7L, title = "خطاها"))

            var state = awaitItem()
            while (!state.isErrorsOpen) state = awaitItem()

            assertTrue(state.isErrorsOpen)
            assertFalse(state.isLoadingErrors)
            assertEquals(1, state.errorItems.size)
            assertEquals("خطاها", state.errorTitle)
            assertNull(state.infoDialogMessage)
        }
    }

    @Test
    fun `copy tracking code emits show toast event`() = runTest(testDispatcher) {
        viewModel.events.test {
            viewModel.sendIntent(UserRequestsIntent.CopyTrackingCode(code = "TRACK-1"))

            val event = assertIs<UserRequestsEvent.ShowToast>(awaitItem())
            assertTrue(event.message.isNotBlank())
        }
    }

    @Test
    fun `view details emits navigate to detail with request identifiers`() = runTest(testDispatcher) {
        val request = sampleRequest()

        viewModel.events.test {
            viewModel.sendIntent(UserRequestsIntent.ViewDetails(request))

            val event = assertIs<UserRequestsEvent.NavigateToDetail>(awaitItem())
            assertEquals(request.id, event.requestId)
            assertEquals(request.refCode, event.refCode)
            assertEquals(request.requestTypeId, event.requestTypeId)
            assertEquals(request.title, event.title)
            assertEquals(request.referenceId, event.referenceId)
        }
    }

    @Test
    fun `select tab updates the selected tab`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(UserRequestsIntent.SelectTab(RequestStatusTab.COMPLETED))

            var state = awaitItem()
            while (state.selectedTab != RequestStatusTab.COMPLETED) state = awaitItem()

            assertEquals(RequestStatusTab.COMPLETED, state.selectedTab)
        }
    }




    @Test
    fun `InitFilters seeds refCode and requestTypeId, opens the filter panel, and searches with both`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(UserRequestsIntent.InitFilters(refCode = "REF-1", requestTypeId = "42"))

            var state = awaitItem()
            while (!state.isFilterOpen) state = awaitItem()

            assertEquals("REF-1", state.refCode)
            assertEquals("42", state.selectedRequestTypeId)
            assertTrue(state.isFilterOpen)
            assertEquals("REF-1", repository.lastSearch?.refCode)
            assertEquals("42", repository.lastSearch?.requestTypeId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `InitFilters with a blank refCode and requestTypeId does not open the filter panel`() = runTest(testDispatcher) {
        repository.userRequestsResult = listOf(
            UserRequestDN(
                id = 1L,
                refCode = "1075558440",
                title = "تست",
                comment = null,
                creationTime = null,
                createByName = null,
                status = null,
                requestType = null,
                referenceId = null,
            )
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(UserRequestsIntent.InitFilters(refCode = null, requestTypeId = ""))

            // Blank/null filters are treated as absent: the search still runs (unfiltered), but
            // nothing seeds refCode/selectedRequestTypeId or opens the filter panel.
            var state = awaitItem()
            while (state.isLoading) state = awaitItem()

            assertFalse(state.isFilterOpen)
            assertEquals("", state.refCode)
            assertEquals(null, state.selectedRequestTypeId)
            assertEquals(null, repository.lastSearch?.refCode)
            assertEquals(null, repository.lastSearch?.requestTypeId)
        }
    }

    @Test
    fun `load requests failure surfaces error state`() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        repository.error = RuntimeException("boom")

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(UserRequestsIntent.LoadRequests)

            var state = awaitItem()
            while (state.paginationError == null) state = awaitItem()

            assertFalse(state.isLoading)
            assertEquals(RuntimeException("boom").toSingleLineMessage(), state.paginationError)
        }
    }

    @Test
    fun `requests are paged - first page, then the next page appends until total`() = runTest(testDispatcher) {
        repository.userRequestsResult = (1L..15L).map { sampleDomain(it) }
        repository.userRequestsTotal = 15

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(UserRequestsIntent.LoadRequests)
            var state = awaitItem()
            while (state.requests.size != 10) state = awaitItem()
            assertFalse(state.endReached)

            viewModel.sendIntent(UserRequestsIntent.LoadNextPage)
            while (state.requests.size != 15) state = awaitItem()

            assertTrue(state.endReached)
            assertEquals(10, repository.lastPage?.start)
        }
    }

    private fun sampleDomain(id: Long) = UserRequestDN(
        id = id,
        refCode = id.toString(),
        title = "تست",
        comment = null,
        creationTime = null,
        createByName = null,
        status = null,
        requestType = null,
        referenceId = null,
    )

    @Test
    fun `open smart guide failure emits show toast event`() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        repository.error = RuntimeException("boom")

        viewModel.events.test {
            viewModel.sendIntent(UserRequestsIntent.OpenSmartGuide(requestType = 1, requestStatus = "0018", title = "راهنما"))

            val event = assertIs<UserRequestsEvent.ShowToast>(awaitItem())
            assertEquals(RuntimeException("boom").toSingleLineMessage(), event.message)
        }
    }

    @Test
    fun `repeated searches do not starve later intents`() = runTest(testDispatcher) {
        // Each search's list flow stays open like the real Room-backed one; before the fix these
        // filled flatMapMerge's 16 slots and the guide tap below was never handled.
        repository.keepRequestsFlowOpen = true
        repeat(20) { viewModel.sendIntent(UserRequestsIntent.SearchRequests) }
        repository.shouldThrowError = true
        repository.error = RuntimeException("boom")

        viewModel.events.test {
            viewModel.sendIntent(UserRequestsIntent.OpenSmartGuide(requestType = 1, requestStatus = "0018", title = "راهنما"))

            val event = assertIs<UserRequestsEvent.ShowToast>(awaitItem())
            assertEquals(RuntimeException("boom").toSingleLineMessage(), event.message)
        }
    }

    private fun sampleRequest(): UserRequestPR = UserRequestPR(
        id = 491371155L,
        refCode = "1075558440",
        title = "گواهی کسر اقساط معوق",
        comment = "",
        creationTime = "۱۴۰۴/۱۲/۱۸",
        createByName = "کاربر نمونه",
        statusDesc = "در حال بررسی",
        statusCode = "0001",
        requestTypeId = 100L,
        requestTypeTitle = "گواهی کسر اقساط معوق",
        referenceId = "491371155",
    )
}
