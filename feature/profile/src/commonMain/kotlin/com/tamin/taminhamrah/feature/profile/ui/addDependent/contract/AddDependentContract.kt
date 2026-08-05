package com.tamin.taminhamrah.feature.profile.ui.addDependent.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.addDependent.BranchPR
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.model.common.CityPR

enum class StepperMode {
    DEFAULT_MODE,
    SON_MODE,
    DAUGHTER_MODE
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
    val uri: String? = null
)

@Immutable
data class AddDependentState(
    val currentStep: Int = 1,
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
    val needCallInquiryRegistry: Boolean = true,
    val needCallInquiryEducation: Boolean = true,
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class ActiveBranchesLoaded(
            val branches: List<BranchPR>,
            val autoSelectedBranch: BranchPR?
        ) : PartialState()
        data class FamilyRelationshipsLoaded(val relationships: List<FamilyRelationshipPR>) : PartialState()
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
    data object OnNextStepClicked : AddDependentIntent
    data object OnPreviousStepClicked : AddDependentIntent
    data object SubmitFinalRequest : AddDependentIntent
}

sealed interface AddDependentEvent {
    data class ShowToast(val message: String) : AddDependentEvent
    data class ShowErrorDialog(val title: String, val message: String) : AddDependentEvent
    data object NavigateBack : AddDependentEvent
}
