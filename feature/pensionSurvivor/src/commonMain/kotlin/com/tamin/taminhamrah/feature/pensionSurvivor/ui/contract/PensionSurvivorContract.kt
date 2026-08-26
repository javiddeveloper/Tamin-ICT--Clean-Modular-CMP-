package com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SharedDeceasedDocument
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

enum class PensionSurvivorStep {
    Rules,
    Deceased,
    DeceasedDocuments,
    Survivors,
    Final,
}

enum class DeceasedDocumentType(val code: String) {
    DeathCertificate("23"),
    IdFirstPage("24"),
    IdChildrenPage("25"),
}

val DeceasedDocumentChecklist: ImmutableList<DeceasedDocumentType> = persistentListOf(
    DeceasedDocumentType.DeathCertificate,
    DeceasedDocumentType.IdFirstPage,
    DeceasedDocumentType.IdChildrenPage,
)

@Immutable
data class DeceasedUploadedDocument(
    val type: DeceasedDocumentType,
    val fileName: String,
    val bytes: ByteArray,
    val guid: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DeceasedUploadedDocument) return false
        return type == other.type &&
            fileName == other.fileName &&
            guid == other.guid &&
            bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + guid.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}

data class PensionSurvivorUiState(
    val currentStep: PensionSurvivorStep = PensionSurvivorStep.Rules,
    val isLoading: Boolean = false,
    val isProfileLoading: Boolean = true,
    val applicantFullName: String = "",
    val applicantNationalId: String = "",
    val commitmentAccepted: Boolean = false,
    val deceasedNationalId: String = "",
    val deceasedInfo: DeceasedInfoPR? = null,
    val isDeceasedHistoryConfirmed: Boolean = false,
    val deceasedDocuments: ImmutableMap<DeceasedDocumentType, DeceasedUploadedDocument> = persistentMapOf(),
    val uploadingDeceasedDocument: DeceasedDocumentType? = null,
    val failedDeceasedDocument: DeceasedDocumentType? = null,
    val activeDeceasedDocument: DeceasedDocumentType? = null,
    val deceasedDocumentError: String? = null,
    val survivors: ImmutableList<SurvivorDependentPR> = persistentListOf(),
    val survivorContactDrafts: ImmutableMap<String, SurvivorContactDraft> = persistentMapOf(),
    val requestId: Int? = null,
    val viewerPdf: PdfDownloadPR? = null,
    val viewerDownloadFailed: Boolean = false,
    val isPdfConfirmed: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val finalPdfRevision: Int = 0,
) {
    val deceasedDocumentsUploadedCount: Int
        get() = deceasedDocuments.size

    val areDeceasedDocumentsComplete: Boolean
        get() = DeceasedDocumentChecklist.all { deceasedDocuments.containsKey(it) }

    val isDeceasedDocumentUploading: Boolean
        get() = uploadingDeceasedDocument != null

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class ProfileLoading(val isProfileLoading: Boolean) : PartialState
        data class ApplicantLoaded(
            val fullName: String,
            val nationalId: String,
        ) : PartialState
        data class StepChanged(val step: PensionSurvivorStep) : PartialState
        data class CommitmentChanged(val accepted: Boolean) : PartialState
        data class DeceasedNationalIdChanged(val value: String) : PartialState
        data class DeceasedLoaded(val info: DeceasedInfoPR?) : PartialState
        data class DeceasedHistoryConfirmedChanged(val confirmed: Boolean) : PartialState
        data object DeceasedDocumentsCleared : PartialState
        data class DeceasedDocumentSourceRequested(val type: DeceasedDocumentType) : PartialState
        data object DeceasedDocumentSourceDismissed : PartialState
        data class DeceasedDocumentUploadStarted(val type: DeceasedDocumentType) : PartialState
        data class DeceasedDocumentUploaded(val document: DeceasedUploadedDocument) : PartialState
        data class DeceasedDocumentUploadFailed(
            val type: DeceasedDocumentType,
            val message: String,
        ) : PartialState
        data class DeceasedDocumentRemoved(val type: DeceasedDocumentType) : PartialState
        data class SurvivorsLoaded(val items: ImmutableList<SurvivorDependentPR>) : PartialState
        data class SurvivorContactSaved(
            val nationalId: String,
            val draft: SurvivorContactDraft,
        ) : PartialState
        data class RequestIdLoaded(val requestId: Int?) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object ViewerDownloadFailed : PartialState
        data class PdfConfirmedChanged(val confirmed: Boolean) : PartialState
        data class ShowSuccessDialog(val show: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface PensionSurvivorIntent {
    data object Init : PensionSurvivorIntent
    data class CommitmentChanged(val accepted: Boolean) : PensionSurvivorIntent
    data object ViewRules : PensionSurvivorIntent
    data object NextStep : PensionSurvivorIntent
    data object PreviousStep : PensionSurvivorIntent
    data class DeceasedNationalIdChanged(val value: String) : PensionSurvivorIntent
    data object SearchDeceased : PensionSurvivorIntent
    data class DeceasedHistoryConfirmedChanged(val confirmed: Boolean) : PensionSurvivorIntent
    data class DeceasedDocumentClicked(val type: DeceasedDocumentType) : PensionSurvivorIntent
    data class DeceasedDocumentImagePicked(
        val type: DeceasedDocumentType,
        val fileName: String,
        val bytes: ByteArray,
    ) : PensionSurvivorIntent
    data object DismissDeceasedDocumentSource : PensionSurvivorIntent
    data class RemoveDeceasedDocument(val type: DeceasedDocumentType) : PensionSurvivorIntent
    data class OpenSurvivor(val item: SurvivorDependentPR) : PensionSurvivorIntent
    data class SurvivorContactSaved(
        val nationalId: String,
        val draft: SurvivorContactDraft,
    ) : PensionSurvivorIntent
    data object RefreshSurvivors : PensionSurvivorIntent
    data object DownloadFinalPdf : PensionSurvivorIntent
    data object RetryPdfDownload : PensionSurvivorIntent
    data object DismissPdfViewer : PensionSurvivorIntent
    data class PdfConfirmedChanged(val confirmed: Boolean) : PensionSurvivorIntent
    data object SubmitFinal : PensionSurvivorIntent
    data object DismissSuccessDialog : PensionSurvivorIntent
    data object OnBack : PensionSurvivorIntent
}

sealed interface PensionSurvivorEvent {
    data class ShowToast(val message: String) : PensionSurvivorEvent
    data object NavigateBack : PensionSurvivorEvent
    data class NavigateToSurvivorInfo(
        val survivor: SurvivorDependentPR,
        val deceasedNationalId: String,
        val draft: SurvivorContactDraft?,
        val branchCode: String,
        val deceasedInsuranceId: String,
        val sharedDeceasedDocuments: ImmutableList<SharedDeceasedDocument>,
    ) : PensionSurvivorEvent
    data object OpenPdfViewer : PensionSurvivorEvent
    data object OpenRulesDocument : PensionSurvivorEvent
}

data class SurvivorContactDraft(
    val address: String,
    val phoneNumber: String,
    val mobileNumber: String,
)
