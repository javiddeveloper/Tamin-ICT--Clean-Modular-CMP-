package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.feature.workshops.ui.model.RegistrationDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentDownloader
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopDocumentType
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersEvent.ShowMessage
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersEvent.ShowServerMessage
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.ApplySearch
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.Confirm
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.Delete
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.DraftChanged
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.Edit
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormAddDocument
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormConfirmedChanged
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormDismissed
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormFieldChanged
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormNext
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormOptionPicked
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormPickerLoadMore
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormPickerOpened
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormPickerQueryChanged
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.FormPrev
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.Open
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.PendingActionAccepted
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersIntent.PendingActionDismissed
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.DocumentFileDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.GirlSurvivorReportParamsDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationResultDN
import com.tamin.taminhamrah.model.workshop.NewMemberRequestStatus
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.common.GetCityUseCase
import com.tamin.taminhamrah.useCases.common.GetJobTitlePageUseCase
import com.tamin.taminhamrah.useCases.common.GetRegistrationDeclarationFormUseCase
import com.tamin.taminhamrah.useCases.personal.GetInsuredRegistrationDocListUseCase
import com.tamin.taminhamrah.useCases.personal.PutInsuredRegistrationDocListUseCase
import com.tamin.taminhamrah.useCases.workshops.CheckNewMemberIsNewUseCase
import com.tamin.taminhamrah.useCases.workshops.ConfirmRecentlyAddedMemberUseCase
import com.tamin.taminhamrah.useCases.workshops.CreateNewMemberRegistrationUseCase
import com.tamin.taminhamrah.useCases.workshops.DeleteRecentlyAddedMemberUseCase
import com.tamin.taminhamrah.useCases.workshops.GetRecentlyAddedMembersUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import taminx.core.core_ui.Res
import taminx.core.core_ui.abs_doc_unknown
import taminx.core.core_ui.abs_form_err_already_known
import taminx.core.core_ui.error_image_duplicate
import taminx.core.core_ui.new_member_cannot_edit
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * نام‌نویسی غیرحضوری بیمه‌شده — what reaches the service, and when.
 *
 * Each rule pinned here costs a real record when it breaks: a person goes on file once, at step
 * two, and is updated from then on; someone already known is refused before their documents are
 * gathered; each document type takes one image; and a row is confirmed or deleted only once the
 * question has been answered.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopRecentlyAddedMembersViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var workshops: FakeWorkShopsRepository
    private lateinit var documents: FakeDocumentsRepository

    private lateinit var jobs: FakeJobsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        workshops = FakeWorkShopsRepository()
        documents = FakeDocumentsRepository()
        jobs = FakeJobsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    /** A view model already showing the workshop's list, as the screen leaves it. */
    private fun viewModel(
        uploader: WorkshopAttachmentUploader = WorkshopAttachmentUploader { UPLOADED_GUID },
        downloader: WorkshopAttachmentDownloader = WorkshopAttachmentDownloader { FILED_IMAGE },
    ): WorkshopRecentlyAddedMembersViewModel {
        val cities = FakeCitiesRepository()
        return WorkshopRecentlyAddedMembersViewModel(
            GetRecentlyAddedMembersUseCase(workshops),
            ConfirmRecentlyAddedMemberUseCase(workshops),
            DeleteRecentlyAddedMemberUseCase(workshops),
            CheckNewMemberIsNewUseCase(workshops),
            CreateNewMemberRegistrationUseCase(workshops),
            uploader,
            downloader,
            PutInsuredRegistrationDocListUseCase(documents),
            GetInsuredRegistrationDocListUseCase(documents),
            GetCitiesUseCase(cities),
            GetCityUseCase(cities),
            GetJobTitlePageUseCase(jobs),
            GetRegistrationDeclarationFormUseCase(jobs),
        ).also { it.sendIntent(Open(WORKSHOP_ID, BRANCH_CODE)) }
    }

    // ------------------------------------------------------------------ putting the person on file

    /** Step two is where the person goes on file, so a registration abandoned later can be resumed. */
    @Test
    fun `completing step two creates the person and moves on to the documents`() =
        runTest(testDispatcher) {
            workshops.registrationResult = NewMemberRegistrationResultDN(CREATED_PERSONAL_ID)
            val viewModel = viewModel()

            fillStepTwo(viewModel)
            assertNull(workshops.lastRegistrationRequest, "nothing is filed before step two is done")
            viewModel.sendIntent(FormNext)

            val request = assertNotNull(workshops.lastRegistrationRequest)
            assertNull(request.personalId, "a person not yet on file is created")
            assertEquals(WORKSHOP_ID, request.workshopId)
            val form = assertNotNull(viewModel.uiState.value.form)
            assertEquals(REGISTRATION_FORM_STEPS, form.step)
            assertEquals(CREATED_PERSONAL_ID, form.personalId)
        }

    @Test
    fun `a person already known is refused before anything is created`() = runTest(testDispatcher) {
        workshops.newMemberIsNew = false
        val viewModel = viewModel()
        fillStepTwo(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(FormNext)
            assertEquals(ShowMessage(Res.string.abs_form_err_already_known), awaitItem())
        }

        assertNull(workshops.lastRegistrationRequest)
        val form = assertNotNull(viewModel.uiState.value.form)
        assertEquals(2, form.step, "the form stays on step two")
        assertFalse(form.isSubmitting)
    }

    /**
     * Back on step two after saving, the person is updated under the id the create returned —
     * creating them again would leave two `employers` records.
     */
    @Test
    fun `saving step two again updates the person it created`() = runTest(testDispatcher) {
        workshops.registrationResult = NewMemberRegistrationResultDN(CREATED_PERSONAL_ID)
        val viewModel = viewModel()
        fillStepTwo(viewModel)
        viewModel.sendIntent(FormNext)

        // On file now, so the is-new gate must not be asked again: if it were, this would refuse.
        workshops.newMemberIsNew = false
        viewModel.sendIntent(FormPrev)
        viewModel.sendIntent(FormNext)

        assertEquals(CREATED_PERSONAL_ID, workshops.lastRegistrationRequest?.personalId)
        assertEquals(REGISTRATION_FORM_STEPS, viewModel.uiState.value.form?.step)
    }

    @Test
    fun `a re-opened draft is updated, not registered again`() = runTest(testDispatcher) {
        workshops.newMemberIsNew = false
        val viewModel = viewModel()

        viewModel.sendIntent(Edit(DRAFT))
        viewModel.sendIntent(FormNext)
        viewModel.sendIntent(FormNext)

        assertEquals(DRAFT_PERSONAL_ID, workshops.lastRegistrationRequest?.personalId)
    }

    /** The row carries codes; the form must show the names they stand for. */
    @Test
    fun `re-opening a draft turns its city and job codes back into names`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.sendIntent(Edit(DRAFT))

        val form = assertNotNull(viewModel.uiState.value.form)
        assertEquals(TEHRAN, form.birthCity)
        assertEquals(TEHRAN, form.issueCity)
        assertEquals(PROGRAMMER, form.job)
        assertEquals(DRAFT_PERSONAL_ID, form.personalId)
    }

    @Test
    fun `the last step files the documents against the saved person and closes the form`() =
        runTest(testDispatcher) {
            workshops.registrationResult = NewMemberRegistrationResultDN(CREATED_PERSONAL_ID)
            val viewModel = viewModel()
            fillStepTwo(viewModel)
            viewModel.sendIntent(FormNext)
            attach(viewModel, RegistrationDocumentTypes.first().code)
            viewModel.sendIntent(FormConfirmedChanged(isConfirmed = true))

            viewModel.sendIntent(FormNext)

            assertEquals(CREATED_PERSONAL_ID.toString(), documents.filedPersonalId)
            assertEquals(listOf(UPLOADED_GUID), documents.filedDocuments?.map { it.documentFile.id })
            assertNull(viewModel.uiState.value.form)
        }

    /** Closing the form once the person is on file brings their draft into the list. */
    @Test
    fun `closing a form whose person is on file reloads the list`() = runTest(testDispatcher) {
        workshops.registrationResult = NewMemberRegistrationResultDN(CREATED_PERSONAL_ID)
        val viewModel = viewModel()
        fillStepTwo(viewModel)
        viewModel.sendIntent(FormNext)
        workshops.recentlyAddedMembers = PagedListDN(
            items = listOf(WorkshopNewMemberDN(personalId = CREATED_PERSONAL_ID)),
            total = 1,
        )

        viewModel.sendIntent(FormDismissed)

        assertNull(viewModel.uiState.value.form)
        assertEquals(1, viewModel.uiState.value.list.items.size)
    }

    // ------------------------------------------------------------------ documents

    @Test
    fun `a document type already filed is refused before anything is uploaded`() =
        runTest(testDispatcher) {
            var uploads = 0
            val viewModel = viewModel(
                uploader = WorkshopAttachmentUploader {
                    uploads++
                    UPLOADED_GUID
                },
            )
            viewModel.sendIntent(Edit(WorkshopNewMemberPR()))
            val typeCode = RegistrationDocumentTypes.first().code
            attach(viewModel, typeCode)

            viewModel.events.test {
                attach(viewModel, typeCode)
                assertEquals(ShowMessage(Res.string.error_image_duplicate), awaitItem())
            }

            assertEquals(1, uploads)
            assertEquals(1, viewModel.uiState.value.form?.attachments?.size)
        }

    // ------------------------------------------------------------------ documents already on file

    /**
     * A draft re-opens with the documents filed against it, which the row does not carry: each
     * image read back by its guid and held as its upload would have been — a type the table does
     * not know included, since leaving it out would delete it on save.
     */
    @Test
    fun `re-opening a draft brings back the documents already on file`() = runTest(testDispatcher) {
        documents.onFile = listOf(
            filedDocument(ON_FILE_GUID, typeCode = "01"),
            filedDocument(UNKNOWN_TYPE_GUID, typeCode = "99"),
        )
        val viewModel = viewModel()

        viewModel.sendIntent(Edit(DRAFT))

        assertEquals(DRAFT_PERSONAL_ID.toString(), documents.readPersonalId)
        val form = assertNotNull(viewModel.uiState.value.form)
        assertEquals(
            listOf(
                WorkshopAttachment(
                    guid = ON_FILE_GUID,
                    type = RegistrationDocumentTypes.first(),
                    byteCount = FILED_IMAGE_BYTES,
                ),
                WorkshopAttachment(
                    guid = UNKNOWN_TYPE_GUID,
                    type = WorkshopDocumentType("99", Res.string.abs_doc_unknown),
                    byteCount = FILED_IMAGE_BYTES,
                ),
            ),
            form.attachments.toList(),
        )
    }

    /**
     * The document list is written back whole, so a re-opened draft's documents already on file
     * go back alongside the new ones — sending only the new ones deletes the rest.
     */
    @Test
    fun `filing a re-opened draft keeps the documents already on file`() = runTest(testDispatcher) {
        documents.onFile = listOf(filedDocument(ON_FILE_GUID, typeCode = "01"))
        val viewModel = viewModel()
        viewModel.sendIntent(Edit(DRAFT))
        viewModel.sendIntent(FormNext)
        viewModel.sendIntent(FormNext)
        attach(viewModel, typeCode = "02")
        viewModel.sendIntent(FormConfirmedChanged(isConfirmed = true))

        viewModel.sendIntent(FormNext)

        val filed = assertNotNull(documents.filedDocuments)
        assertEquals(listOf(ON_FILE_GUID, UPLOADED_GUID), filed.map { it.documentFile.id })
        assertEquals(listOf("01", "02"), filed.map { it.documentType })
    }

    /** A draft whose documents cannot be read stays shut, so nothing can overwrite them. */
    @Test
    fun `a draft whose documents cannot be read is not opened`() = runTest(testDispatcher) {
        documents.readError = IllegalStateException("documents unavailable")
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.sendIntent(Edit(DRAFT))
            assertTrue(awaitItem() is ShowServerMessage)
        }

        assertNull(viewModel.uiState.value.form)
        assertNull(viewModel.uiState.value.openingPersonalId, "a failed read must stop the progress")
    }

    /** Listing is not enough: an image that cannot be read back keeps the form shut too. */
    @Test
    fun `a draft whose filed image cannot be read back is not opened`() = runTest(testDispatcher) {
        documents.onFile = listOf(filedDocument(ON_FILE_GUID, typeCode = "01"))
        val viewModel = viewModel(
            downloader = WorkshopAttachmentDownloader { error("image unavailable") },
        )

        viewModel.events.test {
            viewModel.sendIntent(Edit(DRAFT))
            assertTrue(awaitItem() is ShowServerMessage)
        }

        assertNull(viewModel.uiState.value.form)
    }

    /**
     * While a draft's documents load its «ویرایش» shows progress, and a second tap reads nothing
     * again; once they are in, the progress gives way to the form.
     */
    @Test
    fun `a draft shows it is opening until its documents are in`() = runTest(testDispatcher) {
        val gate = CompletableDeferred<Unit>()
        documents.readGate = gate
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.sendIntent(Edit(DRAFT))
            val opening = awaitState { it.openingPersonalId != null }
            assertEquals(DRAFT_PERSONAL_ID, opening.openingPersonalId)
            assertNull(opening.form)

            viewModel.sendIntent(Edit(DRAFT))
            gate.complete(Unit)

            val opened = awaitState { it.form != null }
            assertNull(opened.openingPersonalId)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(1, documents.readCount, "a second tap must not read the documents again")
    }

    // ------------------------------------------------------------------ row actions

    @Test
    fun `confirm asks first and sends nothing until answered`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.sendIntent(Confirm(DRAFT))

        assertEquals(
            PendingMemberAction(DRAFT, MemberAction.CONFIRM),
            viewModel.uiState.value.pendingAction,
        )
        assertNull(workshops.confirmedRequestId)
    }

    @Test
    fun `answering yes confirms that registration`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.sendIntent(Confirm(DRAFT))
        viewModel.sendIntent(PendingActionAccepted)

        assertEquals(DRAFT_REQUEST_ID, workshops.confirmedRequestId)
        assertNull(viewModel.uiState.value.pendingAction)
    }

    @Test
    fun `answering no to delete deletes nothing`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.sendIntent(Delete(DRAFT))
        viewModel.sendIntent(PendingActionDismissed)

        assertNull(workshops.deletedPersonalId)
        assertNull(viewModel.uiState.value.pendingAction)
    }

    @Test
    fun `answering yes to delete deletes that draft`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.sendIntent(Delete(DRAFT))
        viewModel.sendIntent(PendingActionAccepted)

        assertEquals(DRAFT_PERSONAL_ID, workshops.deletedPersonalId)
    }

    /** A submitted registration is refused outright — never asked about and then turned down. */
    @Test
    fun `a submitted registration is refused instead of asked about`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.sendIntent(Delete(SUBMITTED))
            assertEquals(ShowMessage(Res.string.new_member_cannot_edit), awaitItem())
        }

        assertNull(viewModel.uiState.value.pendingAction)
    }

    // ------------------------------------------------------------------ search

    @Test
    fun `searching by request status sends that status with the query`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        viewModel.sendIntent(DraftChanged(NewMemberSearch(status = NewMemberRequestStatus.UNDER_REVIEW)))
        viewModel.sendIntent(ApplySearch)

        assertEquals(NewMemberRequestStatus.UNDER_REVIEW, workshops.lastNewMemberQuery?.requestStatus)
    }

    // ------------------------------------------------------------------ pickers and paging

    @Test
    fun `opening the job picker loads first page and sets canPickerLoadMore true when more items exist`() =
        runTest(testDispatcher) {
            jobs.jobPages = mapOf(
                0 to (1..10).map { JobTitleDN(jobCode = "$it", jobDescription = "شغل $it", status = "", statusDate = "") },
                1 to (11..15).map { JobTitleDN(jobCode = "$it", jobDescription = "شغل $it", status = "", statusDate = "") },
            )
            jobs.totalJobs = 15
            val viewModel = viewModel()

            viewModel.sendIntent(Edit(WorkshopNewMemberPR()))
            viewModel.sendIntent(FormPickerOpened(RegistrationPicker.JOB))

            val form = assertNotNull(viewModel.uiState.value.form)
            assertEquals(RegistrationPicker.JOB, form.picker)
            assertEquals(10, form.pickerOptions.size)
            assertTrue(form.canPickerLoadMore, "picker should be able to load more when total > loaded")
            assertFalse(form.isPickerLoadingMore)
        }

    @Test
    fun `FormPickerLoadMore loads the next page and appends to pickerOptions`() =
        runTest(testDispatcher) {
            jobs.jobPages = mapOf(
                0 to (1..10).map { JobTitleDN(jobCode = "$it", jobDescription = "شغل $it", status = "", statusDate = "") },
                1 to (11..15).map { JobTitleDN(jobCode = "$it", jobDescription = "شغل $it", status = "", statusDate = "") },
            )
            jobs.totalJobs = 15
            val viewModel = viewModel()

            viewModel.sendIntent(Edit(WorkshopNewMemberPR()))
            viewModel.sendIntent(FormPickerOpened(RegistrationPicker.JOB))
            viewModel.sendIntent(FormPickerLoadMore)

            val form = assertNotNull(viewModel.uiState.value.form)
            assertEquals(15, form.pickerOptions.size)
            assertFalse(form.canPickerLoadMore, "end of list reached so canPickerLoadMore should be false")
        }

    @Test
    fun `FormPickerQueryChanged refreshes paginator with jobDescription LIKE filter`() =
        runTest(testDispatcher) {
            val viewModel = viewModel()

            viewModel.sendIntent(Edit(WorkshopNewMemberPR()))
            viewModel.sendIntent(FormPickerOpened(RegistrationPicker.JOB))
            viewModel.sendIntent(FormPickerQueryChanged("برنامه‌نویس"))

            val query = assertNotNull(jobs.lastQuery)
            val filter = assertNotNull(query.filters.firstOrNull())
            assertEquals(FilterProperty.JOB_DESCRIPTION, filter.property)
            assertEquals("*برنامه‌نویس*", filter.value)
            assertEquals(FilterOperator.LIKE, filter.operator)
        }

    @Test
    fun `opening city picker keeps canPickerLoadMore false`() =
        runTest(testDispatcher) {
            val viewModel = viewModel()

            viewModel.sendIntent(Edit(WorkshopNewMemberPR()))
            viewModel.sendIntent(FormPickerOpened(RegistrationPicker.BIRTH_CITY))

            val form = assertNotNull(viewModel.uiState.value.form)
            assertEquals(RegistrationPicker.BIRTH_CITY, form.picker)
            assertEquals(1, form.pickerOptions.size)
            assertFalse(form.canPickerLoadMore, "city pickers do not paginate")
        }

    /** A blank registration taken past step one and filled in on step two, not yet saved. */
    private fun fillStepTwo(viewModel: WorkshopRecentlyAddedMembersViewModel) {
        viewModel.sendIntent(Edit(WorkshopNewMemberPR()))
        viewModel.sendIntent(FormFieldChanged(RegistrationField.FIRST_NAME, "احمد"))
        viewModel.sendIntent(FormFieldChanged(RegistrationField.LAST_NAME, "احمدی"))
        viewModel.sendIntent(FormFieldChanged(RegistrationField.NATIONAL_ID, VALID_NATIONAL_ID))
        viewModel.sendIntent(FormFieldChanged(RegistrationField.BIRTH_DATE, BIRTH_DATE))
        viewModel.sendIntent(FormNext)
        viewModel.sendIntent(FormOptionPicked(RegistrationPicker.BIRTH_CITY, TEHRAN))
        viewModel.sendIntent(FormOptionPicked(RegistrationPicker.ISSUE_CITY, TEHRAN))
        viewModel.sendIntent(FormOptionPicked(RegistrationPicker.JOB, PROGRAMMER))
        viewModel.sendIntent(FormFieldChanged(RegistrationField.START_DATE, START_DATE))
    }

    private fun attach(viewModel: WorkshopRecentlyAddedMembersViewModel, typeCode: String) =
        viewModel.sendIntent(
            FormAddDocument(fileName = "id.jpg", bytes = ByteArray(2048), typeCode = typeCode),
        )
}

