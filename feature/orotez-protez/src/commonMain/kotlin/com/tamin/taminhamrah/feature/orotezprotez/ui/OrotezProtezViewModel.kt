package com.tamin.taminhamrah.feature.orotezprotez.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezBranchDetailUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentChecklist
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezImageSource
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezInsuredDetailUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezMainInfoUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezPicker
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState.PartialState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.bytesOrNull
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.platformFileOrNull
import com.tamin.taminhamrah.mapper.orotezProtez.toBranchWorkshopPresentationList
import com.tamin.taminhamrah.mapper.orotezProtez.toPresentation
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopPR
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonPR
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisRequestDN
import com.tamin.taminhamrah.model.orotezProtez.ShortTermOrthosisRequestFileDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.GetInsuredPersonsUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.GetRequestInsuredMainInfoUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.SaveShortTermOrthosisUseCase
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.orotez_protez_document_duplicate_error
import taminx.core.core_ui.orotez_protez_document_format_error
import taminx.core.core_ui.orotez_protez_document_pick_read_error
import taminx.core.core_ui.orotez_protez_document_upload_error
import taminx.core.core_ui.orotez_protez_document_validation_min_count
import taminx.core.core_ui.orotez_protez_document_validation_required
import taminx.core.core_ui.orotez_protez_insured_person_subtitle
import taminx.core.core_ui.orotez_protez_submit_missing_data_error

