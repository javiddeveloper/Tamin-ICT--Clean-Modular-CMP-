package com.tamin.taminhamrah.feature.profile.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.identity.IdentityInfoPRT
import com.tamin.taminhamrah.model.relation.TaminRelationPR

@Immutable
data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId: String? = null,
    val profileImage: String? = null,
    val identityInfo: IdentityInfoPRT? = null,
    val taminRelation: TaminRelationPR? = null,
    val imageRequestResult: String? = null,
    val isImageRequestLoading: Boolean = false,
    val imageRequestError: String? = null,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class SetUserId(val userId: String?) : PartialState()
        data class ProfileImageLoaded(val image: String?) : PartialState()
        data class IdentityInfoLoaded(val info: IdentityInfoPRT?) : PartialState()
        data class TaminRelationLoaded(val relation: TaminRelationPR?) : PartialState()
        data class ImageRequestLoading(val isLoading: Boolean) : PartialState()
        data class ImageRequestResult(val result: String) : PartialState()
        data class ImageRequestError(val message: String) : PartialState()
    }
}

sealed class ProfileIntent {
    data class LoadProfile(val userId: String? = null) : ProfileIntent()
    data object Logout : ProfileIntent()
    data class OnItemClick(val title: String) : ProfileIntent()
    data class SendImageRequest(val branchCode: String, val filter: String) : ProfileIntent()
}

sealed class ProfileEvent {
    data object NavigateBack : ProfileEvent()
    data object NavigateToSettings : ProfileEvent()
    data object NavigateToIdentity : ProfileEvent()
    data class ShowToast(val message: String) : ProfileEvent()
}