private const val WORKSHOP_ID = "9028218513"
private const val BRANCH_CODE = "14"
private const val UPLOADED_GUID = "a-guid"
private const val CREATED_PERSONAL_ID = 7L
private const val DRAFT_PERSONAL_ID = 42L
private const val DRAFT_REQUEST_ID = 420L
private const val ON_FILE_GUID = "on-file-guid"
private const val UNKNOWN_TYPE_GUID = "unknown-type-guid"

/**
 * A filed image two bytes short of the next kilobyte, whose base64 ends in two padding characters —
 * counting the padding as data would tip it into the wrong «کیلوبایت».
 */
private const val FILED_IMAGE_BYTES = 2047

@OptIn(ExperimentalEncodingApi::class)
private val FILED_IMAGE = Base64.encode(ByteArray(FILED_IMAGE_BYTES))

/** Ten digits whose check digit adds up. */
private const val VALID_NATIONAL_ID = "1234567891"
private const val BIRTH_DATE = "1370/01/01"
private const val START_DATE = "1405/01/01"

private val TEHRAN = PickedOption(code = "0701", label = "تهران")
private val PROGRAMMER = PickedOption(code = "7", label = "برنامه‌نویس")

/** A drafted, confirmable row as the list hands it over: codes rather than names. */
private val DRAFT = WorkshopNewMemberPR(
    personalId = DRAFT_PERSONAL_ID,
    requestId = DRAFT_REQUEST_ID,
    isDraft = true,
    canConfirm = true,
    firstName = "احمد",
    lastName = "احمدی",
    nationalId = VALID_NATIONAL_ID,
    birthDate = BIRTH_DATE,
    startDate = START_DATE,
    cityOfBirthId = TEHRAN.code,
    cityOfIssueId = TEHRAN.code,
    jobCode = PROGRAMMER.code,
)

