package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.model.PersonSearch
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import kotlin.test.assertFalse
import kotlinx.coroutines.CompletableDeferred
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopStackHoldersUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * ذینفعان.
 *
 * The one rule worth pinning down here is which column each search field lands in: the old
 * stakeholder screen crossed the national code and the insurance number over, so a search always
 * came back empty and looked like "no results" rather than like a bug.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopStackholdersViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository
    private lateinit var viewModel: WorkshopStackholdersViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
        viewModel = WorkshopStackholdersViewModel(GetWorkshopStackHoldersUseCase(repository))
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `opening loads the first page for that workshop`() = runTest(testDispatcher) {
        repository.stackHolders = holdersPage(count = 4, total = 4)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            val loaded = awaitUntil { it.list.items.isNotEmpty() }

            assertEquals(4, loaded.list.items.size)
            val query = assertNotNull(repository.lastStackHolderQuery)
            assertEquals(WORKSHOP_ID, query.workshopId)
            assertEquals(BRANCH_CODE, query.branchCode)
            assertEquals(0, query.page)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /** The one search this list has: a stakeholder row carries no insurance number to filter by. */
    @Test
    fun `a national code search is sent as the national code filter`() = runTest(testDispatcher) {
        repository.stackHolders = holdersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }

            viewModel.sendIntent(
                WorkshopStackholdersIntent.DraftChanged(PersonSearch(nationalId = "0024567891")),
            )
            viewModel.sendIntent(WorkshopStackholdersIntent.ApplySearch)
            awaitUntil { it.applied.isNotEmpty }

            val query = assertNotNull(repository.lastStackHolderQuery)
            assertEquals("0024567891", query.nationalId)
            assertEquals(WORKSHOP_ID, query.workshopId)
            assertEquals(BRANCH_CODE, query.branchCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applying a search folds the panel away and restarts at page zero`() = runTest(testDispatcher) {
        repository.stackHolders = holdersPage(count = WORKSHOP_PAGE_SIZE, total = 30)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            repository.stackHolders = holdersPage(count = WORKSHOP_PAGE_SIZE, total = 30, from = WORKSHOP_PAGE_SIZE)
            viewModel.sendIntent(WorkshopStackholdersIntent.LoadMore)
            awaitUntil { it.list.items.size > WORKSHOP_PAGE_SIZE }
            viewModel.sendIntent(WorkshopStackholdersIntent.SearchOpenChanged(true))
            awaitUntil { it.isSearchOpen }

            viewModel.sendIntent(
                WorkshopStackholdersIntent.DraftChanged(PersonSearch(nationalId = "0024567891")),
            )
            viewModel.sendIntent(WorkshopStackholdersIntent.ApplySearch)
            val applied = awaitUntil { it.applied.isNotEmpty && !it.isSearchOpen }

            assertEquals(0, repository.lastStackHolderQuery?.page)
            assertEquals(WORKSHOP_PAGE_SIZE, applied.list.items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing the search drops the filter`() = runTest(testDispatcher) {
        repository.stackHolders = holdersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            viewModel.sendIntent(
                WorkshopStackholdersIntent.DraftChanged(PersonSearch(nationalId = "0024567891")),
            )
            viewModel.sendIntent(WorkshopStackholdersIntent.ApplySearch)
            awaitUntil { it.applied.isNotEmpty }

            viewModel.sendIntent(WorkshopStackholdersIntent.ClearSearch)
            awaitUntil { !it.applied.isNotEmpty }

            assertNull(repository.lastStackHolderQuery?.nationalId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an identity that never arrived fails without asking the service`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(workshopId = "", branchCode = ""))

            assertNull(repository.lastStackHolderQuery)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a refused request becomes the list's error`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "دسترسی مجاز نیست")

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            val failed = awaitUntil { it.list.error != null }

            assertEquals("دسترسی مجاز نیست", failed.list.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a page answered after a new search was applied is dropped`() = runTest(testDispatcher) {
        repository.stackHolderPages = mapOf(
            0 to holdersPage(count = WORKSHOP_PAGE_SIZE, total = 30),
            1 to holdersPage(count = WORKSHOP_PAGE_SIZE, total = 30, from = WORKSHOP_PAGE_SIZE),
        )
        val heldPageOne = CompletableDeferred<Unit>()
        repository.heldStackHolderPages[1] = heldPageOne

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.size == WORKSHOP_PAGE_SIZE }

            // Scrolling to the end, then searching before that page comes back.
            viewModel.sendIntent(WorkshopStackholdersIntent.LoadMore)
            // Page 1 keeps its own answer: the held request reads this map only once it resumes,
            // and a page that came back empty would append nothing whether it was dropped or not.
            repository.stackHolderPages = mapOf(
                0 to holdersPage(count = 1, total = 1, from = 99),
                1 to holdersPage(count = WORKSHOP_PAGE_SIZE, total = 30, from = WORKSHOP_PAGE_SIZE),
            )
            viewModel.sendIntent(
                WorkshopStackholdersIntent.DraftChanged(PersonSearch(nationalId = "0024567899")),
            )
            viewModel.sendIntent(WorkshopStackholdersIntent.ApplySearch)
            val searched = awaitUntil { it.list.items.size == 1 }
            assertEquals(1, searched.list.total)

            heldPageOne.complete(Unit)
            advanceUntilIdle()

            // The old page has landed by now, and must have changed nothing about the search's list.
            val settled = viewModel.uiState.value
            assertEquals(1, settled.list.items.size)
            assertEquals(1, settled.list.receivedCount)
            assertFalse(settled.list.isLoadingMore)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a page that fails after a new search was applied does not fail the new list`() =
        runTest(testDispatcher) {
            repository.stackHolders = holdersPage(count = WORKSHOP_PAGE_SIZE, total = 30)
            val heldPageOne = CompletableDeferred<Unit>()
            repository.heldStackHolderPages[1] = heldPageOne

            viewModel.uiState.test {
                awaitItem()
                viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
                awaitUntil { it.list.items.size == WORKSHOP_PAGE_SIZE }

                viewModel.sendIntent(WorkshopStackholdersIntent.LoadMore)
                repository.stackHolders = holdersPage(count = 1, total = 1, from = 99)
                viewModel.sendIntent(
                    WorkshopStackholdersIntent.DraftChanged(PersonSearch(nationalId = "0024567899")),
                )
                viewModel.sendIntent(WorkshopStackholdersIntent.ApplySearch)
                awaitUntil { it.list.items.size == 1 }

                // The abandoned page-1 request answers with a failure.
                repository.error = TaminApiException(title = "خطا")
                heldPageOne.complete(Unit)
                advanceUntilIdle()

                val settled = viewModel.uiState.value
                assertNull(settled.list.error)
                assertEquals(1, settled.list.items.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    /** Removing the national-code chip is the chip's own way back to the whole list. */
    @Test
    fun `removing the national code chip reloads without the filter`() = runTest(testDispatcher) {
        repository.stackHolders = holdersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            viewModel.sendIntent(
                WorkshopStackholdersIntent.DraftChanged(PersonSearch(nationalId = "0024567891")),
            )
            viewModel.sendIntent(WorkshopStackholdersIntent.ApplySearch)
            awaitUntil { it.applied.isNotEmpty }

            viewModel.sendIntent(WorkshopStackholdersIntent.ReplaceSearch(PersonSearch(nationalId = "")))
            val afterRemoval = awaitUntil { it.applied.nationalId.isBlank() }

            assertNull(repository.lastStackHolderQuery?.nationalId)
            assertEquals(0, repository.lastStackHolderQuery?.page)
            assertFalse(afterRemoval.applied.isNotEmpty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun holdersPage(count: Int, total: Int, from: Int = 0) = PagedListDN(
        items = List(count) {
            val i = from + it
            WorkshopStackHolderDN(
                stackId = i,
                nationalId = "002456789$i",
                firstName = "ذینفع",
                lastName = "شماره $i",
            )
        },
        total = total,
    )

    private suspend fun ReceiveTurbine<WorkshopStackholdersUiState>.awaitUntil(
        predicate: (WorkshopStackholdersUiState) -> Boolean,
    ): WorkshopStackholdersUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }

    private companion object {
        const val WORKSHOP_ID = "0968210170"
        const val BRANCH_CODE = "14"
    }
}
