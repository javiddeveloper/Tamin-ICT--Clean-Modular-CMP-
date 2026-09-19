package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.model.PersonSearch
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopMembersUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * کارکنان: the paging and searching every workshop list screen is built from.
 *
 * The assertions are as much about the *query* as about the answer — a filter that quietly stops
 * being sent, or a page asked for twice, both look perfectly healthy on screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopMembersViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository
    private lateinit var viewModel: WorkshopMembersViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
        viewModel = WorkshopMembersViewModel(GetWorkshopMembersUseCase(repository))
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `opening loads the first page for that workshop`() = runTest(testDispatcher) {
        repository.members = membersPage(count = 3, total = 3)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            val loaded = awaitUntil { it.list.items.isNotEmpty() }

            assertEquals(3, loaded.list.items.size)
            assertFalse(loaded.list.isLoading)
            val query = assertNotNull(repository.lastMemberQuery)
            assertEquals(WORKSHOP_ID, query.workshopId)
            assertEquals(BRANCH_CODE, query.branchCode)
            assertEquals(0, query.page)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `opening the same workshop again does not refetch`() = runTest(testDispatcher) {
        repository.members = membersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            repository.members = membersPage(count = 9, total = 9)

            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))

            assertEquals(1, viewModel.uiState.value.list.items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an identity that never arrived fails without asking the service`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(workshopId = "", branchCode = ""))
            // Blank ids mean the screen was reached without them; a request would 404 anyway.
            assertNull(repository.lastMemberQuery)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading more asks for the next page and appends`() = runTest(testDispatcher) {
        repository.members = membersPage(count = WORKSHOP_PAGE_SIZE, total = 30)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.size == WORKSHOP_PAGE_SIZE }

            repository.members = membersPage(count = WORKSHOP_PAGE_SIZE, total = 30, from = WORKSHOP_PAGE_SIZE)
            viewModel.sendIntent(WorkshopMembersIntent.LoadMore)
            val appended = awaitUntil { it.list.items.size > WORKSHOP_PAGE_SIZE }

            assertEquals(WORKSHOP_PAGE_SIZE * 2, appended.list.items.size)
            assertEquals(1, repository.lastMemberQuery?.page)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loading more at the end of the list asks for nothing`() = runTest(testDispatcher) {
        repository.members = membersPage(count = 2, total = 2)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            assertEquals(0, repository.lastMemberQuery?.page)

            viewModel.sendIntent(WorkshopMembersIntent.LoadMore)

            assertEquals(0, repository.lastMemberQuery?.page)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applying a search sends both fields and restarts at page zero`() = runTest(testDispatcher) {
        repository.members = membersPage(count = WORKSHOP_PAGE_SIZE, total = 30)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            repository.members = membersPage(count = WORKSHOP_PAGE_SIZE, total = 30, from = WORKSHOP_PAGE_SIZE)
            viewModel.sendIntent(WorkshopMembersIntent.LoadMore)
            awaitUntil { it.list.items.size > WORKSHOP_PAGE_SIZE }

            val search = PersonSearch(nationalId = "0024567891", insuranceNumber = "1122334455")
            viewModel.sendIntent(WorkshopMembersIntent.DraftChanged(search))
            viewModel.sendIntent(WorkshopMembersIntent.ApplySearch)
            val applied = awaitUntil { it.applied == search }

            val query = assertNotNull(repository.lastMemberQuery)
            assertEquals("0024567891", query.nationalId)
            assertEquals("1122334455", query.insuranceNumber)
            assertEquals(0, query.page)
            assertEquals(WORKSHOP_PAGE_SIZE, applied.list.items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applying a search folds the panel away`() = runTest(testDispatcher) {
        repository.members = membersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            viewModel.sendIntent(WorkshopMembersIntent.SearchOpenChanged(true))
            awaitUntil { it.isSearchOpen }

            viewModel.sendIntent(WorkshopMembersIntent.ApplySearch)

            assertFalse(awaitUntil { !it.isSearchOpen }.isSearchOpen)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `blank search fields are left out of the query entirely`() = runTest(testDispatcher) {
        repository.members = membersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }

            viewModel.sendIntent(
                WorkshopMembersIntent.DraftChanged(PersonSearch(nationalId = "0024567891")),
            )
            viewModel.sendIntent(WorkshopMembersIntent.ApplySearch)
            awaitUntil { it.applied.nationalId == "0024567891" }

            // Not "" — an empty filter and an absent one mean different things to the service.
            assertNull(repository.lastMemberQuery?.insuranceNumber)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing the search drops the filters and reloads`() = runTest(testDispatcher) {
        repository.members = membersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            viewModel.sendIntent(
                WorkshopMembersIntent.DraftChanged(PersonSearch(nationalId = "0024567891")),
            )
            viewModel.sendIntent(WorkshopMembersIntent.ApplySearch)
            awaitUntil { it.applied.isNotEmpty }

            viewModel.sendIntent(WorkshopMembersIntent.ClearSearch)
            val cleared = awaitUntil { !it.applied.isNotEmpty }

            assertEquals(PersonSearch(), cleared.draft)
            assertNull(repository.lastMemberQuery?.nationalId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removing one search chip keeps the other and reloads with it`() = runTest(testDispatcher) {
        repository.members = membersPage(count = 1, total = 1)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.items.isNotEmpty() }
            val both = PersonSearch(nationalId = "0024567891", insuranceNumber = "0010517475")
            viewModel.sendIntent(WorkshopMembersIntent.DraftChanged(both))
            viewModel.sendIntent(WorkshopMembersIntent.ApplySearch)
            awaitUntil { it.applied == both }

            viewModel.sendIntent(WorkshopMembersIntent.ReplaceSearch(both.copy(nationalId = "")))
            val narrowed = awaitUntil { it.applied.nationalId.isBlank() }

            assertEquals("0010517475", narrowed.applied.insuranceNumber)
            assertEquals(narrowed.applied, narrowed.draft)
            assertNull(repository.lastMemberQuery?.nationalId)
            assertEquals("0010517475", repository.lastMemberQuery?.insuranceNumber)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a refused request becomes the list's error, in the service's words`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "دسترسی مجاز نیست")

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            val failed = awaitUntil { it.list.error != null }

            assertEquals("دسترسی مجاز نیست", failed.list.error)
            assertFalse(failed.list.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry asks again after a failure`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "خطا")

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            awaitUntil { it.list.error != null }

            repository.error = null
            repository.members = membersPage(count = 2, total = 2)
            viewModel.sendIntent(WorkshopMembersIntent.Retry)
            val recovered = awaitUntil { it.list.items.isNotEmpty() }

            assertEquals(2, recovered.list.items.size)
            assertNull(recovered.list.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a member with one missing name half does not render a literal null`() = runTest(testDispatcher) {
        repository.members = PagedListDN(
            items = listOf(WorkshopMemberDN(firstName = "زهرا", lastName = "")),
            total = 1,
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(WorkshopMembersIntent.Open(WORKSHOP_ID, BRANCH_CODE))
            val loaded = awaitUntil { it.list.items.isNotEmpty() }

            assertTrue(loaded.list.items.first().fullName.isNotBlank())
            assertFalse(loaded.list.items.first().fullName.contains("null"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * [count] distinct people, numbered from [from]. A second page must hold different people:
     * the list drops a row identical to one already shown, so a repeated page appends nothing.
     */
    private fun membersPage(count: Int, total: Int, from: Int = 0) = PagedListDN(
        items = List(count) {
            val n = from + it
            WorkshopMemberDN(
                insuranceNumber = "1000000$n",
                firstName = "کارمند",
                lastName = "شماره $n",
                nationalId = "002456789$n",
            )
        },
        total = total,
    )

    private suspend fun ReceiveTurbine<WorkshopMembersUiState>.awaitUntil(
        predicate: (WorkshopMembersUiState) -> Boolean,
    ): WorkshopMembersUiState {
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
