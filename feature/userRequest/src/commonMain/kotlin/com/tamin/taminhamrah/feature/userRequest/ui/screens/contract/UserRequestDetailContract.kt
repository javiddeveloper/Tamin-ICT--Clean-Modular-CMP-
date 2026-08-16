package com.tamin.taminhamrah.feature.userRequest.ui.screens.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.userRequest.UserRequestPR

@Immutable
data class UserRequestDetailState(
    val isLoading: Boolean = false,
    val request: UserRequestPR? = null,
    val error: String? = null,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val request: UserRequestPR) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface UserRequestDetailIntent {
    data class LoadDetail(
        val requestId: Long,
        val refCode: String,
        val requestTypeId: Long,
        val title: String,
        val referenceId: String = "",
    ) : UserRequestDetailIntent

    data object NavigateBack : UserRequestDetailIntent
}

sealed interface UserRequestDetailEvent {
    data object NavigateBack : UserRequestDetailEvent
    data class ShowToast(val message: String) : UserRequestDetailEvent
}
