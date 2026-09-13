package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.AddressError
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.bytesOrNull
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.platformFileOrNull
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.toDisabilityDocumentDNs
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState.PartialState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.LandlinePhoneError
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.addDependent.RefreshDependentsUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.pension.FinalConfirmDisabilityRequestUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetMedicalCommissionPdfUseCase
import com.tamin.taminhamrah.useCases.pension.GetRegisteredMedicalCommissionUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
import com.tamin.taminhamrah.useCases.pension.SaveDisabilityUserInfoUseCase
import com.tamin.taminhamrah.useCases.pension.SaveDocumentDisabilityUseCase
import com.tamin.taminhamrah.useCases.personal.GetDisabilityDependentInfoUseCase
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_female_title
import taminx.core.core_ui.disability_pension_male_title
import taminx.core.core_ui.disability_pension_history_objection_coming_soon
import taminx.core.core_ui.orotez_protez_document_format_error
import taminx.core.core_ui.orotez_protez_document_pick_read_error
import taminx.core.core_ui.orotez_protez_document_duplicate_error
import taminx.core.core_ui.orotez_protez_document_upload_error

enum class DisabilityDemandType(val code: String) {
    DISABILITY_PENSION("01")
}

class DisabilityPensionViewModel(
    private val getDisabilityPersonalInfoUseCase: GetDisabilityPersonalInfoUseCase,
    private val getDisabilityDependentInfoUseCase: GetDisabilityDependentInfoUseCase,
    private val refreshDependentsUseCase: RefreshDependentsUseCase,
    private val getUserAgeUseCase: GetUserAgeUseCase,
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
    private val getRegisteredMedicalCommissionUseCase: GetRegisteredMedicalCommissionUseCase,
    private val getMedicalCommissionPdfUseCase: GetMedicalCommissionPdfUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val saveDisabilityUserInfoUseCase: SaveDisabilityUserInfoUseCase,
    private val saveDocumentDisabilityUseCase: SaveDocumentDisabilityUseCase,
    private val finalConfirmDisabilityRequestUseCase: FinalConfirmDisabilityRequestUseCase,
) : BaseViewModel<DisabilityPensionUiState, PartialState, DisabilityPensionEvent, DisabilityPensionIntent>(
    initialState = DisabilityPensionUiState()
) {

    /** Raw epoch millis for [DisabilitySaveInfoDN.birthDate] — the presentation model only exposes a formatted string. */
    private var applicantBirthDate: Long? = null

    init {
        sendIntent(DisabilityPensionIntent.Init)
    }

    override fun handleIntent(intent: DisabilityPensionIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(DisabilityPensionEvent.ShowToast(e.toSingleLineMessage()))
            emit(createErrorState(e.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: DisabilityPensionIntent): Flow<PartialState> = flow {
        when (intent) {
            DisabilityPensionIntent.Init -> loadApplicantInfo()
            is DisabilityPensionIntent.TermsAcceptedChanged -> {
                emit(PartialState.TermsAcceptedChanged(intent.accepted))
                if (intent.accepted) {
                    emit(PartialState.TermsValidationErrorChanged(false))
                }
            }
            DisabilityPensionIntent.ShowRulesClicked -> emit(PartialState.RulesVisibilityChanged(true))
            DisabilityPensionIntent.DismissRules -> emit(PartialState.RulesVisibilityChanged(false))
            DisabilityPensionIntent.NextStepClicked -> handleNextStepClicked()
            DisabilityPensionIntent.PreviousStepClicked -> handlePreviousStepClicked()
            is DisabilityPensionIntent.DependentCardToggled -> {
                emit(PartialState.DependentCardToggled(intent.id))
            }
            is DisabilityPensionIntent.DependentsListConfirmedChanged -> {
                emit(PartialState.DependentsConfirmedChanged(intent.accepted))
                if (intent.accepted) {
                    emit(PartialState.DependentsConfirmationErrorChanged(false))
                }
            }
            DisabilityPensionIntent.AddDependentClicked -> {
                sendEvent(DisabilityPensionEvent.NavigateToAddDependent)
            }
            DisabilityPensionIntent.RefreshDependentsClicked -> {
                emit(PartialState.RefreshConfirmDialogVisibilityChanged(true))
            }
            DisabilityPensionIntent.DismissRefreshConfirm -> {
                emit(PartialState.RefreshConfirmDialogVisibilityChanged(false))
            }
            DisabilityPensionIntent.ConfirmRefreshDependents -> confirmRefreshDependents()
            DisabilityPensionIntent.DependentsResumed -> {
                if (uiState.value.currentStep == DisabilityPensionStep.Dependents) {
                    loadDependents()
                }
            }
            DisabilityPensionIntent.ToggleIdentityDetails -> {
                emit(PartialState.IdentityDetailsExpandedChanged(!uiState.value.isIdentityDetailsExpanded))
            }
            is DisabilityPensionIntent.LandlinePhoneChanged -> {
                emit(PartialState.LandlinePhoneChanged(intent.value, null))
            }
            is DisabilityPensionIntent.AddressChanged -> {
                emit(PartialState.AddressChanged(intent.value, null))
            }
            is DisabilityPensionIntent.IdentityConfirmedChanged -> {
                emit(PartialState.IdentityConfirmedChanged(intent.accepted))
                if (intent.accepted) {
                    emit(PartialState.IdentityConfirmationErrorChanged(false))
                }
            }
            is DisabilityPensionIntent.WorkshopNameChanged -> {
                emit(PartialState.WorkshopNameChanged(intent.value, error = false))
            }
            is DisabilityPensionIntent.ActivityTypeChanged -> {
                emit(PartialState.ActivityTypeChanged(intent.value))
            }
            is DisabilityPensionIntent.EmployerNameChanged -> {
                emit(PartialState.EmployerNameChanged(intent.value))
            }
            is DisabilityPensionIntent.WorkshopAddressChanged -> {
                emit(PartialState.WorkshopAddressChanged(intent.value, error = false))
            }
            is DisabilityPensionIntent.WorkshopConfirmedChanged -> {
                emit(PartialState.WorkshopConfirmedChanged(intent.accepted))
                if (intent.accepted) {
                    emit(PartialState.WorkshopConfirmationErrorChanged(false))
                }
            }
            is DisabilityPensionIntent.CommissionObjectionChanged -> {
                emit(PartialState.CommissionObjectionChanged(intent.hasObjection))
                emit(PartialState.CommissionValidationErrorChanged(false))
            }
            DisabilityPensionIntent.HistoryObjectionLinkClicked -> {
                sendEvent(DisabilityPensionEvent.ShowToast(org.jetbrains.compose.resources.getString(taminx.core.core_ui.Res.string.disability_pension_history_objection_coming_soon)))
            }
            DisabilityPensionIntent.ShowRegisteredRequestsClicked -> {
                emit(PartialState.RegisteredRequestsSheetVisibilityChanged(true))
                loadRegisteredRequests()
            }
            DisabilityPensionIntent.DismissRegisteredRequestsSheet -> {
                emit(PartialState.RegisteredRequestsSheetVisibilityChanged(false))
            }
            DisabilityPensionIntent.ShowMedicalCommissionPdfViewerClicked -> {
                emit(PartialState.MedicalCommissionPdfViewerVisibilityChanged(true))
            }
            DisabilityPensionIntent.DownloadMedicalCommissionPdfClicked -> downloadMedicalCommissionPdf()
            DisabilityPensionIntent.DismissMedicalCommissionPdfViewer -> {
                emit(PartialState.MedicalCommissionPdfViewerVisibilityChanged(false))
                emit(PartialState.MedicalCommissionPdfChanged(null))
            }
            is DisabilityPensionIntent.DocumentCardClicked -> {
                emit(PartialState.DocumentSourceRequested(intent.documentId))
            }
            is DisabilityPensionIntent.DocumentSourceSelected -> {
                emit(PartialState.DocumentSourceSheetDismissed)
                sendEvent(DisabilityPensionEvent.LaunchImagePicker(intent.documentId, intent.source))
            }
            is DisabilityPensionIntent.DocumentRemoveClicked -> handleDocumentRemoveClicked(intent.documentId)
            is DisabilityPensionIntent.DocumentImagePicked -> handleDocumentImagePicked(intent.documentId, intent.file)
            is DisabilityPensionIntent.DocumentImagePickFailed -> {
                emit(PartialState.DocumentPickRejected(intent.message))
            }
            DisabilityPensionIntent.DismissDocumentSourceSheet -> {
                emit(PartialState.DocumentSourceSheetDismissed)
            }
            DisabilityPensionIntent.ConfirmDocumentsSubmission -> {
                emit(PartialState.DocumentsConfirmDialogVisibilityChanged(false))
                emit(PartialState.EditingFromSummaryChanged(false))
                emit(PartialState.StepChanged(DisabilityPensionStep.Summary))
            }
            DisabilityPensionIntent.DismissDocumentsConfirmDialog -> {
                emit(PartialState.DocumentsConfirmDialogVisibilityChanged(false))
            }
            is DisabilityPensionIntent.FinalConfirmedChanged -> {
                emit(PartialState.FinalConfirmedChanged(intent.accepted))
                if (intent.accepted) {
                    emit(PartialState.FinalConfirmationErrorChanged(false))
                }
            }
            is DisabilityPensionIntent.EditSummarySectionClicked -> {
                emit(PartialState.EditingFromSummaryChanged(true))
                emit(PartialState.StepChanged(intent.step))
            }
            DisabilityPensionIntent.SubmitSuccessAcknowledged -> {
                sendEvent(DisabilityPensionEvent.NavigateBack)
            }
            DisabilityPensionIntent.CloseClicked -> {
                emit(PartialState.ExitConfirmDialogVisibilityChanged(true))
            }
            DisabilityPensionIntent.DismissExitConfirmDialog -> {
                emit(PartialState.ExitConfirmDialogVisibilityChanged(false))
            }
            DisabilityPensionIntent.ConfirmExitClicked -> {
                emit(PartialState.ExitConfirmDialogVisibilityChanged(false))
                sendEvent(DisabilityPensionEvent.NavigateBack)
            }
        }
    }

    private suspend fun FlowCollector<PartialState>.handleDocumentImagePicked(
        documentId: String,
        file: PlatformFile,
    ) {
        val fileName = file.name
        if (!isJpegFileName(fileName)) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.orotez_protez_document_format_error)))
            return
        }

        val bytes = try {
            file.readBytes()
        } catch (e: Exception) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.orotez_protez_document_pick_read_error)))
            return
        }

        if (bytes.size > MAX_DOCUMENT_SIZE_BYTES) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.orotez_protez_document_format_error)))
            return
        }

        val duplicateOfId = findDuplicateDocumentId(excludeId = documentId, bytes = bytes)
        if (duplicateOfId != null) {
            deleteFileQuietly(file)
            emit(PartialState.DocumentPickRejected(getString(Res.string.orotez_protez_document_duplicate_error)))
            return
        }

        uiState.value.documents[documentId]?.platformFileOrNull()?.let { deleteFileQuietly(it) }

        emit(PartialState.DocumentStateChanged(documentId, DisabilityDocumentState.Uploading(file, bytes)))
        try {
            val guid = uploadImageUseCase(UploadImageRequestDN(fileName = fileName, bytes = bytes)).first()
            emit(PartialState.DocumentStateChanged(documentId, DisabilityDocumentState.Uploaded(guid, file, bytes)))
        } catch (e: Exception) {
            val message = e.toSingleLineMessage().ifBlank { getString(Res.string.orotez_protez_document_upload_error) }
            emit(PartialState.DocumentStateChanged(documentId, DisabilityDocumentState.Failed(message, file, bytes)))
        }
    }

    private suspend fun FlowCollector<PartialState>.handleDocumentRemoveClicked(documentId: String) {
        uiState.value.documents[documentId]?.platformFileOrNull()?.let { deleteFileQuietly(it) }
        emit(PartialState.DocumentStateChanged(documentId, DisabilityDocumentState.Empty))
        emit(PartialState.DocumentSourceSheetDismissed)
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

    override fun onCleared() {
        super.onCleared()
        val filesToDelete = uiState.value.documents.values.mapNotNull { it.platformFileOrNull() }
        if (filesToDelete.isEmpty()) return
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            filesToDelete.forEach { file -> runCatching { file.delete(mustExist = false) } }
        }
    }

    private suspend fun FlowCollector<PartialState>.handleNextStepClicked() {
        when (uiState.value.currentStep) {
            DisabilityPensionStep.Terms -> {
                if (uiState.value.isTermsAccepted) {
                    emit(PartialState.TermsValidationErrorChanged(false))
                    if (uiState.value.isEditingFromSummary) {
                        returnToSummary()
                    } else {
                        emit(PartialState.StepChanged(DisabilityPensionStep.Dependents))
                        loadDependents()
                    }
                } else {
                    emit(PartialState.TermsValidationErrorChanged(true))
                }
            }
            DisabilityPensionStep.Dependents -> {
                if (uiState.value.isDependentsListConfirmed) {
                    emit(PartialState.DependentsConfirmationErrorChanged(false))
                    if (uiState.value.isEditingFromSummary) {
                        returnToSummary()
                    } else {
                        emit(PartialState.StepChanged(DisabilityPensionStep.IdentityContact))
                    }
                } else {
                    emit(PartialState.DependentsConfirmationErrorChanged(true))
                }
            }
            DisabilityPensionStep.IdentityContact -> handleIdentityContactNextStep()
            DisabilityPensionStep.Workshop -> handleWorkshopNextStep()
            DisabilityPensionStep.CommissionRecord -> handleCommissionRecordNextStep()
            DisabilityPensionStep.Documents -> handleDocumentsNextStep()
            DisabilityPensionStep.Summary -> handleFinalSubmit()
        }
    }

    /** Jumps straight back to [DisabilityPensionStep.Summary], clearing edit-from-summary mode. */
    private suspend fun FlowCollector<PartialState>.returnToSummary() {
        emit(PartialState.EditingFromSummaryChanged(false))
        emit(PartialState.StepChanged(DisabilityPensionStep.Summary))
    }

    private suspend fun FlowCollector<PartialState>.handleIdentityContactNextStep() {
        val state = uiState.value
        val phoneError = validateLandlinePhone(state.landlinePhone)
        val addressError = validateAddress(state.address)
        emit(PartialState.LandlinePhoneChanged(state.landlinePhone, phoneError))
        emit(PartialState.AddressChanged(state.address, addressError))

        if (phoneError != null || addressError != null) return

        if (state.isIdentityConfirmed) {
            emit(PartialState.IdentityConfirmationErrorChanged(false))
            if (state.isEditingFromSummary) {
                returnToSummary()
            } else {
                emit(PartialState.StepChanged(DisabilityPensionStep.Workshop))
            }
        } else {
            emit(PartialState.IdentityConfirmationErrorChanged(true))
        }
    }

    private suspend fun FlowCollector<PartialState>.handleWorkshopNextStep() {
        val state = uiState.value
        val nameError = state.workshopName.isBlank()
        val addressError = state.workshopAddress.isBlank()
        emit(PartialState.WorkshopNameChanged(state.workshopName, nameError))
        emit(PartialState.WorkshopAddressChanged(state.workshopAddress, addressError))

        if (nameError || addressError) return

        if (state.isWorkshopConfirmed) {
            emit(PartialState.WorkshopConfirmationErrorChanged(false))
            if (state.isEditingFromSummary) {
                returnToSummary()
            } else {
                emit(PartialState.StepChanged(DisabilityPensionStep.CommissionRecord))
                loadInsuranceRecord()
            }
        } else {
            emit(PartialState.WorkshopConfirmationErrorChanged(true))
        }
    }

    private suspend fun FlowCollector<PartialState>.handleCommissionRecordNextStep() {
        val state = uiState.value
        val hasObjection = state.hasCommissionObjection
        if (hasObjection == null) {
            emit(PartialState.CommissionValidationErrorChanged(true))
            return
        }
        if (hasObjection) return
        if (state.isEditingFromSummary) {
            returnToSummary()
        } else {
            emit(PartialState.StepChanged(DisabilityPensionStep.Documents))
        }
    }

    private suspend fun FlowCollector<PartialState>.handleDocumentsNextStep() {
        emit(PartialState.DocumentsConfirmDialogVisibilityChanged(true))
    }

    private suspend fun FlowCollector<PartialState>.handleFinalSubmit() {
        if (uiState.value.isSubmitting) return
        if (!uiState.value.isFinalConfirmed) {
            emit(PartialState.FinalConfirmationErrorChanged(true))
            return
        }
        emit(PartialState.FinalConfirmationErrorChanged(false))
        emit(PartialState.SubmittingChanged(true))
        try {
            val requestRef = saveDisabilityUserInfoUseCase(buildSaveInfoRequest()).first()
            val requestId = requireNotNull(requestRef?.id)
            saveDocumentDisabilityUseCase(requestId, buildSaveDocumentRequest()).first()
            val finalConfirmRef = finalConfirmDisabilityRequestUseCase(
                requestId,
                DisabilityFinalConfirmDN(id = requestId, status = FINAL_CONFIRM_STATUS),
            ).first()
            emit(PartialState.SubmitSucceeded(finalConfirmRef?.refCode ?: requestId.toString()))
        } catch (e: Exception) {
            emit(PartialState.SubmittingChanged(false))
            sendEvent(DisabilityPensionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun buildSaveInfoRequest(): DisabilitySaveInfoDN {
        val state = uiState.value
        val info = state.identityInfo
        val personal = info?.personal
        return DisabilitySaveInfoDN(
            activityType = state.activityType,
            address = state.address,
            age = info?.strAge,
            birthDate = applicantBirthDate,
            branchCode = info?.branch,
            fatherName = personal?.fatherName,
            firstName = personal?.firstName,
            gender = personal?.genderCode,
            idNumber = personal?.idCardNumber,
            insuranceNumber = info?.insuranceId,
            issuePlace = personal?.cityOfIssue,
            lastName = personal?.lastName,
            managerName = state.employerName,
            mobileNumber = info?.mobileNumber,
            nationalCode = personal?.nationalId,
            pensionRequestDocList = emptyList(),
            phoneNumber = state.landlinePhone,
            status = SAVE_INFO_STATUS,
            workshopAddress = state.workshopAddress,
            workshopCode = info?.work?.workshopId,
            workshopName = state.workshopName,
        )
    }

    private fun buildSaveDocumentRequest(): DisabilitySaveDocumentDN = DisabilitySaveDocumentDN(
        pensionRequestDocList = uiState.value.documents.toDisabilityDocumentDNs(),
        status = SAVE_DOCUMENT_STATUS,
    )

    private suspend fun FlowCollector<PartialState>.handlePreviousStepClicked() {
        if (uiState.value.isEditingFromSummary) {
            returnToSummary()
            return
        }
        when (uiState.value.currentStep) {
            DisabilityPensionStep.Dependents -> emit(PartialState.StepChanged(DisabilityPensionStep.Terms))
            DisabilityPensionStep.IdentityContact -> emit(PartialState.StepChanged(DisabilityPensionStep.Dependents))
            DisabilityPensionStep.Workshop -> emit(PartialState.StepChanged(DisabilityPensionStep.IdentityContact))
            DisabilityPensionStep.CommissionRecord -> emit(PartialState.StepChanged(DisabilityPensionStep.Workshop))
            DisabilityPensionStep.Documents -> emit(PartialState.StepChanged(DisabilityPensionStep.CommissionRecord))
            DisabilityPensionStep.Summary -> emit(PartialState.StepChanged(DisabilityPensionStep.Documents))
            DisabilityPensionStep.Terms -> Unit
        }
    }

    private suspend fun FlowCollector<PartialState>.confirmRefreshDependents() {
        if (uiState.value.isRefreshingDependents) return
        emit(PartialState.RefreshConfirmDialogVisibilityChanged(false))
        emit(PartialState.RefreshingDependentsChanged(true))
        refreshDependentsUseCase().collect { result ->
            sendEvent(DisabilityPensionEvent.ShowToast(result.message.orEmpty()))
        }
        emit(PartialState.RefreshingDependentsChanged(false))
        loadDependents()
    }

    override fun reduceState(
        currentState: DisabilityPensionUiState,
        partialState: PartialState
    ): DisabilityPensionUiState = when (partialState) {
        is PartialState.ProfileLoading -> currentState.copy(isProfileLoading = partialState.isProfileLoading)
        is PartialState.ApplicantInfoLoaded -> currentState.copy(
            isProfileLoading = false,
            applicantGenderTitle = partialState.genderTitle,
            applicantFullName = partialState.fullName,
        )
        is PartialState.TermsAcceptedChanged -> currentState.copy(isTermsAccepted = partialState.accepted)
        is PartialState.TermsValidationErrorChanged -> currentState.copy(showTermsValidationError = partialState.show)
        is PartialState.RulesVisibilityChanged -> currentState.copy(showRules = partialState.show)
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.DependentsLoading -> currentState.copy(isDependentsLoading = partialState.isLoading)
        is PartialState.DependentsLoaded -> currentState.copy(
            isDependentsLoading = false,
            dependents = partialState.dependents,
        )
        is PartialState.DependentCardToggled -> currentState.copy(
            expandedDependentIds = if (partialState.id in currentState.expandedDependentIds) {
                currentState.expandedDependentIds - partialState.id
            } else {
                currentState.expandedDependentIds + partialState.id
            }.toPersistentSet(),
        )
        is PartialState.DependentsConfirmedChanged -> currentState.copy(isDependentsListConfirmed = partialState.accepted)
        is PartialState.DependentsConfirmationErrorChanged -> currentState.copy(
            showDependentsConfirmationError = partialState.show,
        )
        is PartialState.RefreshConfirmDialogVisibilityChanged -> currentState.copy(
            showRefreshConfirmDialog = partialState.show,
        )
        is PartialState.RefreshingDependentsChanged -> currentState.copy(
            isRefreshingDependents = partialState.isRefreshing,
        )
        is PartialState.IdentityLoaded -> currentState.copy(identityInfo = partialState.info)
        is PartialState.IdentityAgeLoaded -> currentState.copy(identityAgeYears = partialState.years)
        is PartialState.IdentityDetailsExpandedChanged -> currentState.copy(
            isIdentityDetailsExpanded = partialState.expanded,
        )
        is PartialState.LandlinePhoneChanged -> currentState.copy(
            landlinePhone = partialState.value,
            landlinePhoneError = partialState.error,
        )
        is PartialState.AddressChanged -> currentState.copy(
            address = partialState.value,
            addressError = partialState.error,
        )
        is PartialState.IdentityConfirmedChanged -> currentState.copy(isIdentityConfirmed = partialState.accepted)
        is PartialState.IdentityConfirmationErrorChanged -> currentState.copy(
            showIdentityConfirmationError = partialState.show,
        )
        is PartialState.WorkshopNameChanged -> currentState.copy(
            workshopName = partialState.value,
            workshopNameError = partialState.error,
        )
        is PartialState.ActivityTypeChanged -> currentState.copy(activityType = partialState.value)
        is PartialState.EmployerNameChanged -> currentState.copy(employerName = partialState.value)
        is PartialState.WorkshopAddressChanged -> currentState.copy(
            workshopAddress = partialState.value,
            workshopAddressError = partialState.error,
        )
        is PartialState.WorkshopConfirmedChanged -> currentState.copy(isWorkshopConfirmed = partialState.accepted)
        is PartialState.WorkshopConfirmationErrorChanged -> currentState.copy(
            showWorkshopConfirmationError = partialState.show,
        )
        is PartialState.InsuranceRecordLoading -> currentState.copy(isInsuranceRecordLoading = partialState.isLoading)
        is PartialState.InsuranceRecordLoaded -> currentState.copy(
            isInsuranceRecordLoading = false,
            insuranceRecordDays = partialState.days,
            insuranceRecordMonths = partialState.months,
            insuranceRecordYears = partialState.years,
            insuranceRecordTotalDays = partialState.totalDays,
        )
        is PartialState.CommissionObjectionChanged -> currentState.copy(hasCommissionObjection = partialState.hasObjection)
        is PartialState.CommissionValidationErrorChanged -> currentState.copy(
            showCommissionValidationError = partialState.show,
        )
        is PartialState.RegisteredRequestsSheetVisibilityChanged -> currentState.copy(
            showRegisteredRequestsSheet = partialState.show,
        )
        is PartialState.RegisteredRequestsLoading -> currentState.copy(isRegisteredRequestsLoading = partialState.isLoading)
        is PartialState.RegisteredRequestsLoaded -> currentState.copy(
            isRegisteredRequestsLoading = false,
            registeredRequests = partialState.requests,
        )
        is PartialState.MedicalCommissionPdfViewerVisibilityChanged -> currentState.copy(
            showMedicalCommissionPdfViewer = partialState.show,
        )
        is PartialState.MedicalCommissionPdfChanged -> currentState.copy(
            medicalCommissionPdf = partialState.pdf,
            medicalCommissionPdfDownloadFailed = false,
        )
        is PartialState.MedicalCommissionPdfDownloadFailed -> currentState.copy(
            medicalCommissionPdfDownloadFailed = true,
        )
        is PartialState.DocumentSourceRequested -> currentState.copy(
            showDocumentSourceSheet = true,
            activeDocumentId = partialState.documentId,
        )
        is PartialState.DocumentSourceSheetDismissed -> currentState.copy(
            showDocumentSourceSheet = false,
            activeDocumentId = null,
        )
        is PartialState.DocumentStateChanged -> currentState.copy(
            documents = currentState.documents.toPersistentMap().put(partialState.documentId, partialState.state),
            documentPickError = null,
        )
        is PartialState.DocumentPickRejected -> currentState.copy(documentPickError = partialState.message)
        is PartialState.DocumentsConfirmDialogVisibilityChanged -> currentState.copy(
            showDocumentsConfirmDialog = partialState.show,
        )
        is PartialState.FinalConfirmedChanged -> currentState.copy(isFinalConfirmed = partialState.accepted)
        is PartialState.FinalConfirmationErrorChanged -> currentState.copy(
            showFinalConfirmationError = partialState.show,
        )
        is PartialState.SubmittingChanged -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.SubmitSucceeded -> currentState.copy(
            isSubmitting = false,
            submitTrackingCode = partialState.trackingCode,
        )
        is PartialState.ExitConfirmDialogVisibilityChanged -> currentState.copy(
            showExitConfirmDialog = partialState.show,
        )
        is PartialState.EditingFromSummaryChanged -> currentState.copy(
            isEditingFromSummary = partialState.editing,
        )
        is PartialState.Error -> currentState.copy(
            isProfileLoading = false,
            isDependentsLoading = false,
            isRefreshingDependents = false,
            isInsuranceRecordLoading = false,
            isRegisteredRequestsLoading = false,
            isSubmitting = false,
            error = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private suspend fun FlowCollector<PartialState>.loadApplicantInfo() {
        emit(PartialState.ProfileLoading(true))
        getDisabilityPersonalInfoUseCase().collect { info ->
            val personal = info.personal
            applicantBirthDate = personal?.dateOfBirth
            val fullName = listOfNotNull(personal?.firstName, personal?.lastName)
                .joinToString(" ")
                .ifBlank { "-" }
            val genderTitle = if (personal?.genderCode == "02") {
                org.jetbrains.compose.resources.getString(taminx.core.core_ui.Res.string.disability_pension_female_title)
            } else {
                org.jetbrains.compose.resources.getString(taminx.core.core_ui.Res.string.disability_pension_male_title)
            }
            emit(PartialState.ApplicantInfoLoaded(genderTitle = genderTitle, fullName = fullName))
            emit(PartialState.IdentityLoaded(info.toPresentation()))
            emit(PartialState.IdentityAgeLoaded(loadAgeYears(personal?.dateOfBirth)))
        }
    }

    private suspend fun loadAgeYears(birthDate: Long?): String {
        if (birthDate == null) return ""
        var years = ""
        getUserAgeUseCase(
            listOf(
                ApiFilterDN(
                    property = FilterProperty.BIRTH_DATE,
                    value = birthDate.toString(),
                    operator = FilterOperator.EQUAL,
                ),
            ),
        ).collect { age ->
            years = age.age?.split(",")?.getOrNull(0)?.trim().orEmpty()
        }
        return years
    }

    private suspend fun FlowCollector<PartialState>.loadDependents() {
        emit(PartialState.DependentsLoading(true))
        getDisabilityDependentInfoUseCase(emptyList()).collect { dependents ->
            emit(PartialState.DependentsLoaded(dependents.toPresentation().toImmutableList()))
        }
    }

    private suspend fun FlowCollector<PartialState>.loadInsuranceRecord() {
        emit(PartialState.InsuranceRecordLoading(true))
        val record = getTalfighInfosUseCase().list?.firstOrNull()
        emit(
            PartialState.InsuranceRecordLoaded(
                days = record?.historyDays?.toString() ?: "-",
                months = record?.historyMonths?.toString() ?: "-",
                years = record?.historyYears?.toString() ?: "-",
                totalDays = record?.sumHistoryYears?.toString() ?: "-",
            ),
        )
    }

    private suspend fun FlowCollector<PartialState>.loadRegisteredRequests() {
        emit(PartialState.RegisteredRequestsLoading(true))
        getRegisteredMedicalCommissionUseCase().collect { requests ->
            val disabilityRequests = requests
                .filter { it.demandTypeCode == DisabilityDemandType.DISABILITY_PENSION.code }
                .map { it.toPresentation() }
                .toImmutableList()
            emit(PartialState.RegisteredRequestsLoaded(disabilityRequests))
        }
    }

    private suspend fun FlowCollector<PartialState>.downloadMedicalCommissionPdf() {
        emit(PartialState.MedicalCommissionPdfChanged(null))
        try {
            val workshopName = uiState.value.workshopName
            getMedicalCommissionPdfUseCase(workshopName).collect { pdf ->
                emit(PartialState.MedicalCommissionPdfChanged(pdf.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.MedicalCommissionPdfDownloadFailed)
            sendEvent(DisabilityPensionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun validateLandlinePhone(value: String): LandlinePhoneError? = when {
        value.isBlank() -> LandlinePhoneError.Blank
        !value.startsWith("0") -> LandlinePhoneError.InvalidPrefix
        value.length != LANDLINE_PHONE_LENGTH -> LandlinePhoneError.InvalidLength
        else -> null
    }

    private fun validateAddress(value: String): AddressError? = when {
        value.isBlank() -> AddressError.Blank
        value.length < MIN_ADDRESS_LENGTH -> AddressError.TooShort
        INVALID_ADDRESS_CHARACTERS.containsMatchIn(value) -> AddressError.InvalidCharacters
        else -> null
    }

    private companion object {
        const val LANDLINE_PHONE_LENGTH = 11
        const val MIN_ADDRESS_LENGTH = 10
        const val DISABILITY_DEMAND_TYPE_CODE = "01"
        const val MAX_DOCUMENT_SIZE_BYTES = 2 * 1024 * 1024
        const val SAVE_INFO_STATUS = "3"
        const val SAVE_DOCUMENT_STATUS = "4"
        const val FINAL_CONFIRM_STATUS = "0"
        val INVALID_ADDRESS_CHARACTERS = Regex("[a-zA-Z$&+:;=?@#|/'<>.^*()%!\\\\]")
    }
}
