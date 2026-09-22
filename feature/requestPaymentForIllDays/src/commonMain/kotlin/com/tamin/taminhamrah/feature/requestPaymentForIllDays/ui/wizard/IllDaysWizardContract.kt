package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopPR
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class IllDaysWizardStep {
    BranchCity,
    RestDays,
    Doctor,
    Documents,
}

enum class IllDaysWizardPicker {
    None,
    Branch,
    City,
    StartDate,
    EndDate,
    DocumentSource,
}

@Immutable
data class IllDaysUploadedDocumentUi(
    val localId: String,
    val guid: String,
    val documentType: String,
    val title: String,
    val fileName: String,
    val bytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as IllDaysUploadedDocumentUi
        return localId == other.localId &&
            guid == other.guid &&
            documentType == other.documentType &&
            title == other.title &&
            fileName == other.fileName &&
            bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = localId.hashCode()
        result = 31 * result + guid.hashCode()
        result = 31 * result + documentType.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}

@Immutable
data class IllDaysWizardUiState(
    val currentStep: IllDaysWizardStep = IllDaysWizardStep.BranchCity,
    val isLoading: Boolean = true,
    val isCovidLoading: Boolean = false,
    val isUploadingDocument: Boolean = false,
    val isSubmitting: Boolean = false,
    val insuredMainInfo: IllDaysInsuredMainInfoPR? = null,
    val branchOptions: ImmutableList<IllDaysBranchWorkshopPR> = persistentListOf(),
    val cityOptions: ImmutableList<CityPR> = persistentListOf(),
    val selectedBranch: IllDaysBranchWorkshopPR? = null,
    val selectedCity: CityPR? = null,
    val isCovid: Boolean = false,
    val startDateLabel: String = "",
    val startDateMillis: Long? = null,
    val endDateLabel: String = "",
    val endDateMillis: Long? = null,
    val dayCount: Int? = null,
    val doctorName: String = "",
    val doctorCode: String = "",
    val hasMedicalRecord: Boolean = false,
    val documents: ImmutableList<IllDaysUploadedDocumentUi> = persistentListOf(),
    val uploadingFileName: String = "",
    val uploadingBytes: ByteArray? = null,
    val previewDocumentLocalId: String? = null,
    val picker: IllDaysWizardPicker = IllDaysWizardPicker.None,
    val errorMessage: String? = null,
) {
    val canGoNextFromStep1: Boolean
        get() = selectedBranch != null && selectedCity != null

    val canGoNextFromStep2: Boolean
        get() = startDateMillis != null && endDateMillis != null &&
            (endDateMillis ?: 0L) >= (startDateMillis ?: 0L)

    val canGoNextFromStep3: Boolean
        get() = doctorName.isNotBlank() && doctorCode.isNotBlank()

    val canSubmit: Boolean
        get() = documents.isNotEmpty() && !isUploadingDocument && !isSubmitting

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class CovidLoading(val isLoading: Boolean) : PartialState
        data class UploadingDocument(val isUploading: Boolean) : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class InsuredLoaded(val info: IllDaysInsuredMainInfoPR) : PartialState
        data class BranchesLoaded(
            val branches: ImmutableList<IllDaysBranchWorkshopPR>,
            val selected: IllDaysBranchWorkshopPR?,
        ) : PartialState
        data class CitiesLoaded(val cities: ImmutableList<CityPR>) : PartialState
        data class BranchSelected(val branch: IllDaysBranchWorkshopPR) : PartialState
        data class CitySelected(val city: CityPR) : PartialState
        data class StepChanged(val step: IllDaysWizardStep) : PartialState
        data class PickerChanged(val picker: IllDaysWizardPicker) : PartialState
        data class CovidToggled(val enabled: Boolean) : PartialState
        data class RestDatesSet(
            val startMillis: Long?,
            val startLabel: String,
            val endMillis: Long?,
            val endLabel: String,
            val dayCount: Int?,
        ) : PartialState
        data class DoctorNameChanged(val value: String) : PartialState
        data class DoctorCodeChanged(val value: String) : PartialState
        data class MedicalRecordToggled(val enabled: Boolean) : PartialState
        data class DocumentsChanged(
            val documents: ImmutableList<IllDaysUploadedDocumentUi>,
        ) : PartialState
        data class UploadProgress(
            val fileName: String,
            val bytes: ByteArray?,
        ) : PartialState
        data class PreviewDocument(val localId: String?) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface IllDaysWizardIntent {
    data object Load : IllDaysWizardIntent
    data object Retry : IllDaysWizardIntent
    data object OpenBranchPicker : IllDaysWizardIntent
    data object OpenCityPicker : IllDaysWizardIntent
    data object DismissPicker : IllDaysWizardIntent
    data class BranchPicked(val branch: IllDaysBranchWorkshopPR) : IllDaysWizardIntent
    data class CityPicked(val city: CityPR) : IllDaysWizardIntent
    data object NextStep : IllDaysWizardIntent
    data object PreviousStep : IllDaysWizardIntent
    data class CovidChanged(val enabled: Boolean) : IllDaysWizardIntent
    data object OpenStartDatePicker : IllDaysWizardIntent
    data object OpenEndDatePicker : IllDaysWizardIntent
    data class StartDatePicked(val millis: Long, val label: String) : IllDaysWizardIntent
    data class EndDatePicked(val millis: Long, val label: String) : IllDaysWizardIntent
    data class DoctorNameChanged(val value: String) : IllDaysWizardIntent
    data class DoctorCodeChanged(val value: String) : IllDaysWizardIntent
    data class MedicalRecordChanged(val enabled: Boolean) : IllDaysWizardIntent
    data object OpenDocumentSource : IllDaysWizardIntent
    data object OpenCamera : IllDaysWizardIntent
    data object OpenGallery : IllDaysWizardIntent
    data class DocumentImagePicked(val fileName: String, val bytes: ByteArray) : IllDaysWizardIntent
    data class RemoveDocument(val localId: String) : IllDaysWizardIntent
    data class PreviewDocument(val localId: String) : IllDaysWizardIntent
    data object DismissPreview : IllDaysWizardIntent
    data object Submit : IllDaysWizardIntent
    data object OpenCalculate : IllDaysWizardIntent
    data object Back : IllDaysWizardIntent
}

sealed interface IllDaysWizardEvent {
    data object NavigateBack : IllDaysWizardEvent
    data object NavigateToCalculate : IllDaysWizardEvent
    data object LaunchCamera : IllDaysWizardEvent
    data object LaunchGallery : IllDaysWizardEvent
    data class ShowToast(val message: String) : IllDaysWizardEvent
    data class ShowSuccess(val message: String) : IllDaysWizardEvent
}
