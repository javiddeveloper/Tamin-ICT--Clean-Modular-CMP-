package com.tamin.taminhamrah.feature.profile.ui.identity.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.identity.IdentityInfoPR

@Immutable
data class IdentityInUiState(
    val isLoading: Boolean = false,
    val identityInfo: IdentityInfoPR? = null,
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class IdentityLoaded(val info: IdentityInfoPR) : PartialState()
        data class Error(val message: String) : PartialState()
    }
}

sealed interface IdentityInIntent {
    data object LoadIdentity : IdentityInIntent
    data object OnBackClicked : IdentityInIntent
}

sealed interface IdentityInEvent {
    data object NavigateBack : IdentityInEvent
}
