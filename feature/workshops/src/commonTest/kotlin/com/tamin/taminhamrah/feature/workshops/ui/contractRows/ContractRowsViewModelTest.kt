package com.tamin.taminhamrah.feature.workshops.ui.contractRows

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowTab
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowsIntent
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.useCases.workshops.GetContractRowsWithAgreementUseCase
import com.tamin.taminhamrah.useCases.workshops.GetContractRowsWithoutAgreementUseCase
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

    /** The branch is optional — the service accepts the workshop on its own. */
    @Test
    fun `a blank branch code is accepted`() = runTest(testDispatcher) {
        repository.contractRowsWithAgreement = PagedListDN(listOf(agreementRow("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(ContractRowsIntent.DraftWorkshopIdChanged("9028212822"))
        vm.sendIntent(ContractRowsIntent.ApplyPicker)

        vm.uiState.test {
            val state = awaitItem()
            assertNotNull(state.applied)
            assertEquals("", state.applied.branchCode)
            assertFalse(state.showWorkshopIdError)
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
            assertTrue(state.myWorkshops.isEmpty())
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
