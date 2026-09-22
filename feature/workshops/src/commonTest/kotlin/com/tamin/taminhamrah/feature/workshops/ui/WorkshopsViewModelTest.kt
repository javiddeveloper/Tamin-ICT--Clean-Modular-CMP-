package com.tamin.taminhamrah.feature.workshops.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeFeatureManager
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopSearch
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopStats
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenDebtsUseCase
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
    private lateinit var featureManager: FakeFeatureManager

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
        featureManager = FakeFeatureManager()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /** Built after the answer is staged, because the ViewModel loads as soon as it exists. */
    private fun viewModel() = WorkshopsViewModel(
        GetEmployerAgreementsUseCase(repository),
        featureManager,
        GetArticleSixteenDebtsUseCase(repository),
    )

    @Test
    fun `the list loads itself without being asked`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 2, total = 2)

        val viewModel = viewModel()

        assertEquals(2, viewModel.uiState.value.list.items.size)
        assertNotNull(repository.lastWorkshopListQuery)
    }

    @Test
    fun `an unfiltered list that fits one page is counted without another request`() =
        runTest(testDispatcher) {
            repository.employerAgreements = PagedListDN(
                items = listOf(agreement(0, ACTIVE_CODE), agreement(1, ACTIVE_CODE), agreement(2, SEMI_ACTIVE_CODE)),
                total = 3,
            )

            val viewModel = viewModel()
            val stats = assertNotNull(viewModel.uiState.value.stats)

            assertEquals(WorkshopStats(total = 3, active = 2), stats)
            assertEquals(1, stats.inactive)
            // The page the list asked for was the last one made: nothing was fetched to count.
            val last = assertNotNull(repository.lastWorkshopListQuery)
            assertNull(last.status)
            assertEquals(0, last.page)
        }

    /**
     * The service answers one row per agreement, so a workshop under nine agreements arrives nine
     * times. The list shows it once; the strip above it has to agree — it used to read ۹ over ۱.
     */
    @Test
    fun `a workshop repeated across agreements is counted once, as the list shows it`() =
        runTest(testDispatcher) {
            repository.employerAgreements = PagedListDN(
                items = List(9) { agreement(0, ACTIVE_CODE) },
                total = 9,
            )

            val state = viewModel().uiState.value

            assertEquals(1, state.list.items.size)
            assertEquals(WorkshopStats(total = 1, active = 1), state.stats)
        }

    /** A count of the first page alone would stop at ten; the rest of the pages count too. */
    @Test
    fun `the count covers every page, repeats across pages included`() = runTest(testDispatcher) {
        val firstPage = PagedListDN(items = List(WORKSHOP_PAGE_SIZE) { agreement(it, ACTIVE_CODE) }, total = 15)
        // Two of the second page's rows repeat workshops from the first.
        val secondPage = PagedListDN(
            items = listOf(agreement(0, ACTIVE_CODE), agreement(1, ACTIVE_CODE)) +
                List(3) { agreement(WORKSHOP_PAGE_SIZE + it, INACTIVE_CODE) },
            total = 15,
        )
        val pages = PagedAgreements(repository, listOf(firstPage, secondPage))

        val viewModel = WorkshopsViewModel(
            GetEmployerAgreementsUseCase(pages),
            featureManager,
            GetArticleSixteenDebtsUseCase(repository),
        )

        assertEquals(WorkshopStats(total = 13, active = 10), viewModel.uiState.value.stats)
        assertEquals(listOf(0, 1), pages.requestedPages)
        // Counting does not page the list itself in: it still holds only what was asked for.
        assertEquals(WORKSHOP_PAGE_SIZE, viewModel.uiState.value.list.items.size)
    }

    // ------------------------------------------------- feature flags and identity

    /**
     * A service the server switched off must disappear from here too.
     *
     * ردیف‌های پیمان is reachable two ways — the services grid, which routes through
     * `FeatureFlag.CONTRACT_INFO`, and this menu. Honoring the flag in one place only closes one
     * of the two doors.
     */
    @Test
    fun `an action whose feature flag is off is not offered`() = runTest(testDispatcher) {
        featureManager.disabled = setOf(FeatureFlag.CONTRACT_INFO)

        val viewModel = viewModel()

        val actions = viewModel.uiState.value.availableActions
        assertFalse(WorkshopAction.CONTRACT_ROWS in actions)
        // The rest of the menu is untouched.
        assertTrue(WorkshopAction.PAYMENT_SHEETS in actions)
    }

    @Test
    fun `every action is offered when nothing is disabled`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        assertEquals(WorkshopAction.entries.size, viewModel.uiState.value.availableActions.size)
    }

    /** A flag that cannot be read is not a flag that is off — the menu still opens. */
    @Test
    fun `a failing feature manager leaves the menu intact`() = runTest(testDispatcher) {
        featureManager.error = IllegalStateException("boom")

        val viewModel = viewModel()

        assertEquals(WorkshopAction.entries.size, viewModel.uiState.value.availableActions.size)
    }

    /**
     * Half an identity addresses a route that does not exist.
     *
     * Every workshop service takes workshopId/branchCode as path segments, so navigating with one
     * of them blank lands on a 404 the destination shows as an unexplained empty list. It is
     * refused here, where the missing half is still visible, and the refusal is spoken.
     */
    @Test
    fun `an action on a workshop missing half its identity does not navigate`() =
        runTest(testDispatcher) {
            val viewModel = viewModel()
            val incomplete = WorkshopPR(workshopId = "9028212822", branchCode = "", hasIdentity = false)

            viewModel.events.test {
                viewModel.sendIntent(WorkshopsIntent.ActionSelected(WorkshopAction.CONTRACT_ROWS, incomplete))
                assertTrue(awaitItem() is WorkshopsEvent.ShowMessage)
            }
        }

    @Test
    fun `an action on a complete workshop navigates`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        val complete = WorkshopPR(workshopId = "9028212822", branchCode = "0210", hasIdentity = true)

        viewModel.events.test {
            viewModel.sendIntent(WorkshopsIntent.ActionSelected(WorkshopAction.CONTRACT_ROWS, complete))
            val event = awaitItem()
            assertTrue(event is WorkshopsEvent.Navigate)
            assertEquals("0210", event.branchCode)
        }
    }

    @Test
    fun `a later page failing still counts what arrived`() = runTest(testDispatcher) {
        repository.employerAgreements = PagedListDN(
            items = List(WORKSHOP_PAGE_SIZE) { agreement(it, if (it < 4) ACTIVE_CODE else INACTIVE_CODE) },
            total = 15,
        )
        val viewModel = WorkshopsViewModel(
            GetEmployerAgreementsUseCase(FailAfterFirstCall(repository)),
            featureManager,
            GetArticleSixteenDebtsUseCase(repository),
        )

        assertEquals(WorkshopStats(total = WORKSHOP_PAGE_SIZE, active = 4), viewModel.uiState.value.stats)
        // The list itself is untouched by the count failing.
        assertNull(viewModel.uiState.value.list.error)
    }

    @Test
    fun `searching sends both halves, trimmed, and starts again at page zero`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = WORKSHOP_PAGE_SIZE, total = 30)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            // A real second page carries different workshops; identical rows would collapse.
            repository.employerAgreements =
                agreementsPage(count = WORKSHOP_PAGE_SIZE, total = 30, startAt = WORKSHOP_PAGE_SIZE)
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
        // ماده ۱۶ only opens on a workshop that has a debt to open on.
        repository.articleSixteenDebts = PagedListDN(items = listOf(WorkshopsDebtListModelDN(debitNumber = "1")))
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
    fun `article sixteen on a workshop with no debt answers with the dialog`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        repository.articleSixteenDebts = PagedListDN()
        val viewModel = viewModel()
        val workshop = viewModel.uiState.value.list.items.first()

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopsIntent.ActionSelected(WorkshopAction.ARTICLE_SIXTEEN, workshop),
            )

            // «لیست بدهی برای این کارگاه یافت نشد», as the design and the old app say it.
            assertTrue(viewModel.uiState.value.isNoDebtDialogOpen)
            assertFalse(viewModel.uiState.value.isCheckingDebts)
            expectNoEvents()
        }

        viewModel.sendIntent(WorkshopsIntent.NoDebtDialogDismissed)
        assertFalse(viewModel.uiState.value.isNoDebtDialogOpen)
    }

    @Test
    fun `a failed debt check says why and leaves the list alone`() = runTest(testDispatcher) {
        repository.employerAgreements = agreementsPage(count = 1, total = 1)
        val viewModel = viewModel()
        val workshop = viewModel.uiState.value.list.items.first()
        repository.error = TaminApiException(title = "سرویس در دسترس نیست")

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopsIntent.ActionSelected(WorkshopAction.ARTICLE_SIXTEEN, workshop),
            )

            assertEquals(WorkshopsEvent.ShowToast("سرویس در دسترس نیست"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.uiState.value.isCheckingDebts)
        assertFalse(viewModel.uiState.value.isNoDebtDialogOpen)
        assertEquals(1, viewModel.uiState.value.list.items.size)
        assertNull(viewModel.uiState.value.list.error)
    }

    @Test
    fun `a refused list keeps the service's own wording`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "دسترسی مجاز نیست")
        val viewModel = viewModel()

        assertEquals("دسترسی مجاز نیست", viewModel.uiState.value.list.error)
        assertFalse(viewModel.uiState.value.list.isLoading)
    }

    /** Workshop [id]'s row under one agreement; the same [id] twice is the same workshop twice. */
    private fun agreement(id: Int, statusCode: String) = EmployerAgreementDN(
        workshop = WorkshopSummaryDN(
            workshopId = "09682101$id",
            branchCode = "14",
            name = "آموزشگاه شماره $id",
            statusCode = statusCode,
        ),
    )

    private fun agreementsPage(count: Int, total: Int, startAt: Int = 0) = PagedListDN(
        items = List(count) {
            EmployerAgreementDN(
                workshop = WorkshopSummaryDN(
                    workshopId = "09682101${startAt + it}",
                    branchCode = "14",
                    name = "آموزشگاه شماره ${startAt + it}",
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

private val ACTIVE_CODE = WorkshopActivityStatus.ACTIVE.code
private val SEMI_ACTIVE_CODE = WorkshopActivityStatus.SEMI_ACTIVE.code
private val INACTIVE_CODE = WorkshopActivityStatus.INACTIVE.code

/** Answers each page from its own list, the way the service pages; past the last, nothing. */
private class PagedAgreements(
    private val delegate: FakeWorkShopsRepository,
    private val pages: List<PagedListDN<EmployerAgreementDN>>,
) : com.tamin.taminhamrah.repository.WorkShopsRepository by delegate {
    val requestedPages = mutableListOf<Int>()

    override suspend fun getEmployerAgreements(
        query: com.tamin.taminhamrah.model.workshop.WorkshopListQuery,
    ): PagedListDN<EmployerAgreementDN> {
        requestedPages += query.page
        return pages.getOrElse(query.page) { PagedListDN(total = pages.first().total) }
    }
}

/**
 * Answers the first call and refuses every one after it.
 *
 * Stands in for the count failing part-way: the first page is on screen, a later one the count
 * needed is not, and the strip must still print figures rather than nothing.
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
