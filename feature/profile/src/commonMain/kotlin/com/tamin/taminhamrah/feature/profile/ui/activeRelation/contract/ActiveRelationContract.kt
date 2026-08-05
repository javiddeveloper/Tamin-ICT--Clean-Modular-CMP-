package com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationPR
import com.tamin.taminhamrah.model.certificate.RecipientPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ActiveRelationUiState(
    val isLoading: Boolean = false,
    val items: ImmutableList<ActiveRelationPR> = persistentListOf(),
    val error: String? = null,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0,
    val lastCheckTime: String = "",

    // Bottom sheet states
    val selectedItem: ActiveRelationPR? = null,
    val showCertificateSheet: Boolean = false,
    val showRecipientsSheet: Boolean = false,
    val recipients: ImmutableList<RecipientPR> = persistentListOf(),
    val filteredRecipients: ImmutableList<RecipientPR> = persistentListOf(),
    val isLoadingRecipients: Boolean = false,
    val selectedRecipient: RecipientPR? = null,
    val branchName: String = "",
    val searchQuery: String = "",
    val showSuccessDialog: Boolean = false
) {
    sealed interface PartialState {
        data class SetLoading(val isLoading: Boolean) : PartialState
        data class SetData(
            val items: ImmutableList<ActiveRelationPR>,
            val activeCount: Int,
            val inactiveCount: Int,
            val lastCheckTime: String
        ) : PartialState
        data class SetError(val error: String?) : PartialState

        // Bottom sheet partial states
        data class SetShowCertificateSheet(val show: Boolean, val item: ActiveRelationPR? = null) : PartialState
        data class SetShowRecipientsSheet(val show: Boolean) : PartialState
        data class SetRecipients(val list: ImmutableList<RecipientPR>) : PartialState
        data class SetLoadingRecipients(val isLoading: Boolean) : PartialState
        data class SetSelectedRecipient(val recipient: RecipientPR?) : PartialState
        data class SetBranchName(val name: String) : PartialState
        data class SetSearchQuery(val query: String) : PartialState
        data class SetShowSuccessDialog(val show: Boolean) : PartialState
    }
}

sealed interface ActiveRelationIntent {
    data object LoadActiveRelations : ActiveRelationIntent
    data object OnBackClicked : ActiveRelationIntent
    data class OnSendCertificateClicked(val item: ActiveRelationPR) : ActiveRelationIntent

    // New intents
    data object OnSelectRecipientClicked : ActiveRelationIntent
    data class OnRecipientSelected(val recipient: RecipientPR?) : ActiveRelationIntent
    data class OnBranchNameChanged(val name: String) : ActiveRelationIntent
    data class OnIssueCertificateClicked(val item: ActiveRelationPR) : ActiveRelationIntent
    data object OnDismissCertificateSheet : ActiveRelationIntent
    data object OnDismissRecipientsSheet : ActiveRelationIntent
    data object OnDismissSuccessDialog : ActiveRelationIntent
    data class OnSearchRecipients(val query: String) : ActiveRelationIntent
}

sealed interface ActiveRelationEvent {
    data object NavigateBack : ActiveRelationEvent
    data class ShowToast(val message: String) : ActiveRelationEvent
}
