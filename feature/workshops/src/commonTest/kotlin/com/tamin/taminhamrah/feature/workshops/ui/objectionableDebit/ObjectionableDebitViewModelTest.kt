package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.model.ObjectionDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.DebitObjectionResultDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.CheckObjectionDeadlineUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitObjectionPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetObjectionableDebitsUseCase
import com.tamin.taminhamrah.useCases.workshops.SaveDebitObjectionUseCase
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
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * اعتراض به بدهی — one list, one action per row, and the action is not the same for every row.
 *
 * What is pinned here is that branch. A debt already objected to opens its filed objection; a debt
 * still open to objection is checked against its deadline first, and only a debt inside the window
 * reaches the form. Getting that wrong either hides a filing window that is still open or invites
 * someone to fill in a form the service will refuse.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObjectionableDebitViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ObjectionableDebitViewModel(
        GetObjectionableDebitsUseCase(repository),
        CheckObjectionDeadlineUseCase(repository),
        GetDebitObjectionPdfUseCase(repository),
        WorkshopAttachmentUploader { UPLOADED_GUID },
        SaveDebitObjectionUseCase(repository),
    )

    private fun open(viewModel: ObjectionableDebitViewModel) =
        viewModel.sendIntent(ObjectionableDebitIntent.Open(WORKSHOP_ID, BRANCH_CODE))

    @Test
    fun `opening loads the objectionable debts of that workshop`() = runTest(testDispatcher) {
        repository.objectionableDebits = debts(count = 2)

        val viewModel = viewModel()
        open(viewModel)

        assertEquals(2, viewModel.uiState.value.list.items.size)
        assertEquals(WORKSHOP_ID, viewModel.uiState.value.workshopId)
    }

    @Test
    fun `reopening the same workshop does not refetch`() = runTest(testDispatcher) {
        repository.objectionableDebits = debts(count = 1)

        val viewModel = viewModel()
        open(viewModel)
        repository.objectionableDebits = debts(count = 5)
        open(viewModel)

        assertEquals(1, viewModel.uiState.value.list.items.size)
    }

    /**
     * A debt whose objection was already filed opens that objection rather than a new form — the
     * row's single action means different things depending on the debt.
     */
    @Test
    fun `a debt already objected to opens its filed objection`() = runTest(testDispatcher) {
        repository.objectionableDebits = PagedListDN(items = listOf(filedDebt()), total = 1)

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(
            ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
        )

        assertNotNull(viewModel.uiState.value.viewerPdf, "the filed objection should be shown")
        assertNull(viewModel.uiState.value.form, "no new form is offered for a filed objection")
    }

    /** Without a sequence number there is no objection to fetch, so it says so instead of failing. */
    @Test
    fun `a filed objection with no sequence number is reported`() = runTest(testDispatcher) {
        repository.objectionableDebits =
            PagedListDN(items = listOf(filedDebt(seqNo = null)), total = 1)

        val viewModel = viewModel()
        open(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(
                ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
            )
            assertTrue(awaitItem() is ObjectionableDebitEvent.ShowMessage)
        }
        assertNull(viewModel.uiState.value.viewerPdf)
    }

    /** Inside the filing window, the row opens the form. */
    @Test
    fun `an objectionable debt still in time opens the form`() = runTest(testDispatcher) {
        repository.objectionableDebits = PagedListDN(items = listOf(estimateDebt()), total = 1)
        repository.objectionElapsedDays = WITHIN_WINDOW

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(
            ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
        )

        assertNotNull(viewModel.uiState.value.form, "a debt inside its window should reach the form")
        assertNull(viewModel.uiState.value.checkingDebitNumber, "the row must stop showing progress")
    }

    /**
     * Past the window the form must not open at all — letting someone fill it in only to have the
     * service refuse it is the failure this check exists to prevent.
     */
    @Test
    fun `an objectionable debt out of time is refused before the form`() = runTest(testDispatcher) {
        repository.objectionableDebits = PagedListDN(items = listOf(estimateDebt()), total = 1)
        repository.objectionElapsedDays = PAST_WINDOW

        val viewModel = viewModel()
        open(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(
                ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
            )
            assertTrue(awaitItem() is ObjectionableDebitEvent.ShowMessage)
        }
        assertNull(viewModel.uiState.value.form, "no form for a debt past its filing window")
    }

    /**
     * The tick is not the answer: submit asks once more, and nothing is filed until it is answered.
     *
     * The old app put a modal between the تعهدنامه checkbox and the API call for a reason — a
     * filed objection cannot be withdrawn.
     */
    @Test
    fun `a complete form asks before it files`() = runTest(testDispatcher) {
        val viewModel = readyToSubmit()

        viewModel.sendIntent(ObjectionableDebitIntent.FormSubmit)

        assertEquals(viewModel.uiState.value.form?.isConfirmVisible, true, "it must ask first")
        assertNull(repository.lastDebitObjectionRequest, "nothing may be filed before the answer")
    }

    @Test
    fun `answering the question files the objection`() = runTest(testDispatcher) {
        val viewModel = readyToSubmit()

        viewModel.sendIntent(ObjectionableDebitIntent.FormSubmit)
        viewModel.sendIntent(ObjectionableDebitIntent.FormConfirmAccepted)

        assertNotNull(repository.lastDebitObjectionRequest, "confirming must file it")
        assertNull(viewModel.uiState.value.form, "the form closes once it is filed")
    }

    /** The design answers a filed objection with a dialog carrying its tracking code, not a toast. */
    @Test
    fun `a filed objection keeps its tracking code on screen until dismissed`() = runTest(testDispatcher) {
        repository.objectionResult = DebitObjectionResultDN(referenceCode = "2740913")
        val viewModel = readyToSubmit()

        viewModel.sendIntent(ObjectionableDebitIntent.FormSubmit)
        viewModel.sendIntent(ObjectionableDebitIntent.FormConfirmAccepted)
        assertEquals("2740913", viewModel.uiState.value.filedReferenceCode)

        viewModel.sendIntent(ObjectionableDebitIntent.DismissFiled)
        assertNull(viewModel.uiState.value.filedReferenceCode)
    }

    /** A failed PDF used to go into the list's error state, which is not drawn while rows are up. */
    @Test
    fun `a failed filed-objection pdf says why and leaves the list as it was`() = runTest(testDispatcher) {
        repository.objectionableDebits = PagedListDN(items = listOf(filedDebt()), total = 1)
        val viewModel = viewModel()
        open(viewModel)
        repository.error = TaminApiException(title = "فایل یافت نشد")

        viewModel.events.test {
            viewModel.sendIntent(
                ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
            )

            assertEquals(ObjectionableDebitEvent.ShowServerMessage("فایل یافت نشد"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(viewModel.uiState.value.list.error)
        assertEquals(1, viewModel.uiState.value.list.items.size)
        assertFalse(viewModel.uiState.value.isBusy)
    }

    @Test
    fun `declining the question files nothing and keeps the form`() = runTest(testDispatcher) {
        val viewModel = readyToSubmit()

        viewModel.sendIntent(ObjectionableDebitIntent.FormSubmit)
        viewModel.sendIntent(ObjectionableDebitIntent.FormConfirmDismissed)

        assertNull(repository.lastDebitObjectionRequest, "declining must file nothing")
        assertNotNull(viewModel.uiState.value.form, "the form stays, filled in as it was")
        assertNotEquals(viewModel.uiState.value.form?.isConfirmVisible, true)
    }

    /** An incomplete form is refused where it always was — the question is never reached. */
    @Test
    fun `an incomplete form is refused instead of asked about`() = runTest(testDispatcher) {
        val viewModel = openForm()

        viewModel.sendIntent(ObjectionableDebitIntent.FormSubmit)

        val form = assertNotNull(viewModel.uiState.value.form)
        assertTrue(form.hasTriedSubmit, "the form must say what is missing")
        assertFalse(form.isConfirmVisible, "and must not ask about a form it would refuse")
        assertNull(repository.lastDebitObjectionRequest)
    }

    /** A debt inside its window, with its form open. */
    private fun openForm(): ObjectionableDebitViewModel {
        repository.objectionableDebits = PagedListDN(items = listOf(estimateDebt()), total = 1)
        repository.objectionElapsedDays = WITHIN_WINDOW

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(
            ObjectionableDebitIntent.RowAction(viewModel.uiState.value.list.items[0])
        )
        return viewModel
    }

    /** …and filled in far enough that every rule passes. */
    private fun readyToSubmit(): ObjectionableDebitViewModel {
        val viewModel = openForm()
        viewModel.sendIntent(
            ObjectionableDebitIntent.FormAddDocument(
                fileName = "evidence.jpg",
                bytes = ByteArray(2048),
                typeCode = ObjectionDocumentTypes.first().code,
            )
        )
        viewModel.sendIntent(ObjectionableDebitIntent.FormConfirmedChanged(isConfirmed = true))
        return viewModel
    }

    private fun debts(count: Int) = PagedListDN(
        items = List(count) { estimateDebt(debitNumber = "$RAW_DEBIT_NUMBER$it") },
        total = count,
    )

    /** برآوردی and objectionable — the row that leads to the form. */
    private fun estimateDebt(debitNumber: String = RAW_DEBIT_NUMBER) = WorkShopDebtDN(
        debitNumber = debitNumber,
        orderRecipeDate = "14050525",
        debitStepCode = STEP_ESTIMATE,
        debitStatCode = STAT_OBJECTIONABLE,
        debitAmount = 14_203_311L,
        debitRemain = 14_203_311L,
    )

    /** Already objected to: [WorkShopDebtDN.seqNo] is what makes the kind FILED. */
    private fun filedDebt(seqNo: Long? = 42L) = WorkShopDebtDN(
        debitNumber = RAW_DEBIT_NUMBER,
        orderRecipeDate = "14050525",
        seqNo = seqNo,
    )

    private companion object {
        const val WORKSHOP_ID = "9028218513"
        const val BRANCH_CODE = "14"
        const val RAW_DEBIT_NUMBER = "6310030089235"
        const val UPLOADED_GUID = "a-guid"

        /** The codes [WorkShopDebtDN.objectionKind] reads to decide a row is still objectionable. */
        const val STEP_ESTIMATE = "01"
        const val STAT_OBJECTIONABLE = "03"

        /** ESTIMATE allows 31 days, so these sit either side of it. */
        const val WITHIN_WINDOW = 10
        const val PAST_WINDOW = 99
    }
}
