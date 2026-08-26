package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.useCases.common.GetJobTitleUseCase
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.util.toPersianDigits
import com.tamin.taminhamrah.useCases.workshops.CreateNewMemberRegistrationUseCase
import com.tamin.taminhamrah.useCases.workshops.CheckNewMemberIsNewUseCase
import com.tamin.taminhamrah.useCases.personal.PutInsuredRegistrationDocListUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.model.workshop.NewMemberRegistrationDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.DocumentFileDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.feature.workshops.ui.model.RegistrationDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormDocument
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.ConfirmRecentlyAddedMemberUseCase
import com.tamin.taminhamrah.useCases.workshops.DeleteRecentlyAddedMemberUseCase
import com.tamin.taminhamrah.useCases.workshops.GetRecentlyAddedMembersUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import taminx.core.core_ui.Res
import taminx.core.core_ui.new_member_cannot_edit

/** نام نویسی غیر حضوری بیمه شده. */
class WorkshopRecentlyAddedMembersViewModel(
    private val getRecentlyAddedMembers: GetRecentlyAddedMembersUseCase,
    private val confirmRecentlyAddedMember: ConfirmRecentlyAddedMemberUseCase,
    private val deleteRecentlyAddedMember: DeleteRecentlyAddedMemberUseCase,
    private val checkNewMemberIsNew: CheckNewMemberIsNewUseCase,
    private val createNewMemberRegistration: CreateNewMemberRegistrationUseCase,
    private val uploadImage: UploadImageUseCase,
    private val putRegistrationDocuments: PutInsuredRegistrationDocListUseCase,
    private val getCities: GetCitiesUseCase,
    private val getJobTitle: GetJobTitleUseCase,
) : BaseViewModel<
    WorkshopRecentlyAddedMembersUiState,
    PartialState,
    WorkshopRecentlyAddedMembersEvent,
    WorkshopRecentlyAddedMembersIntent,
    >(initialState = WorkshopRecentlyAddedMembersUiState()) {

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
        is WorkshopRecentlyAddedMembersIntent.Confirm -> confirm(intent.member)
        is WorkshopRecentlyAddedMembersIntent.Delete -> delete(intent.member)
        is WorkshopRecentlyAddedMembersIntent.Edit -> edit(intent.member)
        WorkshopRecentlyAddedMembersIntent.FormDismissed ->
            flow { emit(PartialState.FormChanged(null)) }

        WorkshopRecentlyAddedMembersIntent.FormNext -> formNext()
        WorkshopRecentlyAddedMembersIntent.FormPrev ->
            editForm { copy(step = (step - 1).coerceAtLeast(1), hasTriedNext = false) }

        is WorkshopRecentlyAddedMembersIntent.FormFieldChanged ->
            editForm { intent.edit(this).copy(hasTriedNext = false) }

        is WorkshopRecentlyAddedMembersIntent.FormPickerOpened -> openPicker(intent.picker)
        is WorkshopRecentlyAddedMembersIntent.FormPickerQueryChanged -> searchPicker(intent.query)
        is WorkshopRecentlyAddedMembersIntent.FormOptionPicked -> editForm {
            when (intent.picker) {
                RegistrationPicker.BIRTH_CITY -> copy(birthCity = intent.option)
                RegistrationPicker.ISSUE_CITY -> copy(issueCity = intent.option)
                RegistrationPicker.JOB -> copy(job = intent.option)
            }.copy(hasTriedNext = false, picker = null)
        }

        is WorkshopRecentlyAddedMembersIntent.FormAddDocument -> addDocument(intent)
        is WorkshopRecentlyAddedMembersIntent.FormRemoveDocument -> editForm {
            copy(
                documents = documents.removeAt(intent.index),
                uploaded = uploaded.removeAt(intent.index),
            )
        }

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

    /** Confirming reloads the list, because the row's own state changes with it. */
    private fun confirm(member: WorkshopNewMemberPR): Flow<PartialState> = flow<PartialState> {
        val requestId = member.requestId
        if (!member.canConfirm || requestId == null) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.new_member_cannot_edit))
            return@flow
        }
        emit(PartialState.Busy(member.personalId))
        val referenceCode = confirmRecentlyAddedMember(requestId)
        emit(PartialState.Busy(null))
        sendEvent(WorkshopRecentlyAddedMembersEvent.Confirmed(referenceCode))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.Busy(null))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    private fun delete(member: WorkshopNewMemberPR): Flow<PartialState> = flow<PartialState> {
        val personalId = member.personalId
        if (!member.isDraft || personalId == null) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.new_member_cannot_edit))
            return@flow
        }
        emit(PartialState.Busy(personalId))
        deleteRecentlyAddedMember(personalId)
        emit(PartialState.Busy(null))
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.Busy(null))
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    /**
     * Opens the registration form.
     *
     * A blank member is «افزودن پرسنل جدید»; a drafted one re-opens what was filled in, as far as
     * the list row carries it. A submitted registration cannot be edited at all.
     */
    private fun edit(member: WorkshopNewMemberPR): Flow<PartialState> = flow {
        if (!member.isDraft && member.nationalId.isNotBlank()) {
            sendEvent(WorkshopRecentlyAddedMembersEvent.ShowMessage(Res.string.new_member_cannot_edit))
            return@flow
        }
        emit(PartialState.FormChanged(RegistrationFormState()))
    }

    /** One-line edits of the open form, which is most of what it does. */
    private fun editForm(edit: RegistrationFormState.() -> RegistrationFormState) =
        flow { emit(PartialState.FormEdited(edit)) }

    /**
     * «مرحلهٔ بعد» on the first two steps, and the submission on the last.
     *
     * A step that is not complete reveals why instead of advancing — and step one additionally
     * refuses a national id whose check digit does not add up, before the service is asked.
     */
    private fun formNext(): Flow<PartialState> {
        val form = uiState.value.form ?: return flow { }
        val isStepValid = form.isStepComplete &&
            (form.step != 1 || isValidIranianNationalId(form.nationalId))
        if (!isStepValid) return editForm { copy(hasTriedNext = true) }
        if (!form.isLastStep) return editForm { copy(step = step + 1, hasTriedNext = false) }
        return submitRegistration()
    }

    /**
     * Opens a lookup sheet and fills it.
     *
     * Cities and jobs are both searched by name, so the sheet carries a query and re-asks as it
     * is typed rather than pulling every row down once.
     */
    private fun openPicker(picker: RegistrationPicker?): Flow<PartialState> = flow {
        emit(
            PartialState.FormEdited {
                copy(picker = picker, pickerQuery = "", pickerOptions = persistentListOf())
            },
        )
        if (picker != null) emitAll(loadPickerOptions(picker, query = ""))
    }

    private fun searchPicker(query: String): Flow<PartialState> = flow {
        val picker = uiState.value.form?.picker ?: return@flow
        emit(PartialState.FormEdited { copy(pickerQuery = query) })
        emitAll(loadPickerOptions(picker, query))
    }

    private fun loadPickerOptions(
        picker: RegistrationPicker,
        query: String,
    ): Flow<PartialState> = flow<PartialState> {
        emit(PartialState.FormEdited { copy(isPickerLoading = true) })
        val options = when (picker) {
            // The job list has no server-side name filter — there is no such property on the
            // wire — so it is fetched once and narrowed here.
            RegistrationPicker.JOB -> getJobTitle(emptyList()).first()?.list.orEmpty()
                .filter { query.isBlank() || it.jobDescription.contains(query) }
                .map { PickedOption(it.jobCode, it.jobDescription) }

            else -> getCities(cityName = query.takeIf { it.isNotBlank() }).first()
                .map { PickedOption(it.cityCode, it.cityName.orEmpty()) }
        }
        emit(
            PartialState.FormEdited {
                copy(isPickerLoading = false, pickerOptions = options.toPersistentList())
            },
        )
    }.catch {
        emit(PartialState.FormEdited { copy(isPickerLoading = false) })
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    /** Sends the picked image up and keeps only the guid that comes back. */
    private fun addDocument(
        intent: WorkshopRecentlyAddedMembersIntent.FormAddDocument,
    ): Flow<PartialState> = flow<PartialState> {
        emit(PartialState.FormEdited { copy(isUploading = true) })
        val guid = uploadImage(
            UploadImageRequestDN(fileName = intent.fileName, bytes = intent.bytes),
        ).first()
        val type = RegistrationDocumentTypes.first { it.code == intent.typeCode }
        emit(
            PartialState.FormEdited {
                copy(
                    isUploading = false,
                    hasTriedNext = false,
                    documents = documents.add(
                        WorkshopFormDocument(
                            typeCode = type.code,
                            typeLabel = type.label,
                            size = intent.bytes.size.asKilobytes(),
                        ),
                    ),
                    uploaded = uploaded.add(UploadedRegistrationDocument(guid, intent.typeCode)),
                )
            },
        )
    }.catch {
        emit(PartialState.FormEdited { copy(isUploading = false) })
        emit(PartialState.Error(it.toSingleLineMessage()))
    }

    /**
     * Creates the registration, then files its documents against the person it created.
     *
     * Three calls in order, as the old app makes them: ask whether this national id is already
     * known so an existing person is updated rather than duplicated, create, then attach. The
     * documents go last because they are filed against the `personalId` the create returns.
     */
    private fun submitRegistration(): Flow<PartialState> = flow<PartialState> {
        val state = uiState.value
        val form = state.form ?: return@flow
        emit(PartialState.FormEdited { copy(isSubmitting = true) })

        val existing = checkNewMemberIsNew(form.nationalId)
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
                personalId = existing.personalId,
            ),
        )

        val personalId = result.personalId ?: existing.personalId
        if (personalId != null) {
            putRegistrationDocuments(
                personalId.toString(),
                form.uploaded.map { document ->
                    InsuredDocDN(
                        documentType = document.typeCode,
                        id = 0,
                        documentFile = DocumentFileDN(
                            createdBy = "",
                            id = document.guid,
                            image = "",
                        ),
                    )
                },
            ).first()
        }

        emit(PartialState.FormChanged(null))
        sendEvent(
            WorkshopRecentlyAddedMembersEvent.RegistrationFiled(
                result.requestId?.toString().orEmpty(),
            ),
        )
        emitAll(loadPage(page = 0))
    }.catch {
        emit(PartialState.FormEdited { copy(isSubmitting = false) })
        emit(PartialState.Error(it.toSingleLineMessage()))
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
        is PartialState.FormChanged -> currentState.copy(form = partialState.form)
        is PartialState.FormEdited -> currentState.copy(
            form = currentState.form?.let(partialState.edit),
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

/**
 * A byte count as the whole kilobytes the upload box prints.
 *
 * Rounded up, so a file that is genuinely there never reads as «۰ کیلوبایت».
 */
private fun Int.asKilobytes(): String =
    ((this + BYTES_PER_KB - 1) / BYTES_PER_KB).toString().toPersianDigits()

private const val BYTES_PER_KB = 1024