private val SUBMITTED = DRAFT.copy(isDraft = false, canConfirm = false)

private fun filedDocument(guid: String, typeCode: String) = InsuredDocDN(
    documentType = typeCode,
    id = 1,
    documentFile = DocumentFileDN(createdBy = "", id = guid, image = ""),
)

/** Knows one city, which is all the pickers and the draft need. */
private class FakeCitiesRepository : CityProvinceRepository {
    private val tehran = CityDN(cityCode = TEHRAN.code, provinceCode = null, cityName = TEHRAN.label)

    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> =
        flowOf(listOf(tehran))

    override fun getCity(cityId: String): Flow<CityDN> =
        flowOf(tehran).filter { it.cityCode == cityId }

    override fun getProvinces(): Flow<List<ProvinceDN>> = unused()
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = unused()
    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = unused()
}

/** Knows one job; nothing else of the common repository is part of this screen's flows. */
private class FakeJobsRepository : CommonRepository {
    var lastQuery: ApiQueryParamDN? = null
    var jobPages: Map<Int, List<JobTitleDN>> = mapOf(
        0 to listOf(
            JobTitleDN(
                jobCode = PROGRAMMER.code,
                jobDescription = PROGRAMMER.label,
                status = "",
                statusDate = "",
            ),
        ),
    )
    var totalJobs: Int = 1

    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = flowOf(
        JobTitleListDN(
            list = jobPages[query.page] ?: emptyList(),
            total = totalJobs,
        ),
    )

