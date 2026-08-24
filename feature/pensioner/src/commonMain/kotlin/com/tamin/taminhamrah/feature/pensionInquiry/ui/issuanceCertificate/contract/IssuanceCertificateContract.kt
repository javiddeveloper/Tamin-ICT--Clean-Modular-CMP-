package com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.model.pension.PensionIdPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class IssuanceCertificateStep {
    Info,
    Confirm,
}

@Immutable
data class IssuanceCertificateUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,

    val currentStep: IssuanceCertificateStep = IssuanceCertificateStep.Info,

    val fullName: String = "",

    val pensionerIds: ImmutableList<PensionIdPR> = persistentListOf(),
    val selectedPensionerId: String? = null,
    val pensionerIdError: String? = null,
    val showPensionerSheet: Boolean = false,

    val recipients: ImmutableList<RecipientPR> = persistentListOf(),
    val filteredRecipients: ImmutableList<RecipientPR> = persistentListOf(),
    val isLoadingRecipients: Boolean = false,
    val selectedRecipient: RecipientPR? = null,
    val recipientError: String? = null,
    val showRecipientsSheet: Boolean = false,
    val searchQuery: String = "",

    val branchName: String = "",
    val branchNameError: String? = null,

    val showSuccessDialog: Boolean = false,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class Error(val message: String?) : PartialState

        data class GoToStep(val step: IssuanceCertificateStep) : PartialState
        data class FullNameLoaded(val fullName: String) : PartialState

        data class PensionerIdsLoaded(val list: ImmutableList<PensionIdPR>) : PartialState
        data class SelectedPensionerIdChanged(val id: String?) : PartialState
        data class ShowPensionerSheet(val show: Boolean) : PartialState

        data class RecipientsLoaded(val list: ImmutableList<RecipientPR>) : PartialState
        data class LoadingRecipients(val isLoading: Boolean) : PartialState
        data class SelectedRecipientChanged(val recipient: RecipientPR?) : PartialState
        data class ShowRecipientsSheet(val show: Boolean) : PartialState
        data class SearchQueryChanged(val query: String) : PartialState

        data class BranchNameChanged(val name: String) : PartialState

        data class ValidationFailed(
            val pensionerIdError: String?,
            val recipientError: String?,
            val branchNameError: String?
        ) : PartialState

        data object ClearFieldErrors : PartialState
        data class ShowSuccessDialog(val show: Boolean) : PartialState
    }
}

sealed interface IssuanceCertificateIntent {
    data object Init : IssuanceCertificateIntent
    data object ShowPensionerSheet : IssuanceCertificateIntent
    data object DismissPensionerSheet : IssuanceCertificateIntent
    data class SelectPensionerId(val id: String) : IssuanceCertificateIntent
    data object ShowRecipientsSheet : IssuanceCertificateIntent
    data object DismissRecipientsSheet : IssuanceCertificateIntent
    data class SelectRecipient(val recipient: RecipientPR) : IssuanceCertificateIntent
    data class SearchRecipients(val query: String) : IssuanceCertificateIntent
    data class ChangeBranchName(val name: String) : IssuanceCertificateIntent
    data object GoToNextStep : IssuanceCertificateIntent
    data object GoToPreviousStep : IssuanceCertificateIntent
    data object SubmitRequest : IssuanceCertificateIntent
    data object DismissSuccessDialog : IssuanceCertificateIntent
}

sealed interface IssuanceCertificateEvent {
    data class ShowToast(val message: String) : IssuanceCertificateEvent
    data object NavigateHome : IssuanceCertificateEvent
}
