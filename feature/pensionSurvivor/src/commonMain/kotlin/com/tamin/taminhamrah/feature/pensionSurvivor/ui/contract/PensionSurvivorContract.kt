package com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract

import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class PensionSurvivorStep {
    Rules,
    Deceased,
    Survivors,
    Final,
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
    val survivors: ImmutableList<SurvivorDependentPR> = persistentListOf(),
    val requestId: Int? = null,
    val viewerPdf: PdfDownloadPR? = null,
    val isPdfConfirmed: Boolean = false,
    val showSuccessDialog: Boolean = false,
) {
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
        data class SurvivorsLoaded(val items: ImmutableList<SurvivorDependentPR>) : PartialState
        data class RequestIdLoaded(val requestId: Int?) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
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
    data class OpenSurvivor(val item: SurvivorDependentPR) : PensionSurvivorIntent
    data object RefreshSurvivors : PensionSurvivorIntent
    data object DownloadFinalPdf : PensionSurvivorIntent
    data object DismissPdfViewer : PensionSurvivorIntent
    data class PdfConfirmedChanged(val confirmed: Boolean) : PensionSurvivorIntent
    data object SubmitFinal : PensionSurvivorIntent
    data object DismissSuccessDialog : PensionSurvivorIntent
    data object OnBack : PensionSurvivorIntent
}

sealed interface PensionSurvivorEvent {
    data class ShowToast(val message: String) : PensionSurvivorEvent
    data object NavigateBack : PensionSurvivorEvent
    data class NavigateToSurvivorInfo(val nationalId: String) : PensionSurvivorEvent
    data object OpenPdfViewer : PensionSurvivorEvent
    data object OpenRulesDocument : PensionSurvivorEvent
}