    override fun getJobTitlePage(query: ApiQueryParamDN): Flow<PageDN<JobTitleDN>> = flow {
        lastQuery = query
        val pageNumber = query.page
        emit(
            PageDN(
                items = jobPages[pageNumber] ?: emptyList(),
                total = totalJobs,
            ),
        )
    }

    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = unused()
    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = unused()
    override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> =
        unused()

    override fun getRoles(): Flow<List<RoleDN>> = unused()
    override fun getInsuranceTypes(searchText: String?): Flow<List<InsuranceTypeDN>> = unused()
    override fun checkUserType(): Flow<UserTypeInfoDN> = unused()
}

/** Answers what is on file for a person, and records what documents were filed, and for whom. */
private class FakeDocumentsRepository : PersonalRepository {
    var filedPersonalId: String? = null
        private set
    var filedDocuments: List<InsuredDocDN>? = null
        private set

    /** What a read returns; set [readError] to make it fail instead. */
    var onFile: List<InsuredDocDN> = emptyList()
    var readError: Throwable? = null
    var readPersonalId: String? = null
        private set

    /** Holds a read open until completed, so a test can look at the screen while it runs. */
    var readGate: CompletableDeferred<Unit>? = null
    var readCount: Int = 0
        private set

    override fun getInsuredRegistrationDocList(personalId: String): Flow<List<InsuredDocDN>> = flow {
        readCount++
        readGate?.await()
        readError?.let { throw it }
        readPersonalId = personalId
        emit(onFile)
    }

