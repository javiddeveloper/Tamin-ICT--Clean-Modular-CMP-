package com.tamin.taminhamrah.feature.pensionSurvivor.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedDocumentType
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedUploadedDocument
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState.PartialState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SharedDeceasedDocument
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.personal.GetAgeUseCase
import com.tamin.taminhamrah.useCases.personal.GetConfirmSurvivorsListUseCase
import com.tamin.taminhamrah.useCases.personal.GetDeceasedInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetFinalSurvivorPensionPDFUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetSurvivorListUseCase
import com.tamin.taminhamrah.useCases.personal.SubmitFinalSurvivorPensionUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_upload_all_docs
import taminx.core.core_ui.occurrence_doc_format_error
import taminx.core.core_ui.pension_survivor_final_multiple_requests_latest_selected
import taminx.core.core_ui.pension_survivor_final_request_unavailable

class PensionSurvivorViewModel(
    private val getPersonalInfoUseCase: GetPersonalInfoUseCase,
    private val getDeceasedInfoUseCase: GetDeceasedInfoUseCase,
    private val getAgeUseCase: GetAgeUseCase,
    private val getSurvivorListUseCase: GetSurvivorListUseCase,
    private val getConfirmSurvivorsListUseCase: GetConfirmSurvivorsListUseCase,
    private val getFinalSurvivorPensionPDFUseCase: GetFinalSurvivorPensionPDFUseCase,
    private val submitFinalSurvivorPensionUseCase: SubmitFinalSurvivorPensionUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
) : BaseViewModel<
    PensionSurvivorUiState,
    PartialState,
    PensionSurvivorEvent,
    PensionSurvivorIntent,
