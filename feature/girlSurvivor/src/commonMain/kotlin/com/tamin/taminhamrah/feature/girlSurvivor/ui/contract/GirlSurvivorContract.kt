package com.tamin.taminhamrah.feature.girlSurvivor.ui.contract

import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class GirlSurvivorStep {
    Details,
    Commitment,
}

data class GirlSurvivorProfileRowPR(
    val label: String,
    val value: String,
    val numeric: Boolean = true,
)

data class GirlSurvivorFieldErrors(
    val address: String? = null,
    val zipCode: String? = null,
    val phoneNumber: String? = null,
    val deceasedNationalCode: String? = null,
    val deceasedPensionId: String? = null,
)

data class GirlSurvivorUiState(
    val currentStep: GirlSurvivorStep = GirlSurvivorStep.Details,
    val isProfileLoading: Boolean = true,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val profileRows: ImmutableList<GirlSurvivorProfileRowPR> = persistentListOf(),
    val fullName: String = "",
    val fatherName: String = "",
    val nationalId: String = "",
    val address: String = "",
    val zipCode: String = "",
    val phoneNumber: String = "",
    val usePensionIdMode: Boolean = false,
    val deceasedNationalCode: String = "",
    val deceasedPensionId: String = "",
    val fieldErrors: GirlSurvivorFieldErrors = GirlSurvivorFieldErrors(),
    val viewerPdf: PdfDownloadPR? = null,
    val viewerDownloadFailed: Boolean = false,
    val isPdfConfirmed: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val confirmPayload: ConfirmGirlSurvivorDN? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class ProfileLoading(val isProfileLoading: Boolean) : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class StepChanged(val step: GirlSurvivorStep) : PartialState
        data class ProfileLoaded(
            val rows: ImmutableList<GirlSurvivorProfileRowPR>,
            val fullName: String,
            val fatherName: String,
            val nationalId: String,
            val phoneNumber: String,
            val confirmPayloadBase: ConfirmGirlSurvivorDN,
        ) : PartialState
        data class AddressChanged(val value: String) : PartialState
        data class ZipCodeChanged(val value: String) : PartialState
        data class PhoneNumberChanged(val value: String) : PartialState
        data class DeceasedNationalCodeChanged(val value: String) : PartialState
        data class DeceasedPensionIdChanged(val value: String) : PartialState
        data class UsePensionIdModeChanged(val enabled: Boolean) : PartialState
        data class FieldErrorsChanged(val errors: GirlSurvivorFieldErrors) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object ViewerDownloadFailed : PartialState
        data class PdfConfirmedChanged(val confirmed: Boolean) : PartialState
        data class ConfirmPayloadUpdated(val payload: ConfirmGirlSurvivorDN) : PartialState
        data class ShowSuccessDialog(val show: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface GirlSurvivorIntent {
    data object Init : GirlSurvivorIntent
    data class AddressChanged(val value: String) : GirlSurvivorIntent
    data class ZipCodeChanged(val value: String) : GirlSurvivorIntent
    data class PhoneNumberChanged(val value: String) : GirlSurvivorIntent
    data class DeceasedNationalCodeChanged(val value: String) : GirlSurvivorIntent
    data class DeceasedPensionIdChanged(val value: String) : GirlSurvivorIntent
    data class UsePensionIdModeChanged(val enabled: Boolean) : GirlSurvivorIntent
    data object DownloadAndViewForm : GirlSurvivorIntent
    data object RetryPdfDownload : GirlSurvivorIntent
    data object DismissPdfViewer : GirlSurvivorIntent
    data class PdfConfirmedChanged(val confirmed: Boolean) : GirlSurvivorIntent
    data object SubmitRequest : GirlSurvivorIntent
    data object GoToPreviousStep : GirlSurvivorIntent
    data object DismissSuccessDialog : GirlSurvivorIntent
}

sealed interface GirlSurvivorEvent {
    data class ShowToast(val message: String) : GirlSurvivorEvent
    data object NavigateBack : GirlSurvivorEvent
    data object OpenPdfViewer : GirlSurvivorEvent
}
