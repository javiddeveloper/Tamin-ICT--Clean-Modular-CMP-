package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.RegistrationDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentDownloader
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopDocumentType
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.common.isValidIranianNationalId
import com.tamin.taminhamrah.model.personal.DocumentFileDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.pdfDownload.asPdfDownload
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.query.city.CityListQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.useCases.common.GetCitiesPageUseCase
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
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import taminx.core.core_ui.Res
import taminx.core.core_ui.abs_doc_unknown
import taminx.core.core_ui.abs_form_err_already_known
import taminx.core.core_ui.error_image_duplicate
import taminx.core.core_ui.new_member_cannot_edit

/** نام نویسی غیر حضوری بیمه شده. */
class WorkshopRecentlyAddedMembersViewModel(
    private val getRecentlyAddedMembers: GetRecentlyAddedMembersUseCase,
    private val confirmRecentlyAddedMember: ConfirmRecentlyAddedMemberUseCase,
    private val deleteRecentlyAddedMember: DeleteRecentlyAddedMemberUseCase,
    private val checkNewMemberIsNew: CheckNewMemberIsNewUseCase,
    private val createNewMemberRegistration: CreateNewMemberRegistrationUseCase,
    private val uploadAttachment: WorkshopAttachmentUploader,
    private val downloadAttachment: WorkshopAttachmentDownloader,
    private val putRegistrationDocuments: PutInsuredRegistrationDocListUseCase,
    private val getFiledDocuments: GetInsuredRegistrationDocListUseCase,
    private val getCitiesPage: GetCitiesPageUseCase,
    private val getCity: GetCityUseCase,
    private val getJobTitlePage: GetJobTitlePageUseCase,
    private val getRegistrationDeclarationForm: GetRegistrationDeclarationFormUseCase,
) : BaseViewModel<
    WorkshopRecentlyAddedMembersUiState,
    PartialState,
    WorkshopRecentlyAddedMembersEvent,
    WorkshopRecentlyAddedMembersIntent,
    >(initialState = WorkshopRecentlyAddedMembersUiState()) {

    private val jobPaginator = Paginator(
        loadPage = { query -> getJobTitlePage(query).first() },
    )
    private val cityPaginator = Paginator(
        loadPage = { query -> getCitiesPage(query).first() },
    )

    init {
        sendIntent(WorkshopRecentlyAddedMembersIntent.InitJobPaging)
    }

    override fun handleIntent(
        intent: WorkshopRecentlyAddedMembersIntent,
    ): Flow<PartialState> = when (intent) {
        is WorkshopRecentlyAddedMembersIntent.Open -> open(intent)
        WorkshopRecentlyAddedMembersIntent.LoadMore -> loadMore()
        WorkshopRecentlyAddedMembersIntent.Retry -> loadPage(page = 0)
        is WorkshopRecentlyAddedMembersIntent.SearchOpenChanged ->
            flow { emit(PartialState.SearchOpenChanged(intent.isOpen)) }

        is WorkshopRecentlyAddedMembersIntent.DraftChanged ->
            flow { emit(PartialState.DraftChanged(intent.draft)) }

        WorkshopRecentlyAddedMembersIntent.ApplySearch -> applySearch(uiState.value.draft)
        WorkshopRecentlyAddedMembersIntent.ClearSearch -> applySearch(NewMemberSearch())
        is WorkshopRecentlyAddedMembersIntent.Confirm -> ask(intent.member, MemberAction.CONFIRM)
        is WorkshopRecentlyAddedMembersIntent.Delete -> ask(intent.member, MemberAction.DELETE)
        WorkshopRecentlyAddedMembersIntent.PendingActionAccepted -> acceptPendingAction()
        WorkshopRecentlyAddedMembersIntent.PendingActionDismissed ->
            just(PartialState.PendingActionChanged(null))

        is WorkshopRecentlyAddedMembersIntent.Edit -> edit(intent.member)
        WorkshopRecentlyAddedMembersIntent.FormDismissed -> dismissForm()

        WorkshopRecentlyAddedMembersIntent.FormNext -> formNext()
        WorkshopRecentlyAddedMembersIntent.FormPrev ->
            just(PartialState.FormStepChanged((currentStep() - 1).coerceAtLeast(FIRST_STEP)))

        is WorkshopRecentlyAddedMembersIntent.FormFieldChanged ->
            just(PartialState.FormFieldChanged(intent.field, intent.value))

        is WorkshopRecentlyAddedMembersIntent.FormSummaryToggled ->
            just(PartialState.FormSummaryToggled(intent.isOpen))

        is WorkshopRecentlyAddedMembersIntent.FormConfirmedChanged ->
            just(PartialState.FormConfirmedChanged(intent.isConfirmed))

        is WorkshopRecentlyAddedMembersIntent.FormStepRequested ->
            just(PartialState.FormStepChanged(intent.step))

        is WorkshopRecentlyAddedMembersIntent.FormPickerOpened -> openPicker(intent.picker)
        is WorkshopRecentlyAddedMembersIntent.FormPickerQueryChanged -> searchPicker(intent.query)
        WorkshopRecentlyAddedMembersIntent.FormPickerLoadMore -> flow {
            when (uiState.value.form?.picker) {
                RegistrationPicker.JOB -> jobPaginator.loadNext()
                RegistrationPicker.BIRTH_CITY, RegistrationPicker.ISSUE_CITY -> cityPaginator.loadNext()
                null -> Unit
            }
        }
        WorkshopRecentlyAddedMembersIntent.InitJobPaging -> merge(observeJobPaging(), observeCityPaging())
        WorkshopRecentlyAddedMembersIntent.FormDownloadDeclaration -> downloadDeclaration()
        WorkshopRecentlyAddedMembersIntent.DeclarationViewerDismissed ->
            just(PartialState.DeclarationPdfChanged(null))
        is WorkshopRecentlyAddedMembersIntent.FormOptionPicked ->
            just(PartialState.FormOptionPicked(intent.picker, intent.option))

        is WorkshopRecentlyAddedMembersIntent.FormAddDocument -> addAttachment(intent)
        is WorkshopRecentlyAddedMembersIntent.FormRemoveDocument ->
            just(PartialState.FormAttachmentRemoved(intent.index))

        is WorkshopRecentlyAddedMembersIntent.Follow -> flow {
            sendEvent(WorkshopRecentlyAddedMembersEvent.OpenCartable(intent.member.referenceCode))
        }
    }

    private fun open(intent: WorkshopRecentlyAddedMembersIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode))
        emitAll(loadPage(page = 0, identity = intent.workshopId to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        search: NewMemberSearch = uiState.value.applied,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (workshopId, branchCode) = identity
        if (workshopId.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getRecentlyAddedMembers(
            WorkshopNewMemberQuery(
                workshopId = workshopId,
                branchCode = branchCode,
                nationalId = search.nationalId.takeIf { it.isNotBlank() },
                requestStatus = search.status,
                page = page,
            )
        )
        emit(
            PartialState.Loaded(
                uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() }
            )
        )
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    private fun applySearch(search: NewMemberSearch): Flow<PartialState> = flow {
        emit(PartialState.DraftChanged(search))
        emit(PartialState.Applied(search))
        emit(PartialState.SearchOpenChanged(false))
        emitAll(loadPage(page = 0, search = search))
    }

    /**
     * Asks «آیا مطمئن هستید؟» before a row action is sent: confirming files the request and deleting
     * discards the draft, and neither can be taken back from this list.
     *
     * A row that cannot take the action is refused before it is asked about, as the old app did —
     * a question whose "yes" is then turned down is worse than no question.
     */
    private fun ask(member: WorkshopNewMemberPR, action: MemberAction): Flow<PartialState> = flow {
        val isAllowed = when (action) {
            MemberAction.CONFIRM -> member.canConfirm && member.requestId != null
            MemberAction.DELETE -> member.isDraft && member.personalId != null
        }
        if (!isAllowed) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.new_member_cannot_edit))
            return@flow
        }
        emit(PartialState.PendingActionChanged(PendingMemberAction(member, action)))
    }

    private fun acceptPendingAction(): Flow<PartialState> = flow {
        val pending = uiState.value.pendingAction ?: return@flow
        emit(PartialState.PendingActionChanged(null))
        emitAll(
            when (pending.action) {
                MemberAction.CONFIRM -> confirm(pending.member)
                MemberAction.DELETE -> delete(pending.member)
            },
        )
    }

    /** Confirming reloads the list, because the row's own state changes with it. */
    private fun confirm(member: WorkshopNewMemberPR): Flow<PartialState> = flow {
        val requestId = member.requestId ?: return@flow
        emit(PartialState.Busy(member.personalId))
        val referenceCode = confirmRecentlyAddedMember(requestId)
        emit(PartialState.Busy(null))
        sendEvent(WorkshopRecentlyAddedMembersEvent.Confirmed(referenceCode))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.Busy(null))
        emit(reportFailure(it))
    }

    private fun delete(member: WorkshopNewMemberPR): Flow<PartialState> = flow {
        val personalId = member.personalId ?: return@flow
        emit(PartialState.Busy(personalId))
        deleteRecentlyAddedMember(personalId)
        emit(PartialState.Busy(null))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.Busy(null))
        emit(reportFailure(it))
    }

    /**
     * Closes the form. From step two on the person is on file as a draft, so the list is reloaded
     * to show the row the registration can be resumed from.
     */
    private fun dismissForm(): Flow<PartialState> = flow {
        val isOnFile = uiState.value.form?.personalId != null
        emit(PartialState.FormChanged(null))
        if (isOnFile) emitAll(loadPage(page = 0))
    }

    /**
     * Opens the registration form.
     *
     * A blank member is «افزودن پرسنل جدید»; a drafted one re-opens what was filled in, as far as
     * the list row carries it. A submitted registration cannot be edited at all.
     *
     * A draft's documents already on file are read before its form opens, not after: the last
     * step writes the document list back whole, as the old app does, so a form that could not
     * read them must not be the one to overwrite them. A failed read is said, and the form stays
     * shut.
     */
    private fun edit(member: WorkshopNewMemberPR): Flow<PartialState> = flow {
        if (!member.isDraft && member.nationalId.isNotBlank()) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.new_member_cannot_edit))
            return@flow
        }
        val personalId = member.personalId
        val filedDocuments: PersistentList<WorkshopAttachment> = if (personalId == null) {
            persistentListOf()
        } else {
            // One draft opens at a time; a second tap while its documents load would read them twice.
            if (uiState.value.openingPersonalId != null) return@flow
            emit(PartialState.OpeningChanged(personalId))
            val documents = readFiledDocuments(personalId)
            emit(PartialState.OpeningChanged(null))
            documents
        }
        emit(PartialState.FormChanged(member.asFormState(filedDocuments)))
        emitAll(resolveDraftLabels(member))
    }.catch {
        emit(PartialState.OpeningChanged(null))
        sendEvent(WorkshopRecentlyAddedMembersEvent.ShowServerMessage(it.toSingleLineMessage()))
    }

    /**
     * The documents already filed against [personalId], each image read back by its guid — so the
     * form holds exactly what an upload of them would have left it holding.
     */
    private suspend fun readFiledDocuments(personalId: Long): PersistentList<WorkshopAttachment> =
        coroutineScope {
            getFiledDocuments(personalId.toString()).first()
                .map { document ->
                    async {
                        downloadAttachment(
                            guid = document.documentFile.id,
                            type = filedDocumentType(document.documentType),
                        )
                    }
                }
                .awaitAll()
                .toPersistentList()
        }

    /**
     * Turns the codes a re-opened draft carries into the names the fields should show.
     *
     * The row stores what the service files — `cityOfBirthId`, a job code — so each is looked up
     * and the field re-seeded as though the user had picked it. A lookup that fails leaves the
     * code showing rather than failing the edit, which is why each is caught on its own.
     */
    private fun resolveDraftLabels(member: WorkshopNewMemberPR): Flow<PartialState> = flow {
        emitAll(resolveCity(member.cityOfBirthId, RegistrationPicker.BIRTH_CITY))
        emitAll(resolveCity(member.cityOfIssueId, RegistrationPicker.ISSUE_CITY))

        val jobCode = member.jobCode
        if (jobCode.isNotBlank()) {
            // Asked for by code, as the old app does: an unfiltered first page holds ten of the
            // thousands of jobs, so any other job would never be found there.
            val byCode = ApiQueryParamDN(
                filters = listOf(ApiFilterDN(FilterProperty.JOB_CODE, jobCode, FilterOperator.EQUAL)),
            )
            val name = runCatching {
                getJobTitlePage(byCode).first().items
                    .firstOrNull { it.jobCode == jobCode }?.jobDescription
            }.getOrNull()
            if (name != null) {
                emit(
                    PartialState.FormOptionPicked(
                        RegistrationPicker.JOB,
                        PickedOption(jobCode, name),
                    ),
                )
            }
        }
    }

    private fun resolveCity(
        cityId: String,
        picker: RegistrationPicker,
    ): Flow<PartialState> = flow {
        if (cityId.isBlank()) return@flow
        val name = runCatching { getCity(cityId).first().cityName }.getOrNull() ?: return@flow
        emit(PartialState.FormOptionPicked(picker, PickedOption(cityId, name)))
    }

    /**
     * A draft re-opened as the form that produced it.
     *
     * The identity, cities and job come off the list row, which already carries the codes —
     * `relation-tamins` returns the person's cities and job on the row itself. The row carries
     * codes, not names, so the fields start empty and [resolveDraftLabels] fills them in as each
     * lookup answers — a code is not a thing the user can read. The row does not carry the
     * documents, so [filedDocuments] are read separately.
     */
    private fun WorkshopNewMemberPR.asFormState(
        filedDocuments: PersistentList<WorkshopAttachment>,
    ): RegistrationFormState = RegistrationFormState(
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId.digitsOnly(),
        birthDate = birthDate,
        startDate = startDate,
        personalId = personalId,
        attachments = filedDocuments,
    )

    /**
     * The blank declaration form.
     *
     * A static PDF rather than a generated one, so it is fetched and handed straight to the
     * device's own saver; the screen writes it, because only the UI layer knows where downloads
     * belong on each platform.
     */
    private fun downloadDeclaration(): Flow<PartialState> = flow {
        emit(PartialState.FormDeclarationDownloading(true))
        val bytes = getRegistrationDeclarationForm().first()
        emit(PartialState.FormDeclarationDownloading(false))
        emit(PartialState.DeclarationPdfChanged(bytes.asPdfDownload()))
    }.catch {
        emit(PartialState.FormDeclarationDownloading(false))
        emit(reportFailure(it))
    }

    private fun just(partialState: PartialState): Flow<PartialState> =
        flow { emit(partialState) }

    /** The step the open form is on, or the first when none is open. */
    private fun currentStep(): Int = uiState.value.form?.step ?: FIRST_STEP

    /**
     * «مرحلهٔ بعد» on the first two steps, and the submission on the last.
     *
     * A step that is not complete reveals why instead of advancing — and step one additionally
     * refuses a national id whose check digit does not add up, before the service is asked. Step
     * two puts the person on file before moving on; the last step files the documents.
     */
    private fun formNext(): Flow<PartialState> {
        val form = uiState.value.form ?: return flow { }
        val isStepValid = form.isStepComplete &&
            (form.step != 1 || isValidIranianNationalId(form.nationalId))
        if (!isStepValid) return just(PartialState.FormNextRejected)
        return when {
            form.isLastStep -> fileDocuments()
            form.step == SAVE_STEP -> saveRegistration()
            else -> just(PartialState.FormStepChanged(form.step + 1))
        }
    }

    /**
     * Opens a lookup sheet and fills it.
     *
     * Cities and jobs are both searched by name, so the sheet carries a query and re-asks as it
     * is typed rather than pulling every row down once.
     */
    private fun openPicker(picker: RegistrationPicker?): Flow<PartialState> = flow {
        emit(PartialState.FormPickerOpened(picker))
        when (picker) {
            RegistrationPicker.JOB -> jobPaginator.refresh(jobBaseQuery(""))
            RegistrationPicker.BIRTH_CITY, RegistrationPicker.ISSUE_CITY -> cityPaginator.refresh(cityBaseQuery(""))
            null -> Unit
        }
    }

    private fun searchPicker(query: String): Flow<PartialState> = flow {
        val picker = uiState.value.form?.picker ?: return@flow
        emit(PartialState.FormPickerQueryChanged(query))
        when (picker) {
            RegistrationPicker.JOB -> jobPaginator.refresh(jobBaseQuery(query))
            RegistrationPicker.BIRTH_CITY, RegistrationPicker.ISSUE_CITY -> cityPaginator.refresh(cityBaseQuery(query))
        }
    }

    private fun observeJobPaging(): Flow<PartialState> = jobPaginator.state.transform { paging ->
        emit(
            PartialState.FormPickerJobPagingChanged(
                items = paging.items.map { PickedOption(it.jobCode, it.jobDescription) }.toPersistentList(),
                isLoadingFirstPage = paging.isLoadingFirstPage,
                isLoadingNextPage = paging.isLoadingNextPage,
                endReached = paging.endReached,
            ),
        )
        // The paginator keeps a failure in its state instead of throwing, so without this a job
        // search that failed would read as a search that found nothing. Said the way a failed
        // city search is.
        paging.error?.let { emit(reportFailure(it)) }
    }

    private fun observeCityPaging(): Flow<PartialState> = cityPaginator.state.transform { paging ->
        emit(
            PartialState.FormPickerCityPagingChanged(
                items = paging.items.map { PickedOption(it.cityCode, it.cityName.orEmpty()) }.toPersistentList(),
                isLoadingFirstPage = paging.isLoadingFirstPage,
                isLoadingNextPage = paging.isLoadingNextPage,
                endReached = paging.endReached,
            ),
        )
        paging.error?.let { emit(reportFailure(it)) }
    }

    /** `jobDescription LIKE "*query*"`, and `LIKE "*"` for a blank one — what the old app sends. */
    private fun jobBaseQuery(query: String): ApiQueryParamDN = ApiQueryParamDN(
        filters = listOf(
            ApiFilterDN(
                property = FilterProperty.JOB_DESCRIPTION,
                value = query.trim().takeIf { it.isNotEmpty() }?.let { "*$it*" } ?: "*",
                operator = FilterOperator.LIKE,
            ),
        ),
    )

    private fun cityBaseQuery(query: String): ApiQueryParamDN = ApiQueryParamDN(
        filters = CityListQuery.filters(cityName = query.takeIf { it.isNotBlank() }),
    )

    /**
     * Sends the picked image up and keeps only the guid that comes back.
     *
     * Each type takes one image. The sheet stops offering a type once it is filed, and one that
     * arrives anyway is refused here, before anything is uploaded.
     */
    private fun addAttachment(
        intent: WorkshopRecentlyAddedMembersIntent.FormAddDocument,
    ): Flow<PartialState> = flow {
        if (uiState.value.form?.hasDocumentOfType(intent.typeCode) == true) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.error_image_duplicate))
            return@flow
        }
        emit(PartialState.FormUploadingChanged(true))
        val attachment = uploadAttachment(
            fileName = intent.fileName,
            bytes = intent.bytes,
            typeCode = intent.typeCode,
            types = RegistrationDocumentTypes,
        )
        emit(PartialState.FormAttachmentAdded(attachment))
    }.catch {
        emit(PartialState.FormUploadingChanged(false))
        emit(reportFailure(it))
    }

    /**
     * Puts the person on file as a draft once step two is complete, before any document is asked
     * for — where the old app creates the record too, so a registration abandoned at the documents
     * is still in the list to be resumed.
     *
     * Someone not yet on file is checked against `relation-tamins/isnew` first, so a person the
     * organization already knows is refused here rather than after their documents are uploaded.
     * Someone already on file — a re-opened draft, or this form back on step two — is updated
     * rather than created again.
     */
    private fun saveRegistration(): Flow<PartialState> = flow {
        val state = uiState.value
        val form = state.form ?: return@flow
        emit(PartialState.FormSubmittingChanged(true))

        // `relation-tamins/isnew` answers a bare boolean and carries no id, so it is a gate, not
        // a lookup: a person the organization already knows cannot be registered again here.
        if (form.personalId == null && !checkNewMemberIsNew(form.nationalId)) {
            emit(PartialState.FormSubmittingChanged(false))
            sendEvent(
                WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.abs_form_err_already_known),
            )
            return@flow
        }

        val result = createNewMemberRegistration(
            NewMemberRegistrationDN(
                firstName = form.firstName,
                lastName = form.lastName,
                nationalId = form.nationalId,
                dateOfBirth = form.birthDate,
                cityOfBirthId = form.birthCity?.code.orEmpty(),
                cityOfIssueId = form.issueCity?.code.orEmpty(),
                jobCode = form.job?.code.orEmpty(),
                startDate = form.startDate,
                workshopId = state.workshopId,
                branchCode = state.branchCode,
                // Set once the person is on file, which makes this an update, not a second record.
                personalId = form.personalId,
            ),
        )
        // The documents are filed against this id; going on without one would only fail at the
        // last step, after they have been uploaded.
        val personalId = checkNotNull(result.personalId ?: form.personalId) {
            "employers answered without the person's id"
        }
        emit(PartialState.FormSaved(personalId))
    }.catch {
        emit(PartialState.FormSubmittingChanged(false))
        emit(reportFailure(it))
    }

    /** Files the documents against the person step two put on file, then closes the form. */
    private fun fileDocuments(): Flow<PartialState> = flow {
        val form = uiState.value.form ?: return@flow
        val personalId = checkNotNull(form.personalId) {
            "the last step is reached only after step two has saved the person"
        }
        emit(PartialState.FormSubmittingChanged(true))
        // The list replaces what is on file, so it carries the documents that were already there.
        putRegistrationDocuments(personalId.toString(), form.attachments.map { it.toInsuredDoc() })
            .first()

        emit(PartialState.FormChanged(null))
        // The creation returns the person, not a tracking code — the row that appears in the list
        // carries it, so the message says the registration was filed and no more.
        sendEvent(WorkshopRecentlyAddedMembersEvent.RegistrationFiled)
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.FormSubmittingChanged(false))
        emit(reportFailure(it))
    }

    override fun reduceState(
        currentState: WorkshopRecentlyAddedMembersUiState,
        partialState: PartialState,
    ): WorkshopRecentlyAddedMembersUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(
            busyPersonalId = null,
            list = currentState.list.failed(partialState.message),
        )

        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.DraftChanged -> currentState.copy(draft = partialState.draft)
        is PartialState.Applied -> currentState.copy(applied = partialState.search)
        is PartialState.SearchOpenChanged -> currentState.copy(isSearchOpen = partialState.isOpen)
        is PartialState.Busy -> currentState.copy(busyPersonalId = partialState.personalId)
        is PartialState.PendingActionChanged ->
            currentState.copy(pendingAction = partialState.pending)

        is PartialState.OpeningChanged ->
            currentState.copy(openingPersonalId = partialState.personalId)

        is PartialState.FormChanged -> currentState.copy(form = partialState.form)
        is PartialState.FormStepChanged -> currentState.editForm {
            copy(step = partialState.step, hasTriedNext = false)
        }

        is PartialState.FormFieldChanged -> currentState.editForm {
            when (partialState.field) {
                RegistrationField.FIRST_NAME -> copy(firstName = partialState.value)
                RegistrationField.LAST_NAME -> copy(lastName = partialState.value)
                RegistrationField.NATIONAL_ID -> copy(nationalId = partialState.value)
                RegistrationField.BIRTH_DATE -> copy(birthDate = partialState.value)
                RegistrationField.START_DATE -> copy(startDate = partialState.value)
            }.copy(hasTriedNext = false)
        }

        is PartialState.FormOptionPicked -> currentState.editForm {
            when (partialState.picker) {
                RegistrationPicker.BIRTH_CITY -> copy(birthCity = partialState.option)
                RegistrationPicker.ISSUE_CITY -> copy(issueCity = partialState.option)
                RegistrationPicker.JOB -> copy(job = partialState.option)
            }.copy(hasTriedNext = false, picker = null)
        }

        is PartialState.FormSummaryToggled -> currentState.editForm {
            copy(isSummaryOpen = partialState.isOpen)
        }

        is PartialState.FormConfirmedChanged -> currentState.editForm {
            copy(isConfirmed = partialState.isConfirmed, hasTriedNext = false)
        }

        is PartialState.FormAttachmentAdded -> currentState.editForm {
            copy(
                isUploading = false,
                hasTriedNext = false,
                attachments = attachments.add(partialState.attachment),
            )
        }

        is PartialState.FormAttachmentRemoved -> currentState.editForm {
            copy(attachments = attachments.removeAt(partialState.index))
        }

        PartialState.FormNextRejected -> currentState.editForm { copy(hasTriedNext = true) }
        is PartialState.FormSaved -> currentState.editForm {
            copy(
                personalId = partialState.personalId,
                step = step + 1,
                isSubmitting = false,
                hasTriedNext = false,
            )
        }

        is PartialState.FormUploadingChanged -> currentState.editForm {
            copy(isUploading = partialState.isUploading)
        }

        is PartialState.FormSubmittingChanged -> currentState.editForm {
            copy(isSubmitting = partialState.isSubmitting)
        }

        is PartialState.DeclarationPdfChanged ->
            currentState.copy(declarationPdf = partialState.pdf)

        is PartialState.FormDeclarationDownloading -> currentState.editForm {
            copy(isDownloadingDeclaration = partialState.isDownloading)
        }

        is PartialState.FormPickerOpened -> currentState.editForm {
            copy(
                picker = partialState.picker,
                pickerQuery = "",
                pickerOptions = persistentListOf(),
                isPickerLoading = false,
                isPickerLoadingMore = false,
                canPickerLoadMore = false,
            )
        }

        is PartialState.FormPickerQueryChanged -> currentState.editForm {
            copy(pickerQuery = partialState.query)
        }

        is PartialState.FormPickerJobPagingChanged -> currentState.editForm {
            if (picker == RegistrationPicker.JOB) {
                copy(
                    pickerOptions = partialState.items,
                    isPickerLoading = partialState.isLoadingFirstPage,
                    isPickerLoadingMore = partialState.isLoadingNextPage,
                    canPickerLoadMore = !partialState.endReached,
                )
            } else {
                this
            }
        }

        is PartialState.FormPickerCityPagingChanged -> currentState.editForm {
            if (picker == RegistrationPicker.BIRTH_CITY || picker == RegistrationPicker.ISSUE_CITY) {
                copy(
                    pickerOptions = partialState.items,
                    isPickerLoading = partialState.isLoadingFirstPage,
                    isPickerLoadingMore = partialState.isLoadingNextPage,
                    canPickerLoadMore = !partialState.endReached,
                )
            } else {
                this
            }
        }
    }

    /**
     * A failure the user must see now.
     *
     * With a form open the list is not on screen, so its error state is not either; the
     * message is raised as an event instead and the toast host shows it.
     */
    private fun reportFailure(throwable: Throwable): PartialState {
        val message = throwable.toSingleLineMessage()
        if (uiState.value.form != null) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowServerMessage(message))
        }
        return PartialState.Error(message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

/** Applies [edit] to the open form, or does nothing when no form is open. */
private inline fun WorkshopRecentlyAddedMembersUiState.editForm(
    edit: RegistrationFormState.() -> RegistrationFormState,
): WorkshopRecentlyAddedMembersUiState = copy(form = form?.edit())

/** Forms count their steps from one. */
private const val FIRST_STEP = 1

/** The step whose «مرحلهٔ بعد» puts the person on file — everything the create needs is in by then. */
private const val SAVE_STEP = 2

/**
 * The registration type a document on file was filed under.
 *
 * A code the table does not list keeps a generic label rather than being dropped: the document
 * list is written back whole, so dropping one here would delete it from the person's file. The old
 * app kept such documents too, untitled.
 */
private fun filedDocumentType(code: String): WorkshopDocumentType =
    RegistrationDocumentTypes.firstOrNull { it.code == code }
        ?: WorkshopDocumentType(code, Res.string.abs_doc_unknown)

/** An attachment as `documents/{personalId}` files it: the uploaded image's guid, under its type. */
private fun WorkshopAttachment.toInsuredDoc(): InsuredDocDN = InsuredDocDN(
    documentType = type.code,
    id = 0,
    documentFile = DocumentFileDN(createdBy = "", id = guid, image = ""),
)