>(
    initialState = PensionSurvivorUiState(),
) {
    init {
        sendIntent(PensionSurvivorIntent.Init)
    }

    override fun handleIntent(intent: PensionSurvivorIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(PensionSurvivorEvent.ShowToast(e.toSingleLineMessage()))
            emit(createErrorState(e.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: PensionSurvivorIntent): Flow<PartialState> = flow {
        when (intent) {
            PensionSurvivorIntent.Init -> loadApplicantProfile()
            is PensionSurvivorIntent.CommitmentChanged -> {
                emit(PartialState.CommitmentChanged(intent.accepted))
            }
            PensionSurvivorIntent.ViewRules -> {
                sendEvent(PensionSurvivorEvent.OpenRulesDocument)
            }
            PensionSurvivorIntent.NextStep -> goToNextStep()
            PensionSurvivorIntent.PreviousStep -> goToPreviousStep()
            is PensionSurvivorIntent.DeceasedNationalIdChanged -> {
                emit(
                    PartialState.DeceasedNationalIdChanged(
                        intent.value.filter(Char::isDigit).take(MAX_NATIONAL_ID_LENGTH),
                    ),
                )
            }
            PensionSurvivorIntent.SearchDeceased -> searchDeceased()
            is PensionSurvivorIntent.DeceasedHistoryConfirmedChanged -> {
                emit(PartialState.DeceasedHistoryConfirmedChanged(intent.confirmed))
            }
            is PensionSurvivorIntent.DeceasedDocumentClicked -> {
                emit(PartialState.DeceasedDocumentSourceRequested(intent.type))
            }
            is PensionSurvivorIntent.DeceasedDocumentImagePicked -> uploadDeceasedDocument(intent)
            PensionSurvivorIntent.DismissDeceasedDocumentSource -> {
                emit(PartialState.DeceasedDocumentSourceDismissed)
            }
            is PensionSurvivorIntent.RemoveDeceasedDocument -> {
                emit(PartialState.DeceasedDocumentRemoved(intent.type))
            }
            is PensionSurvivorIntent.OpenSurvivor -> {
                if (intent.item.nationalId.isNotBlank()) {
                    val state = uiState.value
                    val sharedDeceasedDocuments =
                        if (state.applicantNationalId.isNotBlank() &&
                            state.applicantNationalId == intent.item.nationalId
                        ) {
                            state.deceasedDocuments.values.map { doc ->
                                SharedDeceasedDocument(
                                    documentTypeCode = doc.type.code,
                                    guid = doc.guid,
                                )
                            }.toImmutableList()
                        } else {
                            persistentListOf()
                        }
                    sendEvent(
                        PensionSurvivorEvent.NavigateToSurvivorInfo(
                            survivor = intent.item,
                            deceasedNationalId = state.deceasedNationalId,
                            draft = state.survivorContactDrafts[intent.item.nationalId],
                            branchCode = state.deceasedInfo?.branchCode.orEmpty(),
                            deceasedInsuranceId = state.deceasedInfo?.insuranceId.orEmpty(),
                            sharedDeceasedDocuments = sharedDeceasedDocuments,
                        ),
                    )
                }
            }
            is PensionSurvivorIntent.SurvivorContactSaved -> {
                emit(PartialState.SurvivorContactSaved(intent.nationalId, intent.draft))
            }
            PensionSurvivorIntent.RefreshSurvivors -> loadSurvivors()
            PensionSurvivorIntent.DownloadFinalPdf -> downloadFinalPdf()
            PensionSurvivorIntent.RetryPdfDownload -> downloadFinalPdf()
            PensionSurvivorIntent.DismissPdfViewer -> {
                emit(PartialState.ViewerPdfChanged(null))
            }
            is PensionSurvivorIntent.PdfConfirmedChanged -> {
                emit(PartialState.PdfConfirmedChanged(intent.confirmed))
            }
            PensionSurvivorIntent.SubmitFinal -> submitFinalRequest()
            PensionSurvivorIntent.DismissSuccessDialog -> {
                emit(PartialState.ShowSuccessDialog(false))
                sendEvent(PensionSurvivorEvent.NavigateBack)
            }
            PensionSurvivorIntent.OnBack -> {
                sendEvent(PensionSurvivorEvent.NavigateBack)
            }
        }
    }

    override fun reduceState(
        currentState: PensionSurvivorUiState,
        partialState: PartialState,
    ): PensionSurvivorUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            viewerDownloadFailed = false,
        )
        is PartialState.ProfileLoading -> currentState.copy(isProfileLoading = partialState.isProfileLoading)
        is PartialState.ApplicantLoaded -> currentState.copy(
            applicantFullName = partialState.fullName,
            applicantNationalId = partialState.nationalId,
            isProfileLoading = false,
            isLoading = false,
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step,
            isLoading = false,
        )
        is PartialState.CommitmentChanged -> currentState.copy(commitmentAccepted = partialState.accepted)
        is PartialState.DeceasedNationalIdChanged -> currentState.copy(
            deceasedNationalId = partialState.value,
            deceasedInfo = null,
            isDeceasedHistoryConfirmed = false,
            deceasedDocuments = persistentMapOf(),
            uploadingDeceasedDocument = null,
            failedDeceasedDocument = null,
            activeDeceasedDocument = null,
            deceasedDocumentError = null,
            survivors = persistentListOf(),
            requestId = null,
            viewerPdf = null,
            viewerDownloadFailed = false,
            isPdfConfirmed = false,
            showSuccessDialog = false,
            finalPdfRevision = currentState.finalPdfRevision + 1,
        )
        is PartialState.DeceasedLoaded -> currentState.copy(
            deceasedInfo = partialState.info,
            isDeceasedHistoryConfirmed = false,
            isLoading = false,
        )
        is PartialState.DeceasedHistoryConfirmedChanged -> currentState.copy(
            isDeceasedHistoryConfirmed = partialState.confirmed,
        )
        PartialState.DeceasedDocumentsCleared -> currentState.copy(
            deceasedDocuments = persistentMapOf(),
            uploadingDeceasedDocument = null,
            failedDeceasedDocument = null,
            activeDeceasedDocument = null,
            deceasedDocumentError = null,
        )
        is PartialState.DeceasedDocumentSourceRequested -> currentState.copy(
            activeDeceasedDocument = partialState.type,
            deceasedDocumentError = null,
        )
        PartialState.DeceasedDocumentSourceDismissed -> currentState.copy(
            activeDeceasedDocument = null,
        )
        is PartialState.DeceasedDocumentUploadStarted -> currentState.copy(
            uploadingDeceasedDocument = partialState.type,
            failedDeceasedDocument = null,
            activeDeceasedDocument = null,
            deceasedDocumentError = null,
        )
        is PartialState.DeceasedDocumentUploaded -> currentState.copy(
            deceasedDocuments = currentState.deceasedDocuments.toPersistentMap().put(
                partialState.document.type,
                partialState.document,
            ),
            uploadingDeceasedDocument = null,
            failedDeceasedDocument = null,
            deceasedDocumentError = null,
        )
        is PartialState.DeceasedDocumentUploadFailed -> currentState.copy(
            uploadingDeceasedDocument = null,
            failedDeceasedDocument = partialState.type,
            deceasedDocumentError = partialState.message,
        )
        is PartialState.DeceasedDocumentRemoved -> currentState.copy(
            deceasedDocuments = currentState.deceasedDocuments.toPersistentMap().remove(partialState.type),
            uploadingDeceasedDocument = null,
            failedDeceasedDocument = null,
            activeDeceasedDocument = null,
            deceasedDocumentError = null,
        )
        is PartialState.SurvivorsLoaded -> currentState.copy(
            survivors = partialState.items,
            isLoading = false,
            requestId = null,
            viewerPdf = null,
            viewerDownloadFailed = false,
            isPdfConfirmed = false,
            showSuccessDialog = false,
            finalPdfRevision = currentState.finalPdfRevision + 1,
        )
        is PartialState.SurvivorContactSaved -> currentState.copy(
            survivorContactDrafts = currentState.survivorContactDrafts.toPersistentMap().put(
                partialState.nationalId,
                partialState.draft,
            ),
        )
        is PartialState.RequestIdLoaded -> currentState.copy(
            requestId = partialState.requestId,
            isLoading = false,
        )
        is PartialState.ViewerPdfChanged -> currentState.copy(
            viewerPdf = partialState.pdf,
            isLoading = false,
            viewerDownloadFailed = false,
            isPdfConfirmed = if (partialState.pdf != null) false else currentState.isPdfConfirmed,
        )
        PartialState.ViewerDownloadFailed -> currentState.copy(
            isLoading = false,
            viewerDownloadFailed = true,
        )
        is PartialState.PdfConfirmedChanged -> currentState.copy(isPdfConfirmed = partialState.confirmed)
        is PartialState.ShowSuccessDialog -> currentState.copy(
            showSuccessDialog = partialState.show,
            isLoading = false,
        )
        is PartialState.Error -> currentState.copy(
            isProfileLoading = false,
            isLoading = false,
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

    private suspend fun FlowCollector<PartialState>.loadApplicantProfile() {
        emit(PartialState.ProfileLoading(true))
        getPersonalInfoUseCase(refreshRemote = true).collect { info ->
            val personal = info?.personal
            val fullName = listOfNotNull(personal?.firstName, personal?.lastName)
                .joinToString(" ")
                .trim()

            emit(
                PartialState.ApplicantLoaded(
                    fullName = fullName,
                    nationalId = personal?.nationalId.orEmpty(),
                ),
            )
        }
    }

    private suspend fun FlowCollector<PartialState>.goToNextStep() {
        val state = uiState.value
        if (state.isLoading) return

        when (state.currentStep) {
            PensionSurvivorStep.Rules -> {
                if (state.commitmentAccepted) {
                    emit(PartialState.StepChanged(PensionSurvivorStep.Deceased))
                }
            }

            PensionSurvivorStep.Deceased -> {
                if (state.deceasedInfo == null) return
                if (!state.isDeceasedHistoryConfirmed) return
                if (!state.areDeceasedDocumentsComplete) {
                    sendEvent(PensionSurvivorEvent.ShowToast(getString(Res.string.error_upload_all_docs)))
                    return
                }
                emit(PartialState.StepChanged(PensionSurvivorStep.Survivors))
                loadSurvivors()
            }

            PensionSurvivorStep.Survivors -> {
                emit(PartialState.StepChanged(PensionSurvivorStep.Final))
                loadRequestId()
            }

            PensionSurvivorStep.Final -> Unit
        }
    }

    private suspend fun FlowCollector<PartialState>.goToPreviousStep() {
        when (uiState.value.currentStep) {
            PensionSurvivorStep.Rules -> Unit
            PensionSurvivorStep.Deceased -> emit(PartialState.StepChanged(PensionSurvivorStep.Rules))
            PensionSurvivorStep.Survivors -> emit(PartialState.StepChanged(PensionSurvivorStep.Deceased))
            PensionSurvivorStep.Final -> emit(PartialState.StepChanged(PensionSurvivorStep.Survivors))
        }
    }

    private suspend fun FlowCollector<PartialState>.uploadDeceasedDocument(
        intent: PensionSurvivorIntent.DeceasedDocumentImagePicked,
    ) {
        if (uiState.value.isDeceasedDocumentUploading) return

        if (!isJpegFileName(intent.fileName)) {
            emit(
                PartialState.DeceasedDocumentUploadFailed(
                    type = intent.type,
                    message = getString(Res.string.occurrence_doc_format_error),
                ),
            )
            return
        }

        if (intent.bytes.size > MAX_DOCUMENT_SIZE_BYTES) {
            emit(
                PartialState.DeceasedDocumentUploadFailed(
                    type = intent.type,
                    message = getString(Res.string.occurrence_doc_format_error),
                ),
            )
            return
        }

        emit(PartialState.DeceasedDocumentUploadStarted(intent.type))
        try {
            val guid = uploadImageUseCase(
                UploadImageRequestDN(
                    fileName = intent.fileName,
                    bytes = intent.bytes,
                ),
            ).first()
            emit(
                PartialState.DeceasedDocumentUploaded(
                    DeceasedUploadedDocument(
                        type = intent.type,
                        fileName = intent.fileName,
                        bytes = intent.bytes,
                        guid = guid,
                    ),
                ),
            )
        } catch (e: Exception) {
            emit(
                PartialState.DeceasedDocumentUploadFailed(
                    type = intent.type,
                    message = e.toSingleLineMessage(),
                ),
            )
        }
    }

    private suspend fun FlowCollector<PartialState>.searchDeceased() {
        val state = uiState.value
        if (state.isLoading || state.deceasedNationalId.isBlank()) return

        emit(PartialState.Loading(true))
        getDeceasedInfoUseCase(state.deceasedNationalId).collect { deceasedInfo ->
            emit(PartialState.DeceasedLoaded(deceasedInfo.toPresentationWithResolvedAge()))
        }
    }

    private suspend fun FlowCollector<PartialState>.loadSurvivors() {
        val state = uiState.value
        if (state.isLoading || state.deceasedNationalId.isBlank()) return

        emit(PartialState.Loading(true))
        getSurvivorListUseCase(state.deceasedNationalId).collect { survivors ->
            emit(PartialState.SurvivorsLoaded(survivors.toPresentation().toImmutableList()))
        }
    }

    private suspend fun FlowCollector<PartialState>.loadRequestId() {
        if (uiState.value.isLoading) return

        emit(PartialState.Loading(true))
        // Legacy uses emptyList() here to keep confirmSurvivorsList pagination defaults; if
        // multiple rows still arrive, submit against the latest available max request id.
        getConfirmSurvivorsListUseCase(emptyList()).collect { confirmedItems ->
            val requestIds = confirmedItems.mapNotNull { item -> item.request?.id }
            val requestId = requestIds.maxOrNull()
            emit(
                PartialState.RequestIdLoaded(
                    requestId = requestId,
                ),
            )
            if (requestIds.size > 1) {
                sendEvent(
                    PensionSurvivorEvent.ShowToast(
                        getString(Res.string.pension_survivor_final_multiple_requests_latest_selected),
                    ),
                )
            }
            if (requestId == null) {
                sendEvent(
                    PensionSurvivorEvent.ShowToast(
                        getString(Res.string.pension_survivor_final_request_unavailable),
                    ),
                )
            }
        }
    }

    private suspend fun FlowCollector<PartialState>.downloadFinalPdf() {
        if (uiState.value.isLoading) return

        emit(PartialState.Loading(true))
        emit(PartialState.ViewerPdfChanged(null))
        try {
            getFinalSurvivorPensionPDFUseCase().collect { pdf ->
                emit(PartialState.ViewerPdfChanged(pdf.toPresentation()))
                sendEvent(PensionSurvivorEvent.OpenPdfViewer)
            }
        } catch (e: Exception) {
            emit(PartialState.ViewerDownloadFailed)
            sendEvent(PensionSurvivorEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private suspend fun FlowCollector<PartialState>.submitFinalRequest() {
        val state = uiState.value
        val requestId = state.requestId ?: return
        if (state.isLoading || !state.isPdfConfirmed) return

        emit(PartialState.Loading(true))
        submitFinalSurvivorPensionUseCase(
            requestId = requestId,
            body = SubmitFinalSurvivorPensionDN(id = requestId),
        ).collect {
            emit(PartialState.ShowSuccessDialog(true))
        }
    }

    private suspend fun toPresentationAgeParts(birthDate: Long?): AgeParts {
        if (birthDate == null) return AgeParts()

        var resolved = AgeParts()
        getAgeUseCase(birthDate).collect { age ->
            resolved = age.age.toAgeParts()
        }
        return resolved
    }

    private suspend fun com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN.toPresentationWithResolvedAge(): DeceasedInfoPR {
        val presentation = toPresentation()
        val ageParts = toPresentationAgeParts(personal?.dateOfBirth)
        return presentation.copy(
            yearsAge = ageParts.years ?: presentation.yearsAge,
            monthsAge = ageParts.months ?: presentation.monthsAge,
            daysAge = ageParts.days ?: presentation.daysAge,
        )
    }

    private fun String?.toAgeParts(): AgeParts {
        val values = this
            ?.split(",")
            ?.map { it.trim() }
            .orEmpty()

        return AgeParts(
            years = values.getOrNull(0)?.takeIf { it.isNotBlank() },
            months = values.getOrNull(1)?.takeIf { it.isNotBlank() },
            days = values.getOrNull(2)?.takeIf { it.isNotBlank() },
        )
    }

    private data class AgeParts(
        val years: String? = null,
        val months: String? = null,
        val days: String? = null,
    )

    private companion object {
        const val MAX_NATIONAL_ID_LENGTH = 10
        const val MAX_DOCUMENT_SIZE_BYTES = 2 * 1024 * 1024

        fun isJpegFileName(fileName: String): Boolean {
            val lower = fileName.lowercase()
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
        }
    }
}
