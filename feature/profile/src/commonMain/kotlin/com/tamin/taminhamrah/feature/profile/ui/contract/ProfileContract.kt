package com.tamin.taminhamrah.feature.profile.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.core.model.common.IdentityInfoDN

@Immutable
data class ProfileUiState(
    val screenState: AsyncState<Unit> = AsyncState.Uninitialized,
    val userId: String? = null,
    val profileImageState: AsyncState<String> = AsyncState.Uninitialized,
    val identityInfoState: AsyncState<IdentityInfoDN> = AsyncState.Uninitialized,
) {
    sealed class PartialState {
        data class ScreenStateChanged(val state: AsyncState<Unit>) : PartialState()
        data class SetUserId(val userId: String?) : PartialState()
        data class ProfileImageChanged(val state: AsyncState<String>) : PartialState()
        data class IdentityInfoChanged(val state: AsyncState<IdentityInfoDN>) : PartialState()
    }
}

sealed class ProfileIntent {
    data class LoadProfile(val userId: String? = null) : ProfileIntent()
    data object Logout : ProfileIntent()
    data class OnItemClick(val title: String) : ProfileIntent()
}

sealed class ProfileEvent {
    data object NavigateBack : ProfileEvent()
    data object NavigateToSettings : ProfileEvent()
    data class ShowToast(val message: String) : ProfileEvent()
}

sealed class AsyncState<out T> {
    data object Uninitialized : AsyncState<Nothing>()
    data object Loading : AsyncState<Nothing>()
    data class Success<T>(val data: T) : AsyncState<T>()
    data class Error(val message: String) : AsyncState<Nothing>()
}
