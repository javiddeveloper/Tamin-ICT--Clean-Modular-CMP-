package com.tamin.taminhamrah.feature.workshops.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopSearch
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * کارگاه‌های کارفرما — the list every other screen in the feature is reached from.
 *
 * Its two jobs beyond paging are the ones under test: narrowing the list without losing the
 * narrowing, and handing a row's identity to whichever service the menu picked. A row with only
 * half an identity reaching a downstream screen is the failure that matters — every one of them
 * takes `workshopId/branchCode` as path segments.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /** Built after the answer is staged, because the ViewModel loads as soon as it exists. */
    private fun viewModel() = WorkshopsViewModel(GetEmployerAgreementsUseCase(repository))

    @Test
    fun `the list loads itself without being asked`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 2, total = 2)

        val viewModel = viewModel()

        assertEquals(2, viewModel.uiState.value.list.items.size)
        assertNotNull(repository.lastWorkshopListQuery)
    }

    @Test
    fun `an unfiltered first page also counts how many are active`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 3, total = 7)

        val viewModel = viewModel()
        val stats = assertNotNull(viewModel.uiState.value.stats)

        assertEquals(7, stats.total)
        // The count comes from a second, deliberately tiny request for the ACTIVE slice.
        val last = assertNotNull(repository.lastWorkshopListQuery)
        assertEquals(WorkshopActivityStatus.ACTIVE, last.status)
        assertEquals(1, last.pageSize)
    }

    @Test
    fun `a failed count still leaves the total on screen`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 3, total = 7)
        val viewModel = WorkshopsViewModel(GetEmployerAgreementsUseCase(FailAfterFirstCall(repository)))

        val stats = assertNotNull(viewModel.uiState.value.stats)
        assertEquals(7, stats.total)
        assertEquals(0, stats.active)
    }

    @Test
    fun `searching sends both halves, trimmed, and starts again at page zero`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = WORKSHOP_PAGE_SIZE, total = 30)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopsIntent.LoadMore)
            awaitUntil { it.list.items.size > WORKSHOP_PAGE_SIZE }

            viewModel.sendIntent(WorkshopsIntent.WorkshopIdChanged("  0968210170  "))
            viewModel.sendIntent(WorkshopsIntent.BranchCodeChanged(" 14 "))
            viewModel.sendIntent(WorkshopsIntent.ApplySearch)
            val applied = awaitUntil { it.appliedSearch.isNotEmpty }

            assertEquals(WorkshopSearch("0968210170", "14"), applied.appliedSearch)
            val query = assertNotNull(repository.lastWorkshopListQuery)
            assertEquals("0968210170", query.workshopId)
            assertEquals("14", query.branchCode)
            assertEquals(0, query.page)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searching folds the panel away`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopsIntent.SearchOpenChanged(true))
            awaitUntil { it.isSearchOpen }

            viewModel.sendIntent(WorkshopsIntent.ApplySearch)

            assertFalse(awaitUntil { !it.isSearchOpen }.isSearchOpen)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a status filter survives a later search`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopsIntent.StatusFilterChanged(WorkshopActivityStatus.ACTIVE))
            awaitUntil { it.statusFilter == WorkshopActivityStatus.ACTIVE }

            viewModel.sendIntent(WorkshopsIntent.WorkshopIdChanged("0968210170"))
            viewModel.sendIntent(WorkshopsIntent.ApplySearch)
            val applied = awaitUntil { it.appliedSearch.isNotEmpty }

            assertEquals(WorkshopActivityStatus.ACTIVE, applied.statusFilter)
            assertEquals(WorkshopActivityStatus.ACTIVE, repository.lastWorkshopListQuery?.status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a search survives a later status filter`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopsIntent.WorkshopIdChanged("0968210170"))
            viewModel.sendIntent(WorkshopsIntent.ApplySearch)
            awaitUntil { it.appliedSearch.isNotEmpty }

            viewModel.sendIntent(WorkshopsIntent.StatusFilterChanged(WorkshopActivityStatus.INACTIVE))
            awaitUntil { it.statusFilter == WorkshopActivityStatus.INACTIVE }

            assertEquals("0968210170", repository.lastWorkshopListQuery?.workshopId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `picking a status closes the sheet`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopsIntent.FilterSheetOpenChanged(true))
            awaitUntil { it.isFilterSheetOpen }

            viewModel.sendIntent(WorkshopsIntent.StatusFilterChanged(WorkshopActivityStatus.ACTIVE))

            assertFalse(awaitUntil { !it.isFilterSheetOpen }.isFilterSheetOpen)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing empties the typed fields as well as the applied search`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopsIntent.WorkshopIdChanged("0968210170"))
            viewModel.sendIntent(WorkshopsIntent.ApplySearch)
            awaitUntil { it.appliedSearch.isNotEmpty }

            viewModel.sendIntent(WorkshopsIntent.ClearSearch)
            val cleared = awaitUntil { !it.appliedSearch.isNotEmpty }

            assertEquals("", cleared.workshopIdInput)
            assertEquals("", cleared.branchCodeInput)
            assertNull(repository.lastWorkshopListQuery?.workshopId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `hasActiveFilter covers a status on its own`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()

        assertFalse(viewModel.uiState.value.hasActiveFilter)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopsIntent.StatusFilterChanged(WorkshopActivityStatus.ACTIVE))
            assertTrue(awaitUntil { it.statusFilter != null }.hasActiveFilter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a row with a whole identity opens its detail page`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            val workshop = viewModel.uiState.value.list.items.first()
            assertTrue(workshop.hasIdentity)

            viewModel.sendIntent(WorkshopsIntent.DetailRequested(workshop))
            assertEquals(workshop, awaitUntil { it.detailFor != null }.detailFor)

            viewModel.sendIntent(WorkshopsIntent.DetailDismissed)
            assertNull(awaitUntil { it.detailFor == null }.detailFor)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a row missing half its identity says so instead of opening`() = runTest(testDispatcher) {
        repository.employerAgreements = PagedListDN(
            items = listOf(
                EmployerAgreementDN(
                    workshop = WorkshopSummaryDN(workshopId = "0968210170", branchCode = ""),
                ),
            ),
            total = 1,
        )
        val viewModel = viewModel()

        viewModel.events.test {
            val workshop = viewModel.uiState.value.list.items.first()
            assertFalse(workshop.hasIdentity)

            viewModel.sendIntent(WorkshopsIntent.DetailRequested(workshop))

            assertTrue(awaitItem() is WorkshopsEvent.ShowMessage)
            assertNull(viewModel.uiState.value.detailFor)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `every action navigates, carrying the row's identity`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()
        val workshop = viewModel.uiState.value.list.items.first()

        viewModel.events.test {
            WorkshopAction.entries.forEach { action ->
                viewModel.sendIntent(WorkshopsIntent.ActionSelected(action, workshop))

                val event = awaitItem()
                assertTrue(event is WorkshopsEvent.Navigate, "$action did not navigate")
                assertEquals(action, event.action)
                assertEquals(workshop.workshopId, event.workshopId)
                assertEquals(workshop.branchCode, event.branchCode)
                assertEquals(workshop.name, event.workshopName)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ماده ۱۶ navigates rather than pre-checking for debts`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()
        val workshop = viewModel.uiState.value.list.items.first()

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopsIntent.ActionSelected(WorkshopAction.ARTICLE_SIXTEEN, workshop),
            )

            // It used to answer with a toast when the debt list came back empty, which cost a
            // request per tap and made it the one row that does not open a screen.
            assertTrue(awaitItem() is WorkshopsEvent.Navigate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a refused list keeps the service's own wording`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "دسترسی مجاز نیست")
        val viewModel = viewModel()

        assertEquals("دسترسی مجاز نیست", viewModel.uiState.value.list.error)
        assertFalse(viewModel.uiState.value.list.isLoading)
    }

    private fun agreementsPage(count: Int, total: Int) = PagedListDN(
        items = List(count) {
            EmployerAgreementDN(
                workshop = WorkshopSummaryDN(
                    workshopId = "096821017$it",
                    branchCode = "14",
                    name = "آموزشگاه شماره $it",
                    statusCode = "1",
                ),
            )
        },
        total = total,
    )

    private suspend fun ReceiveTurbine<WorkshopsUiState>.awaitUntil(
        predicate: (WorkshopsUiState) -> Boolean,
    ): WorkshopsUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }
}

/**
 * Answers the first call and refuses every one after it.
 *
 * Stands in for the count request failing on its own: the list is on screen, the ACTIVE slice is
 * not, and the header must still print a total rather than nothing.
 */
private class FailAfterFirstCall(
    private val delegate: FakeWorkShopsRepository,
) : com.tamin.taminhamrah.repository.WorkShopsRepository by delegate {
    private var calls = 0

    override suspend fun getEmployerAgreements(
        query: com.tamin.taminhamrah.model.workshop.WorkshopListQuery,
    ): PagedListDN<EmployerAgreementDN> {
        if (calls++ > 0) throw TaminApiException(title = "شمارش ناموفق")
        return delegate.getEmployerAgreements(query)
    }
}
