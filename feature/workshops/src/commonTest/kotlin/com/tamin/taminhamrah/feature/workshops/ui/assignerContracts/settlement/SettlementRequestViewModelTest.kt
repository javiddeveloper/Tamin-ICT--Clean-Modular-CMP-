package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement

import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.AMOUNT
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.AMOUNT1
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.AMOUNT2
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.AMOUNT3
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.AMOUNT4
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.CURRENCY_AMOUNT
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.CURRENCY_IN_RIAL
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.DOCUMENTS
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.END_DATE
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.LETTER_DATE
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.LETTER_NUMBER
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.START_DATE
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.SUBCONTRACTOR
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.SUBJECT_IMAGE
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementField.TEXT1
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.AddDocument
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.AddSubjectImage
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.DateChanged
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.FieldChanged
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.Next
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.StepSelected
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement.SettlementRequestIntent.SubcontractorChanged
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
import taminx.core.core_ui.settlement_err_build_sum
import taminx.core.core_ui.settlement_err_date_order
import taminx.core.core_ui.settlement_err_dates_required
import taminx.core.core_ui.settlement_err_drivers
import taminx.core.core_ui.settlement_err_letter_number_invalid
import taminx.core.core_ui.settlement_err_letter_number_required
import taminx.core.core_ui.settlement_err_subject_required
import taminx.core.core_ui.settlement_err_terms_incomplete
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * درخواست مفاصاحساب — the design's four steps, the old app's rules on each, and the request they add
 * up to.
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

    /** The first step only reads the پیمان back, so there is nothing on it to refuse. */
    @Test
    fun `the contract step lets the user straight on`() = runTest(testDispatcher) {
        val viewModel = opened()

        viewModel.sendIntent(Next)

        assertEquals(SettlementStep.LETTER, viewModel.uiState.value.step)
        assertTrue(viewModel.uiState.value.errors.isEmpty())
    }

    /** «پیمانکار جزء» has no default answer, so leaving it alone is one of the missing fields. */
    @Test
    fun `the letter step names every missing field and stays put`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachLetter()

        viewModel.sendIntent(Next)

        val state = viewModel.uiState.value
        assertEquals(SettlementStep.LETTER, state.step)
        assertEquals(
            setOf(LETTER_NUMBER, LETTER_DATE, START_DATE, END_DATE, AMOUNT, SUBCONTRACTOR),
            state.errors.keys,
        )
    }

    /** Three rules the old app only printed: each one now keeps the user on the step. */
    @Test
    fun `a short letter number, a reversed range and a currency without rials are refused`() =
        runTest(testDispatcher) {
            val viewModel = opened()
            viewModel.reachLetter()
            viewModel.fillLetterStep()
            viewModel.sendIntent(FieldChanged(LETTER_NUMBER, "1234"))
            viewModel.sendIntent(DateChanged(END_DATE, SettlementDate(1401, 1, 1)))
            viewModel.sendIntent(FieldChanged(CURRENCY_AMOUNT, "500"))

            viewModel.sendIntent(Next)

            val state = viewModel.uiState.value
            assertEquals(SettlementStep.LETTER, state.step)
            assertEquals(setOf(LETTER_NUMBER, END_DATE, CURRENCY_IN_RIAL), state.errors.keys)
            assertEquals(Res.string.settlement_err_date_order, state.errors[END_DATE])
        }

    /** The answer decides whether «مستندات لیست فهرست» is offered, and it goes last, as the design lists it. */
    @Test
    fun `answering the subcontractor question clears its error and decides the document types`() =
        runTest(testDispatcher) {
            val viewModel = opened()
            viewModel.reachLetter()
            viewModel.sendIntent(Next)
            assertTrue(SUBCONTRACTOR in viewModel.uiState.value.errors)

            viewModel.sendIntent(SubcontractorChanged(true))
            assertFalse(SUBCONTRACTOR in viewModel.uiState.value.errors)
            assertEquals(listOf("1", "3", "4", "2"), viewModel.uiState.value.documentTypes.map { it.code })

            viewModel.sendIntent(SubcontractorChanged(false))
            assertEquals(listOf("1", "3", "4"), viewModel.uiState.value.documentTypes.map { it.code })
        }

    @Test
    fun `a filled letter step moves on, and the documents step wants a file`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachLetter()
        viewModel.fillLetterStep()

        viewModel.sendIntent(Next)
        assertEquals(SettlementStep.DOCUMENTS, viewModel.uiState.value.step)
        assertTrue(viewModel.uiState.value.errors.isEmpty())

        viewModel.sendIntent(Next)
        assertEquals(SettlementStep.DOCUMENTS, viewModel.uiState.value.step)
        assertTrue(DOCUMENTS in viewModel.uiState.value.errors)
    }

    /** The progress bar goes back to a passed step; forward stays behind the step's own checks. */
    @Test
    fun `a passed step can be tapped back to and a later one cannot`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachLetter()

        viewModel.sendIntent(StepSelected(SettlementStep.TERMS))
        assertEquals(SettlementStep.LETTER, viewModel.uiState.value.step)

        viewModel.sendIntent(StepSelected(SettlementStep.CONTRACT))
        assertEquals(SettlementStep.CONTRACT, viewModel.uiState.value.step)
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
        viewModel.sendIntent(SubjectSelected(BUILD_COSTS_SUBJECT))
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

    /** Subject 11's four costs are one budget: together they may not pass the gross amount. */
    @Test
    fun `building costs that add up past the gross amount are refused`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachTerms(amount = "1000")
        viewModel.sendIntent(SubjectSelected(BUILD_COSTS_SUBJECT))
        viewModel.sendIntent(FieldChanged(AMOUNT1, "400"))
        viewModel.sendIntent(FieldChanged(AMOUNT2, "400"))
        viewModel.sendIntent(FieldChanged(AMOUNT3, "300"))
        viewModel.sendIntent(FieldChanged(AMOUNT4, "0"))

        viewModel.sendIntent(Next)

        assertEquals(Res.string.settlement_err_build_sum, viewModel.uiState.value.errors[AMOUNT1])
        assertNull(repository.lastSettlementRequest)
    }

    /** Subjects 01 and 29 file an image of their conditions; without it the request does not go. */
    @Test
    fun `foreign equipment is refused without its image and sent with it`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachTerms(amount = "1000000")
        viewModel.sendIntent(SubjectSelected(TaminOptionSheetItem("29", "تجهیزات خارجی")))
        viewModel.sendIntent(FieldChanged(AMOUNT1, "100"))
        viewModel.sendIntent(FieldChanged(AMOUNT2, "4000"))

        viewModel.sendIntent(Next)
        assertTrue(SUBJECT_IMAGE in viewModel.uiState.value.errors)
        assertNull(repository.lastSettlementRequest)

        viewModel.sendIntent(AddSubjectImage("terms.jpg", byteArrayOf(1, 2, 3)))
        assertFalse(SUBJECT_IMAGE in viewModel.uiState.value.errors)
        viewModel.sendIntent(Next)

        assertEquals(IMAGE_GUID, assertNotNull(repository.lastSettlementRequest).subjectImageGuid)
    }

    /** The fields only turn red; the footer names the first problem, in the order the design checks. */
    @Test
    fun `the letter banner names the first problem in the design's order`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachLetter()

        viewModel.sendIntent(Next)
        assertEquals(Res.string.settlement_err_letter_number_required, viewModel.uiState.value.bannerError())

        viewModel.sendIntent(FieldChanged(LETTER_NUMBER, "1234"))
        viewModel.sendIntent(Next)
        assertEquals(Res.string.settlement_err_letter_number_invalid, viewModel.uiState.value.bannerError())

        viewModel.sendIntent(FieldChanged(LETTER_NUMBER, "12345"))
        viewModel.sendIntent(Next)
        assertEquals(Res.string.settlement_err_dates_required, viewModel.uiState.value.bannerError())

        viewModel.fillLetterStep()
        viewModel.sendIntent(DateChanged(END_DATE, SettlementDate(1401, 1, 1)))
        viewModel.sendIntent(Next)
        assertEquals(Res.string.settlement_err_date_order, viewModel.uiState.value.bannerError())
    }

    @Test
    fun `the terms banner asks for the subject, then every field, then names the guard`() =
        runTest(testDispatcher) {
            val viewModel = opened()
            viewModel.reachTerms(amount = "1000")

            viewModel.sendIntent(Next)
            assertEquals(Res.string.settlement_err_subject_required, viewModel.uiState.value.bannerError())

            viewModel.sendIntent(SubjectSelected(BUILD_COSTS_SUBJECT))
            viewModel.sendIntent(Next)
            assertEquals(Res.string.settlement_err_terms_incomplete, viewModel.uiState.value.bannerError())

            viewModel.sendIntent(FieldChanged(AMOUNT1, "900"))
            viewModel.sendIntent(FieldChanged(AMOUNT2, "900"))
            viewModel.sendIntent(FieldChanged(AMOUNT3, "0"))
            viewModel.sendIntent(FieldChanged(AMOUNT4, "0"))
            viewModel.sendIntent(Next)
            assertEquals(Res.string.settlement_err_build_sum, viewModel.uiState.value.bannerError())
        }

    @Test
    fun `submitting sends every step's answers and confirms it`() = runTest(testDispatcher) {
        val viewModel = opened()
        viewModel.reachTerms(amount = "1000000")
        viewModel.sendIntent(SubjectSelected(DRIVERS_SUBJECT))
        viewModel.sendIntent(FieldChanged(AMOUNT1, "250000"))

        viewModel.sendIntent(Next)

        assertTrue(viewModel.uiState.value.isSubmitted)
        assertFalse(viewModel.uiState.value.isSubmitting)
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
        assertFalse(sent.hasSubcontractor)
        // What is left once the drivers' share is out, computed at the moment of sending.
        assertEquals("750000", sent.subjectAmount2)
        assertEquals(listOf("pdf-id"), sent.documents.map { it.documentId })
        assertTrue(sent.documents.single().isPdf)
    }

    private fun SettlementRequestViewModel.reachLetter() {
        sendIntent(Next)
        assertEquals(SettlementStep.LETTER, uiState.value.step)
    }

    private fun SettlementRequestViewModel.fillLetterStep(amount: String = "1000000") {
        sendIntent(FieldChanged(LETTER_NUMBER, "12345"))
        sendIntent(DateChanged(LETTER_DATE, SettlementDate(1403, 1, 1)))
        sendIntent(DateChanged(START_DATE, SettlementDate(1402, 1, 1)))
        sendIntent(DateChanged(END_DATE, SettlementDate(1402, 12, 1)))
        sendIntent(FieldChanged(AMOUNT, amount))
        sendIntent(SubcontractorChanged(false))
    }

    private fun SettlementRequestViewModel.reachTerms(amount: String) {
        reachLetter()
        fillLetterStep(amount)
        sendIntent(Next)
        sendIntent(AddDocument("final.pdf", PDF_BYTES, "4"))
        sendIntent(Next)
        assertEquals(SettlementStep.TERMS, uiState.value.step)
    }

    private companion object {
        const val IMAGE_GUID = "image-guid"
        val PDF_BYTES = "%PDF-1.7 settlement".encodeToByteArray()
        val DRIVERS_SUBJECT = TaminOptionSheetItem("04", "حمل و نقل")
        val BUILD_COSTS_SUBJECT = TaminOptionSheetItem("11", "ساخت و نصب")
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
