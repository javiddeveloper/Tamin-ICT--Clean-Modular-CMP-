package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.workshopMembers.PersonSearch
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopStackHoldersUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

    @Test
    fun `each search field lands in the column that names it`() = runTest(testDispatcher) {
        repository.stackHolders = holdersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }

            viewModel.sendIntent(
                WorkshopStackholdersIntent.DraftChanged(
                    PersonSearch(nationalId = "0024567891", insuranceNumber = "1122334455"),
                ),
            )
            viewModel.sendIntent(WorkshopStackholdersIntent.ApplySearch)
            awaitUntil { it.applied.isNotEmpty }

            val query = assertNotNull(repository.lastStackHolderQuery)
            assertEquals("0024567891", query.nationalId)
            assertEquals("1122334455", query.insuranceNumber)
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
    fun `clearing the search drops both filters`() = runTest(testDispatcher) {
        repository.stackHolders = holdersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopStackholdersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            viewModel.sendIntent(
                WorkshopStackholdersIntent.DraftChanged(
                    PersonSearch(nationalId = "0024567891", insuranceNumber = "1122334455"),
                ),
            )
            viewModel.sendIntent(WorkshopStackholdersIntent.ApplySearch)
            awaitUntil { it.applied.isNotEmpty }

            viewModel.sendIntent(WorkshopStackholdersIntent.ClearSearch)
            awaitUntil { !it.applied.isNotEmpty }

            assertNull(repository.lastStackHolderQuery?.nationalId)
            assertNull(repository.lastStackHolderQuery?.insuranceNumber)
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

    private fun holdersPage(count: Int, total: Int) = PagedListDN(
        items = List(count) {
            WorkshopStackHolderDN(
                stackId = it,
                nationalId = "002456789$it",
                firstName = "ذینفع",
                lastName = "شماره $it",
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