    override fun putInsuredRegistrationDocList(
        personalId: String,
        docs: List<InsuredDocDN>,
    ): Flow<String?> = flow {
        filedPersonalId = personalId
        filedDocuments = docs
        emit(null)
    }

    override fun getPersonalInfo(refreshRemote: Boolean): Flow<PersonalInfoDN?> = unused()
    override fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN> = unused()
    override fun getAge(birthDate: Long): Flow<AgeDN> = unused()
    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> =
        unused()

    override fun getSurvivorList(deceasedNationalId: String): Flow<List<SurvivorDependentDN>> = unused()
    override fun checkGirlSurvivorConditions(
        nationalCode: String,
        pensionerId: String,
    ): Flow<GirlSurvivorConditionDN> = unused()

    override fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> = unused()
    override fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionDN,
    ): Flow<String?> = unused()

    override fun saveSurvivorInfo(body: SaveSurvivorInfoDN): Flow<String?> = unused()
    override fun getFinalSurvivorPensionPDF(): Flow<PdfDownloadDN> = unused()
    override fun getGirlSurvivorReport(params: GirlSurvivorReportParamsDN): Flow<PdfDownloadDN> = unused()
    override fun confirmGirlSurvivor(body: ConfirmGirlSurvivorDN): Flow<String?> = unused()
    override fun getRequestSummary(requestId: String): Flow<NewInsuredSummaryDN?> = unused()
}

private fun <T> unused(): Flow<T> = flow { error("not part of نام‌نویسی غیرحضوری") }

/** Skips states until one matches — the state flow may pass through several on the way. */
private suspend fun ReceiveTurbine<WorkshopRecentlyAddedMembersUiState>.awaitState(
    predicate: (WorkshopRecentlyAddedMembersUiState) -> Boolean,
): WorkshopRecentlyAddedMembersUiState {
    while (true) {
        val state = awaitItem()
        if (predicate(state)) return state
    }
}
