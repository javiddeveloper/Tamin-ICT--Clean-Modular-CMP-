package com.tamin.taminhamrah.feature.cartable.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR

@Immutable
data class MyRequestsUiState(
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

sealed class MyRequestsIntent {
    data object LoadRequests : MyRequestsIntent()
    data object LoadRequestTypes : MyRequestsIntent()
    data class UpdateRefCode(val refCode: String) : MyRequestsIntent()
    data class UpdateRequestType(val requestTypeId: String?) : MyRequestsIntent()
    data object SearchRequests : MyRequestsIntent()
}

sealed class MyRequestsEvent {
    data class ShowToast(val message: String) : MyRequestsEvent()
}
