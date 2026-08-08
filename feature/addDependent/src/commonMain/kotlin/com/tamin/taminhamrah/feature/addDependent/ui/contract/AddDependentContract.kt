package com.tamin.taminhamrah.feature.addDependent.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.addDependent.ui.model.BranchPR
import com.tamin.taminhamrah.feature.addDependent.ui.model.FamilyRelationshipPR
import com.tamin.taminhamrah.feature.addDependent.ui.model.RegistryDataPR
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetConfig

const val STEP_INQUIRY = 1
const val STEP_VERIFICATION = 2
const val STEP_DOCUMENTS = 3
const val STEP_SUCCESS = 4

enum class StepperMode {
    DEFAULT_MODE,
    SON_MODE,
    DAUGHTER_MODE
}

/**
 * Identifies which picker opened the shared bottom sheet, so the screen can route the
 * selection back to the right intent without matching on the (localized) sheet title.
 */
enum class BottomSheetTarget {
    RELATIONSHIP,
    CITY_BIRTH,
    CITY_ISSUANCE,
    BRANCH
}

@Immutable
data class DocType(
    val code: String,
    val title: String,
    val isDisabled: Boolean = false
)

@Immutable
data class UploadedDocument(
    val guid: String,
    val docType: String,
    val fileName: String,
    val fileBytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UploadedDocument

        if (guid != other.guid) return false
        if (docType != other.docType) return false
        if (fileName != other.fileName) return false
        if (fileBytes != null) {
            if (other.fileBytes == null) return false
            if (!fileBytes.contentEquals(other.fileBytes)) return false
        } else if (other.fileBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guid.hashCode()
        result = 31 * result + docType.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + (fileBytes?.contentHashCode() ?: 0)
        return result
    }
}

@Immutable
data class AddDependentState(
    val currentStep: Int = STEP_INQUIRY,
    val stepperMode: StepperMode = StepperMode.DEFAULT_MODE,
    val isLoading: Boolean = false,
    val activeBranches: List<BranchPR> = emptyList(),
    val familyRelationships: List<FamilyRelationshipPR> = emptyList(),
    val dependentNationalId: String = "",
    val birthDateGregorian: String = "",
    val birthDateTimeStamp: String = "",
    val birthDatePersian: String = "",
    val selectedRelationship: FamilyRelationshipPR? = null,
    val registryData: RegistryDataPR? = null,
    val educationCode: String = "",
    val universityName: String = "",
    val isDaughterCommitmentChecked: Boolean = false,
    val selectedCityBirth: CityPR? = null,
    val selectedCityIssuance: CityPR? = null,
    val selectedBranch: BranchPR? = null,
    val requiredDocTypes: List<DocType> = emptyList(),
    val uploadedDocuments: List<UploadedDocument> = emptyList(),
    val cities: List<CityPR> = emptyList(),
    val needCallInquiryRegistry: Boolean = true,
    val needCallInquiryEducation: Boolean = true,
    val bottomSheetConfig: TaminBottomSheetConfig? = null,
    val bottomSheetTarget: BottomSheetTarget? = null,
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class ActiveBranchesLoaded(
            val branches: List<BranchPR>,
            val autoSelectedBranch: BranchPR?
        ) : PartialState()
        data class FamilyRelationshipsLoaded(val relationships: List<FamilyRelationshipPR>) : PartialState()
        data class CitiesLoaded(val cities: List<CityPR>) : PartialState()
        data class NationalIdChanged(val id: String) : PartialState()
        data class BirthDateSelected(
            val persianDate: String,
            val gregorianDate: String,
            val timestamp: String
        ) : PartialState()
        data class RelationshipSelected(val relationship: FamilyRelationshipPR) : PartialState()
        data class RegistryInquirySuccess(
            val registryData: RegistryDataPR,
            val stepperMode: StepperMode,
            val requiredDocTypes: List<DocType>
        ) : PartialState()
        data class EducationCodeChanged(val code: String) : PartialState()
        data class EducationInquirySuccess(val universityName: String) : PartialState()
        data class DaughterCommitmentToggled(val isChecked: Boolean) : PartialState()
        data class CityBirthSelected(val city: CityPR) : PartialState()
        data class CityIssuanceSelected(val city: CityPR) : PartialState()
        data class BranchSelected(val branch: BranchPR) : PartialState()
        data class DocumentUploaded(val document: UploadedDocument) : PartialState()
        data class DocumentDeleted(val docType: String) : PartialState()
        data class StepChanged(val step: Int) : PartialState()
        data class BottomSheetStateChanged(
            val config: TaminBottomSheetConfig?,
            val target: BottomSheetTarget?
        ) : PartialState()
        data class Error(val message: String) : PartialState()
    }
}

sealed interface AddDependentIntent {
    data object InitData : AddDependentIntent
    data class OnNationalIdChanged(val id: String) : AddDependentIntent
    data class OnBirthDateSelected(
        val persianDate: String,
        val gregorianDate: String,
        val timestamp: String
    ) : AddDependentIntent
    data class OnRelationshipSelected(val relationship: FamilyRelationshipPR) : AddDependentIntent
    data object SubmitInquiryRegistry : AddDependentIntent
    data class OnEducationCodeChanged(val code: String) : AddDependentIntent
    data object SubmitInquiryEducation : AddDependentIntent
    data class OnDaughterCommitmentToggled(val isChecked: Boolean) : AddDependentIntent
    data object ShowRelationshipPicker : AddDependentIntent
    data object ShowCityBirthPicker : AddDependentIntent
    data object ShowCityIssuancePicker : AddDependentIntent
    data object ShowBranchPicker : AddDependentIntent
    data object DismissBottomSheet : AddDependentIntent
    data class OnCityBirthSelected(val city: CityPR) : AddDependentIntent
    data class OnCityIssuanceSelected(val city: CityPR) : AddDependentIntent
    data class OnBranchSelected(val branch: BranchPR) : AddDependentIntent
    data class UploadDocument(
        val fileBytes: ByteArray,
        val fileName: String,
        val docType: String
    ) : AddDependentIntent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is UploadDocument) return false
            return fileBytes.contentEquals(other.fileBytes) &&
                    fileName == other.fileName &&
                    docType == other.docType
        }

        override fun hashCode(): Int {
            var result = fileBytes.contentHashCode()
            result = 31 * result + fileName.hashCode()
            result = 31 * result + docType.hashCode()
            return result
        }
    }
    data class DeleteDocument(val docType: String) : AddDependentIntent
    data class OnFileReadError(val message: String) : AddDependentIntent
    data object OnNextStepClicked : AddDependentIntent
    data object OnPreviousStepClicked : AddDependentIntent
    data object SubmitFinalRequest : AddDependentIntent
}

sealed interface AddDependentEvent {
    data class ShowToast(val message: String) : AddDependentEvent
    data class ShowErrorDialog(val title: String, val message: String) : AddDependentEvent
    data object NavigateBack : AddDependentEvent
}
