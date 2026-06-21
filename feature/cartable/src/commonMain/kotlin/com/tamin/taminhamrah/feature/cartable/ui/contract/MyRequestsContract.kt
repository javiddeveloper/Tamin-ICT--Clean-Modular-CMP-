package com.tamin.taminhamrah.feature.cartable.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR

@Immutable
data class UserRequestsUiState(
    val isLoading: Boolean = false,
    val isLoadingTypes: Boolean = false,
    val error: String? = null,
    val refCode: String = "",
    val selectedRequestTypeId: String? = null,
    val requestTypes: List<UserRequestTypePR> = emptyList(),
    val requests: List<UserRequestPR> = emptyList(),
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class LoadingTypes(val isLoadingTypes: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class RefCodeChanged(val refCode: String) : PartialState()
        data class RequestTypeChanged(val requestTypeId: String?) : PartialState()
        data class RequestTypesLoaded(val requestTypes: List<UserRequestTypePR>) : PartialState()
        data class RequestsLoaded(val requests: List<UserRequestPR>) : PartialState()
    }
}

sealed class UserRequestsIntent {
    data object LoadRequests : UserRequestsIntent()
    data object LoadRequestTypes : UserRequestsIntent()
    data class UpdateRefCode(val refCode: String) : UserRequestsIntent()
    data class UpdateRequestType(val requestTypeId: String?) : UserRequestsIntent()
    data object SearchRequests : UserRequestsIntent()
}

sealed class UserRequestsEvent {
    data class ShowToast(val message: String) : UserRequestsEvent()
}
