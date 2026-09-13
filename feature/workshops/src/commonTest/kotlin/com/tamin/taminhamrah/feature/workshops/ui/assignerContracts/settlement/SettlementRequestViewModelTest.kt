package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.AMOUNT
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.AMOUNT1
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.CURRENCY_AMOUNT
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.CURRENCY_IN_RIAL
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.DOCUMENTS
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.END_DATE
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.LETTER_DATE
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.LETTER_NUMBER
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.START_DATE
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.TEXT1
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.AddDocument
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.DateChanged
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.FieldChanged
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.Next
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.SubjectSelected
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.model.workshop.SettlementSubjectDN
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminOptionSheetItem
import com.tamin.taminhamrah.useCases.workshops.GetSettlementSubjectsUseCase
import com.tamin.taminhamrah.useCases.workshops.SubmitSettlementRequestUseCase
import com.tamin.taminhamrah.useCases.workshops.UploadSettlementPdfUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import taminx.core.core_ui.Res
import taminx.core.core_ui.settlement_err_date_order
import taminx.core.core_ui.settlement_err_drivers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * درخواست مفاصاحساب — the old app's rules, step by step, and the request they add up to.
 *
 * What is pinned is what would otherwise slip without a sound: a step that lets the user past with a
 * field the service needs, a PDF sent to the image route, conditions from one subject filed under
 * another, and a remainder computed from stale amounts.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettlementRequestViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository().apply {
            settlementSubjects = listOf(
                SettlementSubjectDN(code = "04", description = "حمل و نقل"),
                SettlementSubjectDN(code = "11", description = "ساخت و نصب"),
            )
        }
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun opened(): SettlementRequestViewModel = SettlementRequestViewModel(
        GetSettlementSubjectsUseCase(repository),
        WorkshopAttachmentUploader { IMAGE_GUID },
        UploadSettlementPdfUseCase(repository),
        SubmitSettlementRequestUseCase(repository),
    ).also { it.sendIntent(SettlementRequestIntent.Open(CONTRACT)) }

    @Test
    fun `opening fetches the subjects the picker offers`() = runTest(testDispatcher) {
        val viewModel = opened()

        assertEquals(listOf("04", "11"), viewModel.uiState.value.subjects.map { it.id })
    }

    @Test
    fun `the first step names every missing field and stays put`() = runTest(testDispatcher) {
        val viewModel = opened()

        viewModel.sendIntent(Next)

        val state = viewModel.uiState.value
        assertEquals(SettlementStep.CONTRACT, state.step)
        assertEquals(setOf(LETTER_NUMBER, LETTER_DATE, START_DATE, END_DATE, AMOUNT), state.errors.keys)
    }

    /** Three rules the old app only printed: each one now keeps the user on the step. */
    @Test
    fun `a short letter number, a reversed range and a currency without rials are refused`() =
        runTest(testDispatcher) {
            val viewModel = opened()
            viewModel.fillContractStep()
            viewModel.sendIntent(FieldChanged(LETTER_NUMBER, "1234"))
            viewModel.sendIntent(DateChanged(END_DATE, SettlementDate(1401, 1, 1)))
            viewModel.sendIntent(FieldChanged(CURRENCY_AMOUNT, "500"))

            viewModel.sendIntent(Next)

            val state = viewModel.uiState.value
            assertEquals(SettlementStep.CONTRACT, state.step)
            assertEquals(setOf(LETTER_NUMBER, END_DATE, CURRENCY_IN_RIAL), state.errors.keys)
            assertEquals(Res.string.settlement_err_date_order, state.errors[END_DATE])
        }

    @Test
    fun `a filled first step moves on, and the documents step wants a file`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.fillContractStep()

        viewModel.sendIntent(Next)
        assertEquals(SettlementStep.DOCUMENTS, viewModel.uiState.value.step)
        assertTrue(viewModel.uiState.value.errors.isEmpty())

        viewModel.sendIntent(Next)
        assertEquals(SettlementStep.DOCUMENTS, viewModel.uiState.value.step)
        assertTrue(DOCUMENTS in viewModel.uiState.value.errors)
    }

    /** Decided by the bytes: the PDF route is the settlement service's own, the image route is shared. */
    @Test
    fun `a pdf goes up the settlement route and an image through upload-image`() = runTest(testDispatcher) {
        val viewModel = opened()

        viewModel.sendIntent(AddDocument("final.pdf", PDF_BYTES, "4"))
        viewModel.sendIntent(AddDocument("letter.jpg", byteArrayOf(1, 2, 3), "1"))

        val (pdf, image) = viewModel.uiState.value.attachments
        assertTrue(pdf.isPdf)
        assertEquals("pdf-id", pdf.guid)
        assertEquals("final.pdf", repository.lastSettlementPdfName)
        assertFalse(image.isPdf)
        assertEquals(IMAGE_GUID, image.guid)
    }

    @Test
    fun `choosing another subject clears the conditions typed for the last one`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.sendIntent(SubjectSelected(TaminOptionSheetItem("11", "ساخت و نصب")))
        viewModel.sendIntent(FieldChanged(AMOUNT1, "900"))

        viewModel.sendIntent(SubjectSelected(DRIVERS_SUBJECT))

        assertEquals(SettlementTerms(), viewModel.uiState.value.terms)
    }

    @Test
    fun `the mechanical share is capped at one hundred and fills in the manual share`() =
        runTest(testDispatcher) {
            val viewModel = opened()
            viewModel.sendIntent(SubjectSelected(TaminOptionSheetItem("03", "مکانیکی")))

            viewModel.sendIntent(FieldChanged(TEXT1, "150"))
            assertEquals("100", viewModel.uiState.value.terms.text1)
            assertEquals("0", viewModel.uiState.value.terms.text2)

            viewModel.sendIntent(FieldChanged(TEXT1, "35"))
            assertEquals("65", viewModel.uiState.value.terms.text2)
        }

    @Test
    fun `drivers work that eats the whole gross amount is refused`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachTerms(amount = "1000")
        viewModel.sendIntent(SubjectSelected(DRIVERS_SUBJECT))
        viewModel.sendIntent(FieldChanged(AMOUNT1, "1000"))

        viewModel.sendIntent(Next)

        assertEquals(Res.string.settlement_err_drivers, viewModel.uiState.value.errors[AMOUNT1])
        assertNull(repository.lastSettlementRequest)
    }

    @Test
    fun `submitting sends every step's answers and reports it`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachTerms(amount = "1000000")
        viewModel.sendIntent(SubjectSelected(DRIVERS_SUBJECT))
        viewModel.sendIntent(FieldChanged(AMOUNT1, "250000"))

        viewModel.events.test {
            viewModel.sendIntent(Next)
            assertEquals(SettlementRequestEvent.Submitted, awaitItem())
        }

        val sent = assertNotNull(repository.lastSettlementRequest)
        // The four keys come off the list row; the branch is the پیمان's own, not the پیمانکار's.
        assertEquals("9028212822", sent.workshopId)
        assertEquals("1", sent.contractRow)
        assertEquals("0310", sent.branchCode)
        assertEquals("3", sent.contractSequence)
        assertEquals("04", sent.subjectCode)
        // 1403/01/01 is Nowruz 2024.
        assertEquals("2024-03-20", sent.letterDate)
        assertEquals(1_000_000L, sent.amount)
        // What is left once the drivers' share is out, computed at the moment of sending.
        assertEquals("750000", sent.subjectAmount2)
        assertEquals(listOf("pdf-id"), sent.documents.map { it.documentId })
        assertTrue(sent.documents.single().isPdf)
    }

    private fun SettlementRequestViewModel.fillContractStep(amount: String = "1000000") {
        sendIntent(FieldChanged(LETTER_NUMBER, "12345"))
        sendIntent(DateChanged(LETTER_DATE, SettlementDate(1403, 1, 1)))
        sendIntent(DateChanged(START_DATE, SettlementDate(1402, 1, 1)))
        sendIntent(DateChanged(END_DATE, SettlementDate(1402, 12, 1)))
        sendIntent(FieldChanged(AMOUNT, amount))
    }

    private fun SettlementRequestViewModel.reachTerms(amount: String) {
        fillContractStep(amount)
        sendIntent(Next)
        sendIntent(AddDocument("final.pdf", PDF_BYTES, "4"))
        sendIntent(Next)
        assertEquals(SettlementStep.TERMS, uiState.value.step)
    }

    private companion object {
        const val IMAGE_GUID = "image-guid"
        val PDF_BYTES = "%PDF-1.7 settlement".encodeToByteArray()
        val DRIVERS_SUBJECT = TaminOptionSheetItem("04", "حمل و نقل")
        val CONTRACT = AssignerContractDN(
            contractRow = "1",
            contractSequence = "3",
            branchCode = "0310",
            employer = AssignerPartyDN(
                workshopId = "9028212822",
                workshopName = "دبستان کارن",
                branchCode = "0210",
            ),
        ).toPresentation()
    }
}
