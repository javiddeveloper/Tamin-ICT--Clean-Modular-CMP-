package com.tamin.taminhamrah.feature.workshops.ui.contractRows

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowTab
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowsIntent
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.useCases.workshops.GetContractRowsWithAgreementUseCase
import com.tamin.taminhamrah.useCases.workshops.GetContractRowsWithoutAgreementUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import com.tamin.taminhamrah.model.workshop.ContractRowQuery
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.CompletableDeferred
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
 * ردیف‌های پیمان.
 *
 * Both endpoints take the workshop and branch as *path segments*, so the screen's real job is
 * refusing to make a request it cannot address, and making the right one once it can. The failures
 * worth catching are all of that shape: a blank code that silently requests a different route, a
 * tab that shows the other service's rows, and a picker button that does nothing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ContractRowsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ContractRowsViewModel(
        getWithAgreement = GetContractRowsWithAgreementUseCase(repository),
        getWithoutAgreement = GetContractRowsWithoutAgreementUseCase(repository),
        getMyWorkshops = GetEmployerAgreementsUseCase(repository),
    )

    private fun agreementRow(row: String, name: String = "دبستان کارن ۲") = EmployerAgreementDN(
        contractRow = row,
        startDate = "14040407",
        mobile = "09143018372",
        email = "karan.school@mail.com",
        workshop = WorkshopSummaryDN(workshopId = "9028212822", branchCode = "0210", name = name),
    )

    private fun leanRow(row: String) = WorkshopContractDN(
        contractRow = row,
        startDate = "14030120",
        workshopId = "9028212822",
        branchCode = "0210",
        workshopName = "شرکت راه‌سازی البرز شرق",
    )

    @Test
    fun `opening with an identity loads that workshop straight away`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("6")), total = 1)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals("9028212822", state.applied?.workshopId)
            assertEquals("0210", state.applied?.branchCode)
            assertEquals(1, state.list.items.size)
            assertEquals("۶", state.list.items.first().rowLabel)
            // The drill-down knows its workshop, so the picker must not cover the list it just got.
            assertFalse(state.isPickerOpen)
        }
        assertEquals("9028212822", repository.lastContractRowQuery?.workshopId)
    }

    /**
     * The services-grid entry knows no workshop. Showing an empty list would leave the user with
     * nothing to act on, so the picker is what opens instead.
     */
    @Test
    fun `opening without an identity raises the picker and requests nothing`() =
        runTest(testDispatcher) {
            val vm = viewModel()
            vm.sendIntent(ContractRowsIntent.Open("", ""))

            vm.uiState.test {
                val state = awaitItem()
                assertTrue(state.isPickerOpen)
                assertNull(state.applied)
                assertTrue(state.list.items.isEmpty())
            }
            assertNull(repository.lastContractRowQuery)
        }

    /**
     * کد کارگاه is a path segment; submitting blank cannot be allowed to "work".
     *
     * It must also not be swallowed — the message appears at the field rather than the button
     * appearing dead.
     */
    @Test
    fun `applying without a workshop code shows the error and makes no request`() =
        runTest(testDispatcher) {
            val vm = viewModel()
            vm.sendIntent(ContractRowsIntent.ApplyPicker)

            vm.uiState.test {
                val state = awaitItem()
                assertTrue(state.showWorkshopIdError)
                assertNull(state.applied)
            }
            assertNull(repository.lastContractRowQuery)
        }

    @Test
    fun `editing the workshop code clears the error`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.ApplyPicker)
        vm.sendIntent(ContractRowsIntent.DraftWorkshopIdChanged("9"))

        vm.uiState.test {
            val state = awaitItem()
            assertFalse(state.showWorkshopIdError)
            assertEquals("9", state.draftWorkshopId)
        }
    }

    @Test
    fun `applying a picked workshop loads it and closes the picker`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.DraftWorkshopIdChanged("9028212822"))
        vm.sendIntent(ContractRowsIntent.DraftBranchCodeChanged("0210"))
        vm.sendIntent(ContractRowsIntent.ApplyPicker)

        vm.uiState.test {
            val state = awaitItem()
            assertFalse(state.isPickerOpen)
            assertFalse(state.showWorkshopIdError)
            assertEquals("9028212822", state.applied?.workshopId)
            assertEquals(1, state.list.items.size)
        }
    }

    /**
     * کد شعبه is required, despite reading like a filter.
     *
     * It is a path segment: submitting blank builds
     * `…/get-employer-agreement-by-workshop-id-and-branch-code/6318210573/` and the live service
     * answers **404**, which the list then renders as "no rows" rather than as a failure. Verified
     * against the real backend on 2026-09-05; the old app refused to fetch without it too.
     */
    @Test
    fun `a blank branch code is rejected and makes no request`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.DraftWorkshopIdChanged("9028212822"))
        vm.sendIntent(ContractRowsIntent.ApplyPicker)

        vm.uiState.test {
            val state = awaitItem()
            assertNull(state.applied)
            assertTrue(state.showBranchCodeError)
            assertFalse(state.showWorkshopIdError)
        }
        assertNull(repository.lastContractRowQuery)
    }

    /** Both messages appear at once when both fields are empty — neither tap is swallowed. */
    @Test
    fun `applying with both codes blank flags both fields`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.ApplyPicker)

        vm.uiState.test {
            val state = awaitItem()
            assertTrue(state.showWorkshopIdError)
            assertTrue(state.showBranchCodeError)
        }
        assertNull(repository.lastContractRowQuery)
    }

    @Test
    fun `editing the branch code clears its error`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.ApplyPicker)
        vm.sendIntent(ContractRowsIntent.DraftBranchCodeChanged("6310"))

        vm.uiState.test {
            assertFalse(awaitItem().showBranchCodeError)
        }
    }

    /** A drill-down that arrives with half an identity must ask, not send `…/{workshopId}/`. */
    @Test
    fun `opening with a blank branch code raises the picker and requests nothing`() =
        runTest(testDispatcher) {
            val vm = viewModel()
            vm.sendIntent(ContractRowsIntent.Open("9028212822", ""))

            vm.uiState.test {
                val state = awaitItem()
                assertTrue(state.isPickerOpen)
                assertNull(state.applied)
            }
            assertNull(repository.lastContractRowQuery)
        }

    // ------------------------------------------------------------- tab auto-fallback

    /**
     * A workshop is in exactly one category, so the screen tries the other rather than showing an
     * empty list whose own copy tells the user to go and try it by hand.
     */
    @Test
    fun `an empty first tab falls back to the other one`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(emptyList(), total = 0)
        repository.contractRowsWithoutAgreement = PagedListDN(listOf(leanRow("3")), total = 1)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(ContractRowTab.WITHOUT_AGREEMENT, state.tab)
            assertEquals(1, state.list.items.size)
            assertTrue(state.didAutoSwitchTab)
        }
    }

    /** Two empty services settle on the empty state instead of ping-ponging between tabs. */
    @Test
    fun `both services empty settles without switching back`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(emptyList(), total = 0)
        repository.contractRowsWithoutAgreement = PagedListDN(emptyList(), total = 0)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            val state = awaitItem()
            assertTrue(state.list.items.isEmpty())
            assertTrue(state.list.isEmpty)
        }
    }

    /** A tab the user picked is final — an empty result there is not overridden. */
    @Test
    fun `a deliberate tab choice does not fall back`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("6")), total = 1)
        repository.contractRowsWithoutAgreement = PagedListDN(emptyList(), total = 0)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))
        vm.sendIntent(ContractRowsIntent.TabSelected(ContractRowTab.WITHOUT_AGREEMENT))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(ContractRowTab.WITHOUT_AGREEMENT, state.tab)
            assertTrue(state.list.items.isEmpty())
            assertFalse(state.didAutoSwitchTab)
        }
    }

    /** The same workshop arrives once per agreement it holds; the picker must offer it once. */
    @Test
    fun `the quick pick list drops duplicate workshops`() = runTest(testDispatcher) {
        repository.employerAgreements = PagedListDN(
            listOf(agreementRow("1"), agreementRow("2"), agreementRow("3")),
            total = 3,
        )

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.PickerOpenChanged(isOpen = true))

        vm.uiState.test {
            assertEquals(1, awaitItem().myWorkshops.items.size)
        }
    }

    /**
     * The two tabs are two different services, not a filter over one result.
     *
     * Switching must re-read, and must not leave the previous service's rows on screen underneath
     * the new tab's heading.
     */
    @Test
    fun `switching tab re-reads from the other service`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("6")), total = 1)
        repository.contractRowsWithoutAgreement =
            PagedListDN(listOf(leanRow("3"), leanRow("5")), total = 2)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))
        vm.sendIntent(ContractRowsIntent.TabSelected(ContractRowTab.WITHOUT_AGREEMENT))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(ContractRowTab.WITHOUT_AGREEMENT, state.tab)
            assertEquals(2, state.list.items.size)
            assertEquals("۳", state.list.items.first().rowLabel)
            // The lean service sends no contact columns, so none may appear on this tab.
            assertEquals("", state.list.items.first().mobile)
        }
    }

    /** With no workshop chosen there is nothing to re-read; only the empty state's wording changes. */
    @Test
    fun `switching tab before a workshop is chosen makes no request`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.TabSelected(ContractRowTab.WITHOUT_AGREEMENT))

        vm.uiState.test {
            assertEquals(ContractRowTab.WITHOUT_AGREEMENT, awaitItem().tab)
        }
        assertNull(repository.lastContractRowQuery)
    }

    @Test
    fun `clearing the picker drops the filter and the rows with it`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))
        vm.sendIntent(ContractRowsIntent.ClearPicker)

        vm.uiState.test {
            val state = awaitItem()
            assertNull(state.applied)
            assertTrue(state.list.items.isEmpty())
            assertEquals("", state.draftWorkshopId)
        }
    }

    /** Returning to a screen already showing a workshop keeps the page rather than refetching it. */
    @Test
    fun `reopening the same identity does not refetch`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))
        repository.contractRowsWithAgreement = PagedListDN(emptyList(), total = 0)
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            assertEquals(1, awaitItem().list.items.size)
        }
    }

    // ------------------------------------------------------------------------ paging

    /**
     * Load-more, which no real account can reach.
     *
     * Every workshop this feature was tested against holds at most one ردیف پیمان, so the second
     * page only ever exists here. A full page plus a larger total is exactly what the service
     * sends when more remain, and it is the only condition under which [PagedListState.hasMore]
     * is true.
     */
    @Test
    fun `a full first page asks for a second and appends it`() = runTest(testDispatcher) {
        val firstPage = List(WORKSHOP_PAGE_SIZE) { agreementRow(row = "${it + 1}") }
        repository.contractRowsWithAgreement = PagedListDN(firstPage, total = WORKSHOP_PAGE_SIZE + 3)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))
        vm.uiState.test {
            val state = awaitItem()
            assertEquals(WORKSHOP_PAGE_SIZE, state.list.items.size)
            assertTrue(state.list.hasMore)
            // start = page * pageSize, so the next page is ordinal 1 — the value the repository
            // is about to be asked for.
            assertEquals(1, state.list.nextPage)
        }

        val secondPage = List(3) { agreementRow(row = "${WORKSHOP_PAGE_SIZE + it + 1}") }
        repository.contractRowsWithAgreement = PagedListDN(secondPage, total = WORKSHOP_PAGE_SIZE + 3)
        vm.sendIntent(ContractRowsIntent.LoadMore)

        vm.uiState.test {
            val state = awaitItem()
            // Appended, not replaced — reaching the end of a list must not flick back to page one.
            assertEquals(WORKSHOP_PAGE_SIZE + 3, state.list.items.size)
            assertEquals("۱", state.list.items.first().rowLabel)
            assertFalse(state.list.hasMore)
        }
        assertEquals(1, repository.lastContractRowQuery?.page)
    }

    /** A short page is the end of the list even when the service overstates its total. */
    @Test
    fun `a short page ends the list`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement =
            PagedListDN(listOf(agreementRow("1")), total = 99)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            assertFalse(awaitItem().list.hasMore)
        }
    }

    /** Scrolling a list that has no more pages must not fire another request. */
    @Test
    fun `load more does nothing once the list is exhausted`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))
        vm.sendIntent(ContractRowsIntent.LoadMore)

        vm.uiState.test {
            assertEquals(1, awaitItem().list.items.size)
        }
        assertEquals(0, repository.lastContractRowQuery?.page)
    }

    // ------------------------------------------------- concurrent tab loads (item 7)

    /**
     * Holds the دارای تعهدنامه call open until released, so both tabs' requests are genuinely in
     * flight at once — the condition `flatMapMerge` allows and `flatMapLatest` would not.
     */
    private class GatedRepository(
        private val delegate: FakeWorkShopsRepository,
        val gate: CompletableDeferred<Unit>,
    ) : WorkShopsRepository by delegate {
        override suspend fun getContractRowsWithAgreement(
            query: ContractRowQuery,
        ): PagedListDN<EmployerAgreementDN> {
            gate.await()
            return delegate.getContractRowsWithAgreement(query)
        }
    }

    /**
     * A result for a tab the user has already left must not be painted under the new tab.
     *
     * `BaseViewModel` dispatches through `flatMapMerge`, so the first request is not canceled by
     * the second — it lands late. Each result carries the tab it was fetched for, and the reducer
     * drops it when that is no longer the tab on screen.
     */
    @Test
    fun `a result for the tab the user left is discarded`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("6")), total = 1)
        repository.contractRowsWithoutAgreement = PagedListDN(listOf(leanRow("3")), total = 1)
        val gate = CompletableDeferred<Unit>()
        val gated = GatedRepository(repository, gate)

        val vm = ContractRowsViewModel(
            getWithAgreement = GetContractRowsWithAgreementUseCase(gated),
            getWithoutAgreement = GetContractRowsWithoutAgreementUseCase(gated),
            getMyWorkshops = GetEmployerAgreementsUseCase(gated),
        )

        // Starts the دارای تعهدنامه load, which now blocks on the gate.
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))
        // The user switches before it returns; the lean service answers immediately.
        vm.sendIntent(ContractRowsIntent.TabSelected(ContractRowTab.WITHOUT_AGREEMENT))
        assertEquals("۳", vm.uiState.value.list.items.single().rowLabel)

        // The abandoned request finally lands.
        gate.complete(Unit)

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(ContractRowTab.WITHOUT_AGREEMENT, state.tab)
            // Still the lean service's row — the late result was dropped, not painted.
            assertEquals("۳", state.list.items.single().rowLabel)
        }
    }

    /** The same guard on the failure path: an abandoned tab's error stays off the current one. */
    @Test
    fun `an error for the tab the user left is discarded`() = runTest(testDispatcher) {
        repository.contractRowsWithoutAgreement = PagedListDN(listOf(leanRow("3")), total = 1)
        val gate = CompletableDeferred<Unit>()
        val gated = object : WorkShopsRepository by repository {
            override suspend fun getContractRowsWithAgreement(
                query: ContractRowQuery,
            ): PagedListDN<EmployerAgreementDN> {
                gate.await()
                throw IllegalStateException("boom")
            }
        }

        val vm = ContractRowsViewModel(
            getWithAgreement = GetContractRowsWithAgreementUseCase(gated),
            getWithoutAgreement = GetContractRowsWithoutAgreementUseCase(gated),
            getMyWorkshops = GetEmployerAgreementsUseCase(gated),
        )

        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))
        vm.sendIntent(ContractRowsIntent.TabSelected(ContractRowTab.WITHOUT_AGREEMENT))
        gate.complete(Unit)

        vm.uiState.test {
            val state = awaitItem()
            assertNull(state.list.error)
            assertEquals("۳", state.list.items.single().rowLabel)
        }
    }

    // ----------------------------------------------------- counts and caps (items 8, 9)

    /** The chip counts what the service holds, not what has been paged in so far. */
    @Test
    fun `the list carries the servers total not the loaded count`() = runTest(testDispatcher) {
        val firstPage = List(WORKSHOP_PAGE_SIZE) { agreementRow(row = "${it + 1}") }
        repository.contractRowsWithAgreement = PagedListDN(firstPage, total = 37)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(WORKSHOP_PAGE_SIZE, state.list.items.size)
            assertEquals(37, state.list.total)
        }
    }

    /** The quick-pick pages in the rest as it is scrolled rather than stopping silently at one page. */
    @Test
    fun `the quick pick pages in the rest of the workshops`() = runTest(testDispatcher) {
        fun workshops(from: Int, count: Int) = List(count) {
            EmployerAgreementDN(
                contractRow = "1",
                workshop = WorkshopSummaryDN(workshopId = "${from + it}", branchCode = "0210"),
            )
        }
        val total = WORKSHOP_PAGE_SIZE + 3
        repository.employerAgreements = PagedListDN(workshops(0, WORKSHOP_PAGE_SIZE), total)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.PickerOpenChanged(isOpen = true))
        assertEquals(WORKSHOP_PAGE_SIZE, vm.uiState.value.myWorkshops.items.size)
        assertTrue(vm.uiState.value.myWorkshops.canLoadMore)

        repository.employerAgreements = PagedListDN(workshops(WORKSHOP_PAGE_SIZE, 3), total)
        vm.sendIntent(ContractRowsIntent.LoadMoreMyWorkshops)

        val state = vm.uiState.value.myWorkshops
        assertEquals(total, state.items.size)
        assertFalse(state.canLoadMore)
        assertEquals(1, repository.lastWorkshopListQuery?.page)
    }

    /**
     * کارگاه‌های شما is a convenience above two fields that already work.
     *
     * If it fails, the section is absent — it must not put an error on a list the user has not
     * asked for yet.
     */
    @Test
    fun `a failing quick pick list does not fail the screen`() = runTest(testDispatcher) {
        repository.error = IllegalStateException("boom")

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.PickerOpenChanged(isOpen = true))

        vm.uiState.test {
            val state = awaitItem()
            assertTrue(state.isPickerOpen)
            assertTrue(state.myWorkshops.items.isEmpty())
            assertFalse(state.myWorkshops.canLoadMore)
            assertNull(state.list.error)
        }
    }

    @Test
    fun `a failing page surfaces as a list error`() = runTest(testDispatcher) {
        repository.error = IllegalStateException("boom")

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            val state = awaitItem()
            assertNotNull(state.list.error)
            assertTrue(state.list.items.isEmpty())
        }
    }
}
