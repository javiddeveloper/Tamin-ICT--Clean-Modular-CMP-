package com.tamin.taminhamrah.feature.profile.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.profile.ui.model.ProfileMenuItem
import com.tamin.taminhamrah.model.identity.IdentityInfoPR
import com.tamin.taminhamrah.model.relation.TaminRelationPR

@Immutable
data class ProfileUiState(
    val isLoading: Boolean = false,
    val isProfileImageLoading: Boolean = true,
    val error: String? = null,
    val userId: String? = null,
    val profileImage: String? = null,
    val identityInfo: IdentityInfoPR? = null,
    val taminRelation: TaminRelationPR? = null,
    val dependentsCount: Int = 0,
    val activeRelationCount: Int = 0,
    val inactiveRelationCount: Int = 0,
    val isActiveRelationLoading: Boolean = true,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class SetUserId(val userId: String?) : PartialState()
        data class ProfileImageLoaded(val image: String?) : PartialState()
        data class IdentityInfoLoaded(val info: IdentityInfoPR?) : PartialState()
        data class TaminRelationLoaded(val relation: TaminRelationPR?) : PartialState()
        data class DependentsCountLoaded(val count: Int) : PartialState()
        data class ActiveRelationStatusLoaded(val activeCount: Int, val inactiveCount: Int) : PartialState()
        sealed class ScreenStateChanged : PartialState() {
            data object Loading : ScreenStateChanged()
            data object Success : ScreenStateChanged()
            data class Error(val message: String?) : ScreenStateChanged()
        }
    }
}

sealed class ProfileIntent {
    data class LoadProfile(val userId: String? = null) : ProfileIntent()
    data object Logout : ProfileIntent()
    data class OnItemClick(val item: ProfileMenuItem) : ProfileIntent()
    data object EditPhotoClicked : ProfileIntent()

    data object NavigateToDependentsList : ProfileIntent()
    data class ToggleTheme(val isDark: Boolean) : ProfileIntent()
}

sealed interface ProfileEvent {
    data object NavigateToActiveRelation : ProfileEvent
    data object NavigateBack : ProfileEvent
    data object NavigateToSettings : ProfileEvent
    data object NavigateToIdentity : ProfileEvent
    data object NavigateToVersionHistory : ProfileEvent
    data object NavigateToMyInbox : ProfileEvent
    data object NavigateToChangeMobile : ProfileEvent
    data object NavigateToContactUs : ProfileEvent
    data object NavigateToDependentsList : ProfileEvent
    data object NavigateToElectronicFile : ProfileEvent
    data object NavigateToUserContracts : ProfileEvent
    data object NavigateToSaveEvents : ProfileEvent
    data object NavigateToEditProfilePhoto : ProfileEvent
    data class OpenUrl(val url: String) : ProfileEvent
    data class ShowToast(val message: String) : ProfileEvent
    data object NavigateToBankAccount : ProfileEvent
    data object NavigateToSecurity : ProfileEvent
    data object NavigateToDeveloperOptions : ProfileEvent
    data class ShareAppLink(val appLink: String) : ProfileEvent
    data class Support(val phone: String) : ProfileEvent
}

