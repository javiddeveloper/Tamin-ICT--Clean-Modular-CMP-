package com.tamin.taminhamrah.feature.cartable.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.userRequest.UserRequestPR

@Immutable
data class MyRequestsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val requests: List<UserRequestPR> = emptyList(),
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class RequestsLoaded(val requests: List<UserRequestPR>) : PartialState()
    }
}

sealed class MyRequestsIntent {
    data object LoadRequests : MyRequestsIntent()
}

sealed class MyRequestsEvent {
    data class ShowToast(val message: String) : MyRequestsEvent()
}
