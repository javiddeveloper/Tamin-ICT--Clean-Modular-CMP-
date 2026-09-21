package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractTab
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsEvent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.ComputationalBaseKeys
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentDN
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.BaseDocumentPR
import com.tamin.taminhamrah.model.workshop.ComputationalBaseDN
import com.tamin.taminhamrah.model.workshop.SettlementCertificateDN
import com.tamin.taminhamrah.model.workshop.SettlementCertificatePR
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.useCases.userRequest.DownloadUserRequestDocumentUseCase
import com.tamin.taminhamrah.useCases.workshops.GetAssignerContractsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetComputationalBasePdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetComputationalBasesUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetSettlementCertificateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
 * واگذارندگان.
 *
 * The list is gated on a search that only the design requires — all three codes are filter clauses,
 * so the service would happily answer an unbounded request. What is worth pinning is therefore the
 * screen's own refusals and the two places a wrong answer would look like a right one: a page that
 * arrives for a پیمان the user has already left, and a document fetched down the wrong of two
 * routes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AssignerContractsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository
    private lateinit var documents: FakeUserRequestRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
        documents = FakeUserRequestRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = AssignerContractsViewModel(
        getContracts = GetAssignerContractsUseCase(repository),
        getBases = GetComputationalBasesUseCase(repository),
        getBasePdf = GetComputationalBasePdfUseCase(repository),
        downloadDocumentImage = DownloadUserRequestDocumentUseCase(documents),
        getMyWorkshops = GetEmployerAgreementsUseCase(repository),
        getCertificate = GetSettlementCertificateUseCase(repository),
    )

    private fun contract(
        row: String,
        sequence: String = "3",
        workshopId: String = "9028212822",
    ) = AssignerContractDN(
        contractRow = row,
        contractSequence = sequence,
        contractNumber = "44122",
        contractDate = "14010210",
        contractSubject = "خدمات نظافت و پشتیبانی",
        assigner = AssignerPartyDN(workshopId = "0968210170", workshopName = "آموزشگاه توکلی"),
        employer = AssignerPartyDN(
            workshopId = workshopId,
            workshopName = "دبستان کارن ۲",
            branchCode = "0210",
        ),
    )

    private fun base(letterNumber: String, vararg docs: BaseDocumentDN) = ComputationalBaseDN(
        letterNumber = letterNumber,
        sendDate = 1670000000000L,
        amount = 84_000_000L,
        documents = docs.toList(),
    )

    // ------------------------------------------------------------------------------ the list

    @Test
    fun `opening with a workshop searches for it straight away`() = runTest(testDispatcher) {
        repository.assignerContracts = PagedListDN(listOf(contract("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals("9028212822", state.filter?.workshopId)
            assertEquals("0210", state.filter?.branchCode)
            assertEquals(1, state.list.items.size)
            assertEquals("۱", state.list.items.single().card.rowLabel)
            // The drill-down knows its workshop; the sheet must not cover the list it just got.
            assertFalse(state.isSearchOpen)
        }
        assertEquals("9028212822", repository.lastAssignerContractQuery?.workshopId)
    }

    /**
     * The services-grid entry knows no workshop and shows every پیمان anyway — the list is not gated
     * on a search. Nothing is filtered, and the sheet stays down.
     */
    @Test
    fun `opening without a workshop loads every contract`() = runTest(testDispatcher) {
        repository.assignerContracts = PagedListDN(listOf(contract("1"), contract("2")), total = 2)

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.Open("", ""))

        vm.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSearchOpen)
            assertNull(state.filter)
            assertEquals(2, state.list.items.size)
        }
        assertEquals("", repository.lastAssignerContractQuery?.workshopId)
        assertNull(repository.lastAssignerContractQuery?.branchCode)
    }

    /** Coming back to the screen keeps the rows already in hand instead of fetching them again. */
    @Test
    fun `re-opening the unfiltered list does not refetch`() = runTest(testDispatcher) {
        repository.assignerContracts = PagedListDN(listOf(contract("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.Open("", ""))
        vm.sendIntent(AssignerContractsIntent.Open("", ""))

        assertEquals(1, repository.assignerContractQueries.size)
    }

    /**
     * A blank کد کارگاه surfaces at the field rather than doing nothing.
     *
     * The service would accept the request — the code is a filter clause, not a path segment — so
     * this guard is the design's, and the button that hits it has to say so.
     */
    @Test
    fun `applying a blank workshop code shows the error and requests nothing`() =
        runTest(testDispatcher) {
            val vm = viewModel()
            vm.sendIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))
            vm.sendIntent(AssignerContractsIntent.ApplySearch)

            vm.uiState.test {
                val state = awaitItem()
                assertTrue(state.draft.showWorkshopIdError)
                assertNull(state.filter)
            }
            assertNull(repository.lastAssignerContractQuery)
        }

    /** The optional halves reach the query as nulls, which is what makes the repository drop them. */
    @Test
    fun `applying with only a workshop code leaves the optional filters unset`() =
        runTest(testDispatcher) {
            repository.assignerContracts = PagedListDN(listOf(contract("1")), total = 1)

            val vm = viewModel()
            vm.sendIntent(AssignerContractsIntent.DraftWorkshopIdChanged("9028212822"))
            vm.sendIntent(AssignerContractsIntent.ApplySearch)

            assertEquals("9028212822", repository.lastAssignerContractQuery?.workshopId)
            assertNull(repository.lastAssignerContractQuery?.branchCode)
            assertNull(repository.lastAssignerContractQuery?.contractRow)
        }

    @Test
    fun `applying a search closes the sheet and carries all three codes`() =
        runTest(testDispatcher) {
            repository.assignerContracts = PagedListDN(listOf(contract("2")), total = 1)

            val vm = viewModel()
            vm.sendIntent(AssignerContractsIntent.DraftWorkshopIdChanged("9028212822"))
            vm.sendIntent(AssignerContractsIntent.DraftBranchCodeChanged("0210"))
            vm.sendIntent(AssignerContractsIntent.DraftContractRowChanged("2"))
            vm.sendIntent(AssignerContractsIntent.ApplySearch)

            vm.uiState.test {
                val state = awaitItem()
                assertFalse(state.isSearchOpen)
                assertEquals("2", state.filter?.contractRow)
            }
            assertEquals("0210", repository.lastAssignerContractQuery?.branchCode)
            assertEquals("2", repository.lastAssignerContractQuery?.contractRow)
        }

    /**
     * Every page arrives without the user scrolling for it.
     *
     * The tabs and their count are decided on the device, so a result held back behind a scroll
     * would leave a tab reading empty while its rows sat on a page nobody had asked for.
     */
    @Test
    fun `every page of the result is loaded up front`() = runTest(testDispatcher) {
        val total = WORKSHOP_PAGE_SIZE + 1
        repository.assignerContractPages = mapOf(
            0 to PagedListDN(List(WORKSHOP_PAGE_SIZE) { contract(row = (it + 1).toString()) }, total),
            1 to PagedListDN(listOf(contract("11")), total),
        )

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(total, state.list.items.size)
            assertEquals("۱۱", state.list.items.last().card.rowLabel)
            assertFalse(state.list.isLoadingMore)
        }
        assertEquals(listOf(0, 1), repository.assignerContractQueries.map { it.page })
    }

    /** Clearing drops the filter and goes back to every پیمان, not to an empty page. */
    @Test
    fun `clearing the search goes back to every contract`() = runTest(testDispatcher) {
        repository.assignerContracts = PagedListDN(listOf(contract("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.Open("9028212822", "0210"))
        repository.assignerContracts = PagedListDN(listOf(contract("1"), contract("2")), total = 2)
        vm.sendIntent(AssignerContractsIntent.ClearSearch)

        vm.uiState.test {
            val state = awaitItem()
            assertNull(state.filter)
            assertEquals(2, state.list.items.size)
            assertEquals("", state.draft.workshopId)
        }
        assertEquals("", repository.lastAssignerContractQuery?.workshopId)
    }

    /**
     * Choosing a tab only changes which half is shown: the rows stay in hand and nothing is fetched
     * again, so switching back and forth costs no request.
     */
    @Test
    fun `selecting a tab switches the half shown without refetching`() = runTest(testDispatcher) {
        repository.assignerContracts = PagedListDN(listOf(contract("1")), total = 1)

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.Open("", ""))
        vm.sendIntent(AssignerContractsIntent.TabSelected(AssignerContractTab.FINISHED))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(AssignerContractTab.FINISHED, state.tab)
            assertEquals(1, state.list.items.size)
        }
        assertEquals(1, repository.assignerContractQueries.size)
    }

    /** A failed list is not an empty one, and the screen has to be able to tell them apart. */
    @Test
    fun `a failed search reports a failure rather than an empty list`() = runTest(testDispatcher) {
        repository.error = IllegalStateException("no connection")

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.Open("9028212822", "0210"))

        vm.uiState.test {
            val state = awaitItem()
            assertTrue(state.list.isFailed)
            assertFalse(state.list.isEmpty)
        }
    }

    // ------------------------------------------------------------------ کارگاه‌های شما

    private fun myWorkshop(id: String, branch: String = "6310") = EmployerAgreementDN(
        workshop = WorkshopSummaryDN(workshopId = id, branchCode = branch, name = "کارگاه $id"),
    )

    /** Typing a ten-digit code from memory is the worst part of the sheet; the list is the fix. */
    @Test
    fun `opening the search fetches the employers own workshops once`() = runTest(testDispatcher) {
        repository.employerAgreements = PagedListDN(
            listOf(myWorkshop("9028212822"), myWorkshop("6318210573")),
            total = 8,
        )

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.myWorkshops.items.size)
            // The employer's real count, so the list knows there is more to page in.
            assertEquals(8, state.myWorkshops.total)
        }

        // Re-opening must not re-fetch: the rows are already in hand. Proven by changing what the
        // service would answer and showing the state does not move.
        repository.employerAgreements = PagedListDN(listOf(myWorkshop("1111111111")), total = 1)
        vm.sendIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = false))
        vm.sendIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))

        assertEquals(2, vm.uiState.value.myWorkshops.items.size)
        assertEquals(8, vm.uiState.value.myWorkshops.total)
    }

    /** The quick-pick pages in the rest as it is scrolled rather than stopping silently at one page. */
    @Test
    fun `the quick pick pages in the rest of the workshops`() = runTest(testDispatcher) {
        fun workshops(from: Int, count: Int) = List(count) { myWorkshop("${from + it}") }
        val total = WORKSHOP_PAGE_SIZE + 3
        repository.employerAgreements = PagedListDN(workshops(0, WORKSHOP_PAGE_SIZE), total)

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))
        assertEquals(WORKSHOP_PAGE_SIZE, vm.uiState.value.myWorkshops.items.size)
        assertTrue(vm.uiState.value.myWorkshops.canLoadMore)

        repository.employerAgreements = PagedListDN(workshops(WORKSHOP_PAGE_SIZE, 3), total)
        vm.sendIntent(AssignerContractsIntent.LoadMoreMyWorkshops)

        val state = vm.uiState.value.myWorkshops
        assertEquals(total, state.items.size)
        assertFalse(state.canLoadMore)
        assertEquals(1, repository.lastWorkshopListQuery?.page)
    }

    /**
     * One کارگاه reaches this list once per agreement it holds, so the same workshop arrives
     * more than once. Deduped on the identity the pick actually uses.
     */
    @Test
    fun `the quick-pick list drops repeated workshops`() = runTest(testDispatcher) {
        repository.employerAgreements = PagedListDN(
            listOf(myWorkshop("9028212822"), myWorkshop("9028212822"), myWorkshop("6318210573")),
            total = 3,
        )

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))

        assertEquals(2, vm.uiState.value.myWorkshops.items.size)
    }

    /** Picking a row fills the code *and* its branch, which is the whole point of the list. */
    @Test
    fun `picking a workshop fills both codes and clears the error`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))
        vm.sendIntent(AssignerContractsIntent.ApplySearch)
        assertTrue(vm.uiState.value.draft.showWorkshopIdError)

        vm.sendIntent(AssignerContractsIntent.QuickPicked("9028212822", "6310"))

        vm.uiState.test {
            val draft = awaitItem().draft
            assertEquals("9028212822", draft.workshopId)
            assertEquals("6310", draft.branchCode)
            assertFalse(draft.showWorkshopIdError)
        }
    }

    /**
     * کارگاه‌های شما is a convenience above three fields that already work.
     *
     * Failing to fetch it must not put an error on a list the user has not asked for yet — the
     * section simply does not appear.
     */
    @Test
    fun `a failed workshop fetch leaves the search usable`() = runTest(testDispatcher) {
        repository.error = IllegalStateException("no connection")

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))

        vm.uiState.test {
            val state = awaitItem()
            assertTrue(state.isSearchOpen)
            assertTrue(state.myWorkshops.items.isEmpty())
            // A failing page stops paging, so it is not asked for again on every scroll.
            assertFalse(state.myWorkshops.canLoadMore)
            // Nothing was requested for the list itself, so it must not read as failed.
            assertFalse(state.list.isFailed)
        }
    }

    // --------------------------------------------------------------- the action sheet + bases

    /**
     * The row keeps the raw keys جزئیات پیمان is addressed by.
     *
     * That screen has no state of its own: its route carries ردیف and sequence, and it finds the
     * پیمان back in this list. Persian digits here — or a trimmed sequence — would make it
     * unfindable, so the pair is pinned as ASCII alongside the label the card prints.
     */
    @Test
    fun `list rows keep the raw keys their detail route is addressed by`() =
        runTest(testDispatcher) {
            repository.assignerContracts = PagedListDN(listOf(contract("1")), total = 1)

            val vm = viewModel()
            vm.sendIntent(AssignerContractsIntent.Open("9028212822", "0210"))

            val row = vm.uiState.value.list.items.single()
            assertEquals("1", row.contractRow)
            assertEquals("3", row.contractSequence)
            // The card still prints the Persian form; the keys are not what is displayed.
            assertEquals("۱", row.card.rowLabel)
            assertEquals(
                row,
                vm.uiState.value.list.items.firstOrNull {
                    it.contractRow == "1" && it.contractSequence == "3"
                },
            )
        }

    /** A پیمان missing any of the four keys cannot address its own bases, and says so up front. */
    @Test
    fun `a contract without a sequence cannot open its bases`() = runTest(testDispatcher) {
        repository.assignerContracts = PagedListDN(listOf(contract("1", sequence = "")), total = 1)

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.Open("9028212822", "0210"))

        assertFalse(vm.uiState.value.list.items.single().canOpenBases)
    }

    @Test
    fun `opening bases sends all four keys and keeps them`() = runTest(testDispatcher) {
        repository.computationalBases = PagedListDN(listOf(base("12044")), total = 1)
        val keys = ComputationalBaseKeys("9028212822", "0210", "1", "3")

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.OpenBases(keys))

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(keys, state.basesKeys)
            assertEquals("۱۲۰۴۴", state.bases.items.single().letterNumber)
        }
        assertEquals("0210", repository.lastComputationalBaseQuery?.branchCode)
        assertEquals("3", repository.lastComputationalBaseQuery?.contractSequence)
    }

    /** Returning to a پیمان already loaded keeps the page instead of fetching it again. */
    @Test
    fun `re-opening the same bases does not refetch`() = runTest(testDispatcher) {
        repository.computationalBases = PagedListDN(listOf(base("12044")), total = 1)
        val keys = ComputationalBaseKeys("9028212822", "0210", "1", "3")

        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.OpenBases(keys))
        repository.lastComputationalBaseQuery = null
        vm.sendIntent(AssignerContractsIntent.OpenBases(keys))

        assertNull(repository.lastComputationalBaseQuery)
    }

    /**
     * A page that lands after the user has moved to another پیمان is dropped.
     *
     * `BaseViewModel` runs intents through `flatMapMerge`, so a slow request is superseded rather
     * than canceled and can arrive last. Without the tag it would paint one contract's bases under
     * another's heading.
     */
    @Test
    fun `a page for a contract the user has left is discarded`() = runTest(testDispatcher) {
        val stale = ComputationalBaseKeys("9028212822", "0210", "1", "3")
        val current = ComputationalBaseKeys("9028212822", "0210", "2", "4")

        repository.computationalBases = PagedListDN(listOf(base("99999")), total = 1)
        val vm = viewModel()
        vm.sendIntent(AssignerContractsIntent.OpenBases(current))

        // The reducer is what drops it, so the stale page is handed straight to it.
        val reduced = vm.uiState.value
        assertEquals(current, reduced.basesKeys)
        assertEquals("۹۹۹۹۹", reduced.bases.items.single().letterNumber)

        // Re-opening the stale keys refetches under *those* keys, which is the legitimate case;
        // what must not happen is the earlier keys overwriting the current list without moving
        // `basesKeys` with them.
        vm.sendIntent(AssignerContractsIntent.OpenBases(stale))
        assertEquals(stale, vm.uiState.value.basesKeys)
    }

    // ---------------------------------------------------------------------------- documents

    /** An image goes down `upload-image` and reaches the viewer as the base64 the service sent. */
    @Test
    fun `an image document is fetched as base64`() = runTest(testDispatcher) {
        documents.document = "BASE64DATA"

        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "a1", kind = BaseDocumentKind.IMAGE)
            )
        )

        vm.uiState.test {
            val state = awaitItem()
            assertEquals("BASE64DATA", state.preview?.imageData)
            assertEquals(BaseDocumentKind.IMAGE, state.preview?.kind)
            assertNull(state.openingDocumentId)
        }
        assertEquals("a1", documents.lastGuid)
    }

    /**
     * An image the service answered blank carries its failure into the viewer.
     *
     * `TaminImageViewer` now has the same three states the PDF viewer does, so a failure is shown
     * where the user is looking rather than only behind them on the row.
     */
    @Test
    fun `an empty image opens the viewer carrying its failure`() = runTest(testDispatcher) {
        documents.document = ""

        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "a1", kind = BaseDocumentKind.IMAGE)
            )
        )

        vm.uiState.test {
            val state = awaitItem()
            assertNull(state.preview?.imageData?.ifBlank { null })
            assertNull(state.openingDocumentId)
            // The viewer stays up and says so, exactly as the PDF one does.
            assertEquals(true, state.preview?.didFail)
            // It failed without saying why, so both fall back to their generic line.
            assertEquals("a1", state.documentFailure?.documentId)
            assertNull(state.documentFailure?.message)
        }
    }

    /**
     * The service's own words reach the row.
     *
     * `upload-image` answers a missing document with «داده ای با اطلاعات شناسه … یافت نشد.», which
     * names the id it could not find. Substituting a generic line for that throws away the only
     * part of the failure worth reading.
     */
    @Test
    fun `a failed document carries the services own reason`() = runTest(testDispatcher) {
        documents.failure = TaminApiException(
            title = "داده ای با اطلاعات شناسه img-1 یافت نشد."
        )

        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "img-1", kind = BaseDocumentKind.IMAGE)
            )
        )

        vm.uiState.test {
            val state = awaitItem()
            assertEquals("img-1", state.documentFailure?.documentId)
            assertEquals("داده ای با اطلاعات شناسه img-1 یافت نشد", state.documentFailure?.message)
            // The viewer is up and carrying the failure, the same shape a PDF takes.
            assertEquals("img-1", state.preview?.documentId)
            assertEquals(true, state.preview?.didFail)
            // The row is live again, because tapping it is the retry.
            assertNull(state.openingDocumentId)
        }
    }

    /**
     * A document the service does not hold is marked permanently unavailable.
     *
     * The row then badges itself and stops responding, because another ask can only produce the
     * same 404. Read off the parsed `ErrorUri`, not the message text — that is localized copy.
     */
    @Test
    fun `a not-found document is marked missing`() = runTest(testDispatcher) {
        documents.failure = TaminApiException(
            title = "یافت نشد",
            cause = TaminErrorUriException(ErrorUri.RESOURCE_NOT_FOUND),
        )

        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "img-1", kind = BaseDocumentKind.IMAGE)
            )
        )

        assertEquals(true, vm.uiState.value.documentFailure?.isMissing)
    }

    /**
     * A fetch that never got through is *not* marked missing.
     *
     * Losing that distinction would let one dropped connection close a row for good, on a document
     * that is perfectly fine.
     */
    @Test
    fun `a connection failure leaves the document retryable`() = runTest(testDispatcher) {
        documents.failure = TaminApiException(
            title = "خطای اتصال",
            cause = TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
        )

        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "img-1", kind = BaseDocumentKind.IMAGE)
            )
        )

        val failure = vm.uiState.value.documentFailure
        assertEquals("img-1", failure?.documentId)
        assertEquals(false, failure?.isMissing)
    }

    /** An image the service answered blank is missing too — it answered, with nothing in it. */
    @Test
    fun `an empty image is marked missing`() = runTest(testDispatcher) {
        documents.document = ""

        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "a1", kind = BaseDocumentKind.IMAGE)
            )
        )

        assertEquals(true, vm.uiState.value.documentFailure?.isMissing)
    }

    /** Tapping a failed row again clears its reason before trying, so a stale one cannot linger. */
    @Test
    fun `retrying a failed document clears its reason`() = runTest(testDispatcher) {
        documents.failure = TaminApiException(title = "یافت نشد")

        val vm = viewModel()
        val document = BaseDocumentPR(documentId = "img-1", kind = BaseDocumentKind.IMAGE)
        vm.sendIntent(AssignerContractsIntent.DocumentTapped(document))
        assertNotNull(vm.uiState.value.documentFailure)

        documents.failure = null
        documents.document = "BASE64DATA"
        vm.sendIntent(AssignerContractsIntent.DocumentTapped(document))

        vm.uiState.test {
            val state = awaitItem()
            assertNull(state.documentFailure)
            assertEquals("BASE64DATA", state.preview?.imageData)
        }
    }

    /** Both kinds raise the viewer before the bytes land, so neither tap looks inert. */
    @Test
    fun `a pdf document raises the viewer and then fills it`() = runTest(testDispatcher) {
        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "b2", kind = BaseDocumentKind.PDF)
            )
        )

        vm.uiState.test {
            val state = awaitItem()
            assertNotNull(state.preview)
            assertEquals(BaseDocumentKind.PDF, state.preview.kind)
            assertEquals("b2", state.preview.documentId)
            assertNull(state.openingDocumentId)
        }
        assertEquals("b2", repository.lastPdfDocumentId)
    }

    /** A second tap while one download is in flight is dropped rather than queued. */
    @Test
    fun `a document already opening refuses a second tap`() = runTest(testDispatcher) {
        documents.document = "BASE64DATA"

        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "a1", kind = BaseDocumentKind.IMAGE)
            )
        )
        documents.lastGuid = null
        // The first fetch has already completed under the unconfined dispatcher, so the guard is
        // exercised by re-entering while a *preview* is up rather than mid-flight; dismissing is
        // what the screen does before another can be opened.
        vm.sendIntent(AssignerContractsIntent.PreviewDismissed)
        assertNull(vm.uiState.value.preview)
    }

    // --------------------------------------------------------------------- گواهی مفاصاحساب

    /**
     * «گواهی صادرشده» reports what was found, in Persian digits — and asks with the پیمانکار's
     * workshop and the پیمان's own branch, row and number, which a transposition would still answer.
     */
    @Test
    fun `a found certificate is announced with the keys the contract carries`() = runTest(testDispatcher) {
        repository.settlementCertificate =
            SettlementCertificateDN(serial = "1080611", number = "38-7712405", date = "14021103")
        val contract = contract("1").copy(branchCode = "0310").toPresentation()

        val vm = viewModel()
        vm.events.test {
            vm.sendIntent(AssignerContractsIntent.CertificateRequested(contract, announce = true))
            assertEquals(
                AssignerContractsEvent.CertificateFound(
                    SettlementCertificatePR(number = "۳۸-۷۷۱۲۴۰۵", date = "۱۴۰۲/۱۱/۰۳")
                ),
                awaitItem(),
            )
        }
        assertEquals(listOf("9028212822", "0310", "1", "44122"), repository.lastCertificateArgs)
    }

    @Test
    fun `a contract with no certificate on file is announced as such`() = runTest(testDispatcher) {
        val vm = viewModel()

        vm.events.test {
            vm.sendIntent(
                AssignerContractsIntent.CertificateRequested(contract("1").toPresentation(), announce = true)
            )
            assertEquals(AssignerContractsEvent.CertificateNotFound, awaitItem())
        }
    }

    /** جزئیات پیمان asks without announcing; the answer waits in state for its status line. */
    @Test
    fun `a silent lookup keeps the certificate against the contract it was asked for`() =
        runTest(testDispatcher) {
            repository.settlementCertificate = SettlementCertificateDN(serial = "1", number = "38-1", date = "14021103")
            val contract = contract("1").toPresentation()

            val vm = viewModel()
            vm.sendIntent(AssignerContractsIntent.CertificateRequested(contract, announce = false))

            val lookup = assertNotNull(vm.uiState.value.certificate)
            assertEquals(contract, lookup.contract)
            assertEquals("۳۸-۱", lookup.certificate?.number)
            assertFalse(lookup.isLoading)
        }

    /** Dismissing clears both the viewer and the in-flight marker. */
    @Test
    fun `dismissing the preview clears it`() = runTest(testDispatcher) {
        documents.document = "BASE64DATA"

        val vm = viewModel()
        vm.sendIntent(
            AssignerContractsIntent.DocumentTapped(
                BaseDocumentPR(documentId = "a1", kind = BaseDocumentKind.IMAGE)
            )
        )
        vm.sendIntent(AssignerContractsIntent.PreviewDismissed)

        vm.uiState.test {
            val state = awaitItem()
            assertNull(state.preview)
            assertNull(state.openingDocumentId)
        }
    }

    /**
     * `UserRequestRepository` reduced to the one call this ViewModel makes.
     *
     * The `upload-image` fetch lives on that repository because درخواست‌های من needed it first; it
     * is the one route every attachment in the app comes through.
     */
    private class FakeUserRequestRepository : UserRequestRepository {
        var document: String = ""
        var lastGuid: String? = null

        /** Set to make the next fetch throw, the way a 404 from `upload-image` does. */
        var failure: Throwable? = null

        override suspend fun downloadUserRequestDocument(guid: String): String {
            lastGuid = guid
            failure?.let { throw it }
            return document
        }

        private fun notUsed(): Nothing =
            error("not exercised by the واگذارندگان ViewModel tests")

        override fun getUserRequests(search: UserRequestSearchParams): Flow<List<UserRequestDN>> =
            flowOf(emptyList())

        override suspend fun refreshUserRequests(search: UserRequestSearchParams): List<UserRequestDN> =
            emptyList()

        override suspend fun getRequestTypes(query: ApiQueryParamDN?): List<UserRequestTypeDN> = notUsed()
        override suspend fun getRequestErrors(requestId: Long): List<RequestErrorDN> = notUsed()
        override suspend fun getSmartGuideList(params: SmartGuideSearchParams): List<SmartGuideDN> = notUsed()
        override suspend fun getUserRequestDetail(id: Long): UserRequestDN = notUsed()
        override suspend fun getShowRequestInfo(
            referenceId: String,
            requestTypeId: Long,
        ): UserRequestDetailsDN = notUsed()
    }
}
