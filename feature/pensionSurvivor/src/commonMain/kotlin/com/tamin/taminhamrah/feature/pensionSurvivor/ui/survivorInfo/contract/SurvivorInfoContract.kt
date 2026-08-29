package com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SharedDeceasedDocument
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SurvivorDependencyType
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SurvivorDocumentType
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SurvivorRelationKind
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class SurvivorUploadedDocument(
    val type: SurvivorDocumentType,
    val fileName: String,
    val bytes: ByteArray,
    val guid: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SurvivorUploadedDocument) return false
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

@Immutable
data class SurvivorInfoUiState(
    val isLoading: Boolean = false,
    val survivor: SurvivorDependentPR? = null,
    val deceasedNationalId: String = "",
    val branchCode: String = "",
    val deceasedInsuranceId: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val mobileNumber: String = "",
    val relationKind: SurvivorRelationKind = SurvivorRelationKind.Unknown,
    val dependencyType: SurvivorDependencyType? = null,
    val ageYears: Int = 0,
    val requiredDocuments: ImmutableList<SurvivorDocumentType> = persistentListOf(),
    val documents: ImmutableMap<SurvivorDocumentType, SurvivorUploadedDocument> = persistentMapOf(),
    val sharedDeceasedDocuments: ImmutableList<SharedDeceasedDocument> = persistentListOf(),
    val uploadingDocument: SurvivorDocumentType? = null,
    val failedDocument: SurvivorDocumentType? = null,
    val activeDocument: SurvivorDocumentType? = null,
    val documentError: String? = null,
    val fieldError: String? = null,
    val showIdentitySheet: Boolean = false,
) {
    val uploadedRequiredCount: Int
        get() = requiredDocuments.count { documents.containsKey(it) }

    val areRequiredDocumentsComplete: Boolean
        get() = requiredDocuments.isNotEmpty() && requiredDocuments.all { documents.containsKey(it) }

    val isDocumentUploading: Boolean
        get() = uploadingDocument != null

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Initialized(
            val survivor: SurvivorDependentPR,
            val deceasedNationalId: String,
            val branchCode: String,
            val deceasedInsuranceId: String,
            val address: String,
            val phoneNumber: String,
            val mobileNumber: String,
            val relationKind: SurvivorRelationKind,
            val dependencyType: SurvivorDependencyType?,
            val ageYears: Int,
            val requiredDocuments: ImmutableList<SurvivorDocumentType>,
            val sharedDeceasedDocuments: ImmutableList<SharedDeceasedDocument>,
        ) : PartialState
        data class AddressChanged(val value: String) : PartialState
        data class PhoneNumberChanged(val value: String) : PartialState
        data class MobileNumberChanged(val value: String) : PartialState
        data class FieldError(val message: String?) : PartialState
        data class DocumentSourceRequested(val type: SurvivorDocumentType) : PartialState
        data object DocumentSourceDismissed : PartialState
        data class DocumentUploadStarted(val type: SurvivorDocumentType) : PartialState
        data class DocumentUploaded(val document: SurvivorUploadedDocument) : PartialState
        data class DocumentUploadFailed(
            val type: SurvivorDocumentType,
            val message: String,
        ) : PartialState
        data class DocumentRemoved(val type: SurvivorDocumentType) : PartialState
        data class IdentitySheetVisibility(val visible: Boolean) : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface SurvivorInfoIntent {
    data class Init(
        val survivor: SurvivorDependentPR,
        val deceasedNationalId: String,
        val branchCode: String = "",
        val deceasedInsuranceId: String = "",
        val address: String = "",
        val phoneNumber: String = "",
        val mobileNumber: String = "",
        val sharedDeceasedDocuments: List<SharedDeceasedDocument> = emptyList(),
    ) : SurvivorInfoIntent

    data class AddressChanged(val value: String) : SurvivorInfoIntent
    data class PhoneNumberChanged(val value: String) : SurvivorInfoIntent
    data class MobileNumberChanged(val value: String) : SurvivorInfoIntent
    data class DocumentClicked(val type: SurvivorDocumentType) : SurvivorInfoIntent
    data class DocumentImagePicked(
        val type: SurvivorDocumentType,
        val fileName: String,
        val bytes: ByteArray,
    ) : SurvivorInfoIntent
    data object DismissDocumentSource : SurvivorInfoIntent
    data class RemoveDocument(val type: SurvivorDocumentType) : SurvivorInfoIntent
    data object ShowIdentity : SurvivorInfoIntent
    data object DismissIdentity : SurvivorInfoIntent
    data object Save : SurvivorInfoIntent
    data object OnBack : SurvivorInfoIntent
}

sealed interface SurvivorInfoEvent {
    data class ShowError(val message: String) : SurvivorInfoEvent
    data class ShowSuccess(val message: String) : SurvivorInfoEvent
    data class Saved(
        val nationalId: String,
        val draft: SurvivorContactDraft,
    ) : SurvivorInfoEvent
    data object NavigateBack : SurvivorInfoEvent
}
