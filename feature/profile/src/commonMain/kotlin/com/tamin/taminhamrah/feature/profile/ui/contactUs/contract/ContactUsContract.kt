package com.tamin.taminhamrah.feature.profile.ui.contactUs.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.contactUs.ContactDetailPR
import com.tamin.taminhamrah.model.contactUs.ContactUsPR
import com.tamin.taminhamrah.model.contactUs.SocialChannelPR

@Immutable
data class ContactUsUiState(
    val isLoading: Boolean = false,
    val contactInfo: ContactUsPR? = null,
    val error: String? = null
) {
    sealed interface PartialState {
        data class SetLoading(val isLoading: Boolean) : PartialState
        data class SetContactInfo(val info: ContactUsPR) : PartialState
        data class SetError(val message: String?) : PartialState
    }
}

sealed interface ContactUsIntent {
    data object LoadContactUs : ContactUsIntent
    data object OnCallHotline : ContactUsIntent
    data class OnSocialChannelClick(val channel: SocialChannelPR) : ContactUsIntent
    data class OnContactDetailClick(val detail: ContactDetailPR) : ContactUsIntent
    data class OnCopyText(val text: String, val label: String) : ContactUsIntent
    data object OnBackClicked : ContactUsIntent
}

sealed interface ContactUsEvent {
    data object NavigateBack : ContactUsEvent
    data class OpenUrl(val url: String) : ContactUsEvent
    data class CopyToClipboard(val text: String, val label: String) : ContactUsEvent
    data class ShowToast(val message: String) : ContactUsEvent
}