class OrotezProtezViewModel(
    private val getRequestInsuredMainInfoUseCase: GetRequestInsuredMainInfoUseCase,
    private val getInsuredPersonsUseCase: GetInsuredPersonsUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val saveShortTermOrthosisUseCase: SaveShortTermOrthosisUseCase,
) : BaseViewModel<OrotezProtezUiState, PartialState, OrotezProtezEvent, OrotezProtezIntent>(
    initialState = OrotezProtezUiState()
) {

    init {
        sendIntent(OrotezProtezIntent.LoadInitialData)
    }

    override fun handleIntent(intent: OrotezProtezIntent): Flow<PartialState> = when (intent) {
        is OrotezProtezIntent.LoadInitialData -> loadInitialData()

        is OrotezProtezIntent.OnPickerRequested -> flow {
            emit(PartialState.PickerChanged(intent.picker))
        }

        is OrotezProtezIntent.OnPickerDismissed -> flow {
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
        }

        is OrotezProtezIntent.OnBranchPicked -> flow {
            emit(PartialState.BranchSelected(intent.option))
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
        }

        is OrotezProtezIntent.OnInsuredPersonPicked -> flow {
            emit(PartialState.InsuredPersonSelected(intent.option))
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
        }

        is OrotezProtezIntent.OnPrescriptionDatePicked -> flow {
            emit(PartialState.PrescriptionDateSelected(intent.millis, intent.label))
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
        }

        is OrotezProtezIntent.OnDocumentCardClicked -> flow {
            emit(PartialState.DocumentSourceRequested(intent.documentId))
        }

        is OrotezProtezIntent.OnDocumentSourceSelected -> flow {
            emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
            sendEvent(OrotezProtezEvent.LaunchImagePicker(intent.documentId, intent.source))
        }

        is OrotezProtezIntent.OnDocumentRemoveClicked -> handleDocumentRemoveClicked(intent.documentId)

        is OrotezProtezIntent.OnDocumentImagePicked -> handleDocumentImagePicked(intent.documentId, intent.file)

        is OrotezProtezIntent.OnDocumentImagePickFailed -> flow {
            emit(PartialState.DocumentPickRejected(intent.message))
        }

        is OrotezProtezIntent.OnNextStepClicked -> flow {
            if (uiState.value.canGoNext) {
                emit(PartialState.StepChanged(OrotezProtezStep.InsuredInfo))
            }
        }

        is OrotezProtezIntent.OnConfirmInsuredInfoClicked -> flow {
            emit(PartialState.StepChanged(OrotezProtezStep.Documents))
        }

        is OrotezProtezIntent.OnSubmitDocumentsClicked -> handleSubmitDocumentsClicked()

        is OrotezProtezIntent.BackToPreviousStep -> handleBackStep()
    }

    /**
     * Reads, validates (format/size/duplicate) and immediately uploads a picked/captured image.
     * Any file that fails validation is deleted right away — it's never stored in state. A file
     * that replaces an already-uploaded document for the same slot causes the *old* temp file to
     * be deleted once the new one has passed validation.
     */
    private fun handleDocumentImagePicked(documentId: String, file: PlatformFile): Flow<PartialState> = flow {
        val fileName = file.name
        if (!isJpegFileName(fileName)) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.orotez_protez_document_format_error)))
            return@flow
        }

        val bytes = try {
            file.readBytes()
        } catch (e: Exception) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.orotez_protez_document_pick_read_error)))
            return@flow
        }

        if (bytes.size > MAX_DOCUMENT_SIZE_BYTES) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.orotez_protez_document_format_error)))
            return@flow
        }

        val duplicateOfId = findDuplicateDocumentId(excludeId = documentId, bytes = bytes)
        if (duplicateOfId != null) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.orotez_protez_document_duplicate_error)))
            return@flow
        }

        // Past this point the new file is accepted for this slot — drop whatever it replaces.
        uiState.value.documents[documentId]?.platformFileOrNull()?.let { deleteFileQuietly(it) }

        emit(PartialState.DocumentStateChanged(documentId, OrotezProtezDocumentState.Uploading(file, bytes)))
        try {
            val guid = uploadImageUseCase(UploadImageRequestDN(fileName = fileName, bytes = bytes)).first()
            emit(PartialState.DocumentStateChanged(documentId, OrotezProtezDocumentState.Uploaded(guid, file, bytes)))
        } catch (e: Exception) {
            val message = e.toSingleLineMessage().ifBlank { getString(Res.string.orotez_protez_document_upload_error) }
            emit(PartialState.DocumentStateChanged(documentId, OrotezProtezDocumentState.Failed(message, file, bytes)))
        }
    }

    private fun handleDocumentRemoveClicked(documentId: String): Flow<PartialState> = flow {
        uiState.value.documents[documentId]?.platformFileOrNull()?.let { deleteFileQuietly(it) }
        emit(PartialState.DocumentStateChanged(documentId, OrotezProtezDocumentState.Empty))
        emit(PartialState.PickerChanged(OrotezProtezPicker.NONE))
    }

    /**
     * Validates the checklist (min document count, required documents present) and, if it
     * passes, builds the request body from what step 1/2/3 have already gathered and submits it.
     */
    private fun handleSubmitDocumentsClicked(): Flow<PartialState> = flow {
        val state = uiState.value
        val uploadedIds = state.uploadedDocumentIds
        val missingRequired = OrotezProtezDocumentChecklist.any { it.isRequired && it.id !in uploadedIds }
        val message = when {
            uploadedIds.size < MIN_REQUIRED_DOCUMENT_COUNT ->
                getString(Res.string.orotez_protez_document_validation_min_count)
            missingRequired ->
                getString(Res.string.orotez_protez_document_validation_required)
            else -> null
        }
        if (message != null) {
            emit(PartialState.DocumentValidationFailed(message))
            return@flow
        }

        val request = state.toSaveShortTermOrthosisRequestDN()
        if (request == null) {
            emit(PartialState.SubmitFailed(getString(Res.string.orotez_protez_submit_missing_data_error)))
            return@flow
        }

        emit(PartialState.DocumentsReadyForSubmission)
        emit(PartialState.Submitting(true))
        try {
            val resultMessage = saveShortTermOrthosisUseCase(request).first()
            emit(PartialState.SubmitSucceeded(resultMessage))
        } catch (e: Exception) {
            emit(PartialState.SubmitFailed(e.toSingleLineMessage()))
        }
    }

    /**
     * Assembles the final submit body from state gathered across all three steps: the
     * policyholder ([OrotezProtezUiState.mainInfo], step 1), the selected branch
     * ([OrotezProtezUiState.branchDetails], step 1), the selected insured person
     * ([OrotezProtezUiState.selectedInsuredDetail], step 1/2) and the uploaded documents
     * ([OrotezProtezUiState.documentSubmissionPayload], step 3). Null if any of these is missing,
     * which should not happen once [OrotezProtezUiState.canGoNext] has gated steps 2/3 — this is
     * only a defensive guard against submitting a malformed request.
     */
    private fun OrotezProtezUiState.toSaveShortTermOrthosisRequestDN(): SaveShortTermOrthosisRequestDN? {
        val mainInfo = mainInfo ?: return null
        val branchDetail = branch?.id?.let { branchDetails[it] } ?: return null
        val insuredId = insuredPerson?.id ?: return null
        val insuredDetail = selectedInsuredDetail ?: return null

        return SaveShortTermOrthosisRequestDN(
            branchCode = branchDetail.branchCode,
            branchName = branchDetail.branchName,
            insuranceFirstName = mainInfo.firstName,
            insuranceLastName = mainInfo.lastName,
            mobileNumber = mainInfo.mobileNumber,
            nationalCode = mainInfo.nationalCode,
            requestFileList = documentSubmissionPayload.map {
                ShortTermOrthosisRequestFileDN(documentFile = it.documentFile, documentType = it.documentType)
            },
            risuid = mainInfo.risuid,
            prescriptionDateTimeStamp = prescriptionDateTimeStamp,
            userNationalCode = insuredDetail.nationalCode,
            userRelation = insuredDetail.relationCode,
            userRelationship = insuredDetail.relation,
            userFirstName = insuredDetail.firstName,
            userInsuredId = insuredId,
            userLastName = insuredDetail.lastName,
        )
    }

    private fun findDuplicateDocumentId(excludeId: String, bytes: ByteArray): String? =
        uiState.value.documents.entries.firstOrNull { (id, state) ->
            id != excludeId && state.bytesOrNull()?.contentEquals(bytes) == true
        }?.key

    private suspend fun deleteFileQuietly(file: PlatformFile) {
        runCatching { file.delete(mustExist = false) }
    }

    private fun isJpegFileName(fileName: String): Boolean {
        val lower = fileName.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
    }

    /** Best-effort cleanup of every still-owned temp file once the screen (and this ViewModel) goes away. */
    override fun onCleared() {
        super.onCleared()
        val filesToDelete = uiState.value.documents.values.mapNotNull { it.platformFileOrNull() }
        if (filesToDelete.isEmpty()) return
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            filesToDelete.forEach { file -> runCatching { file.delete(mustExist = false) } }
        }
    }

    private fun handleBackStep(): Flow<PartialState> = flow {
        when (uiState.value.currentStep) {
            OrotezProtezStep.Documents -> emit(PartialState.StepChanged(OrotezProtezStep.InsuredInfo))
            OrotezProtezStep.InsuredInfo -> emit(PartialState.StepChanged(OrotezProtezStep.UserSelection))
            OrotezProtezStep.UserSelection -> sendEvent(OrotezProtezEvent.NavigateBack)
        }
    }

    /** Step 1's branch/workshop sheet and insured-person sheet are both fed by the real API. */
    private fun loadInitialData(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val mainInfoDN = getRequestInsuredMainInfoUseCase().first()
            val branchOptions = mainInfoDN.toBranchOptions()
            val insuredPersons = getInsuredPersonsUseCase().first().map { it.toPresentation() }
            emit(
                PartialState.DataLoaded(
                    branch = branchOptions.firstOrNull(),
                    branchOptions = branchOptions,
                    branchDetails = mainInfoDN.toBranchDetailUiMap(),
                    insuredPersonOptions = insuredPersons.toOptionUiList(),
                    insuredPersonDetails = insuredPersons.toDetailUiMap(),
                    mainInfo = mainInfoDN?.toMainInfoUi(),
                )
            )
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
        emit(PartialState.Loading(false))
    }

    /** DN -> PR (core-ui) -> this feature's generic picker-row model, kept as three separate steps per the project's layering convention. */
    private fun RequestInsuredMainInfoDN?.toBranchOptions(): ImmutableList<OrotezProtezOptionUi> {
        return this?.toBranchWorkshopPresentationList()
            ?.map { it.toOptionUi() }
            ?.toPersistentList()
            ?: persistentListOf()
    }

    private fun BranchWorkshopPR.toOptionUi() = OrotezProtezOptionUi(id = id, label = label)

    private fun RequestInsuredMainInfoDN?.toBranchDetailUiMap(): ImmutableMap<String, OrotezProtezBranchDetailUi> {
        return this?.toBranchWorkshopPresentationList()
            ?.associate { it.id to OrotezProtezBranchDetailUi(branchCode = it.branchCode, branchName = it.branchName) }
            ?.toPersistentMap()
            ?: persistentMapOf()
    }

    private fun RequestInsuredMainInfoDN.toMainInfoUi() = OrotezProtezMainInfoUi(
        risuid = risuid,
        nationalCode = nationalCode,
        firstName = firstName,
        lastName = lastName,
        mobileNumber = mobileNumber,
    )

    private suspend fun List<InsuredPersonPR>.toOptionUiList(): ImmutableList<OrotezProtezOptionUi> {
        return map { person ->
            OrotezProtezOptionUi(
                id = person.id,
                label = person.label,
                subtitle = getString(Res.string.orotez_protez_insured_person_subtitle, person.id),
            )
        }.toPersistentList()
    }

    private fun List<InsuredPersonPR>.toDetailUiMap(): ImmutableMap<String, OrotezProtezInsuredDetailUi> {
        return associate { person ->
            person.id to OrotezProtezInsuredDetailUi(
                fullName = person.fullName,
                firstName = person.firstName,
                lastName = person.lastName,
                relation = person.relation,
                relationCode = person.relationCode,
                nationalCode = person.nationalCode,
                birthCertificateNumber = person.birthCertificateNumber,
                issuePlace = person.issuePlace,
                birthDateLabel = person.birthDateLabel,
                bookletValidUntilLabel = person.bookletValidUntilLabel,
            )
        }.toPersistentMap()
    }

    override fun reduceState(
        currentState: OrotezProtezUiState,
        partialState: PartialState
    ): OrotezProtezUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.DataLoaded -> currentState.copy(
            branch = partialState.branch,
            branchOptions = partialState.branchOptions,
            branchDetails = partialState.branchDetails,
            insuredPersonOptions = partialState.insuredPersonOptions,
            insuredPersonDetails = partialState.insuredPersonDetails,
            mainInfo = partialState.mainInfo,
        )
        is PartialState.PickerChanged -> currentState.copy(
            picker = partialState.picker,
            activeDocumentId = if (partialState.picker == OrotezProtezPicker.DOCUMENT_SOURCE) {
                currentState.activeDocumentId
            } else {
                null
            },
        )
        is PartialState.DocumentSourceRequested -> currentState.copy(
            picker = OrotezProtezPicker.DOCUMENT_SOURCE,
            activeDocumentId = partialState.documentId,
        )
        is PartialState.BranchSelected -> currentState.copy(branch = partialState.branch)
        is PartialState.InsuredPersonSelected -> currentState.copy(insuredPerson = partialState.insuredPerson)
        is PartialState.PrescriptionDateSelected -> currentState.copy(
            prescriptionDateTimeStamp = partialState.millis,
            prescriptionDateLabel = partialState.label,
        )
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.DocumentStateChanged -> currentState.copy(
            documents = currentState.documents.toPersistentMap().put(partialState.documentId, partialState.state),
            documentPickError = null,
            documentValidationError = null,
        )
        is PartialState.DocumentPickRejected -> currentState.copy(documentPickError = partialState.message)
        is PartialState.DocumentValidationFailed -> currentState.copy(documentValidationError = partialState.message)
        PartialState.DocumentsReadyForSubmission -> currentState.copy(documentValidationError = null)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting, submitError = null)
        is PartialState.SubmitSucceeded -> currentState.copy(
            isSubmitting = false,
            hasSubmitted = true,
            submittedResultMessage = partialState.resultMessage,
            submitError = null,
        )
        is PartialState.SubmitFailed -> currentState.copy(isSubmitting = false, submitError = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private companion object {
        const val MIN_REQUIRED_DOCUMENT_COUNT = 2
        const val MAX_DOCUMENT_SIZE_BYTES = 2 * 1024 * 1024
    }
}
