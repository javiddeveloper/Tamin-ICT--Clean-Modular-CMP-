package com.tamin.taminhamrah.feature.orotezprotez.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class OrotezProtezUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState

    }
}

sealed interface OrotezProtezIntent {
    data object LoadRequests : OrotezProtezIntent
}

sealed interface OrotezProtezEvent
