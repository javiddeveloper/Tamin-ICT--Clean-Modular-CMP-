package com.tamin.taminhamrah.feature.userRequest.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.userRequest.RequestErrorPR
import com.tamin.taminhamrah.model.userRequest.SmartGuidePR
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR

import org.jetbrains.compose.resources.StringResource
import taminx.feature.userrequest.generated.resources.Res
import taminx.feature.userrequest.generated.resources.user_request_tab_action_required
import taminx.feature.userrequest.generated.resources.user_request_tab_all
import taminx.feature.userrequest.generated.resources.user_request_tab_completed
import taminx.feature.userrequest.generated.resources.user_request_tab_in_progress

enum class RequestStatusTab(val labelRes: StringResource) {
    ALL(Res.string.user_request_tab_all),
    IN_PROGRESS(Res.string.user_request_tab_in_progress),
    ACTION_REQUIRED(Res.string.user_request_tab_action_required),
    COMPLETED(Res.string.user_request_tab_completed),
}

object UserRequestKeywords {
    const val IN_PROGRESS = "در جریان"
    const val REVIEW = "بررسی"
    const val DEFECT = "نقص"
    const val DISAPPROVAL = "عدم"
    const val ACTION = "اقدام"
    const val ERROR = "خطا"
    const val APPROVED = "تایید"
    const val CLOSED = "مختومه"
    const val COMPLETED = "تکمیل"
    const val PREGNANCY = "بارداری"
}

@Immutable
data class UserRequestsUiState(
    val isLoading: Boolean = false,
    val isLoadingTypes: Boolean = false,
    val isLoadingErrors: Boolean = false,
    val isLoadingSmartGuide: Boolean = false,
    val requests: List<UserRequestPR> = emptyList(),
    val requestTypes: List<UserRequestTypePR> = emptyList(),
    val selectedTab: RequestStatusTab = RequestStatusTab.ALL,
    val refCode: String = "",
    val selectedRequestTypeId: String? = null,
    val selectedRequestTypeName: String? = null,
    val isFilterOpen: Boolean = false,
    val smartGuideItems: List<SmartGuidePR> = emptyList(),
    val isSmartGuideOpen: Boolean = false,
    val smartGuideTitle: String? = null,
    val errorItems: List<RequestErrorPR> = emptyList(),
    val isErrorsOpen: Boolean = false,
    val errorTitle: String? = null,
    val infoDialogMessage: String? = null,
    val error: String? = null,
) {
    val filteredRequests: List<UserRequestPR>
        get() = when (selectedTab) {
            RequestStatusTab.ALL -> requests
            RequestStatusTab.IN_PROGRESS -> requests.filter {
                it.statusDesc.contains(UserRequestKeywords.IN_PROGRESS) || it.statusDesc.contains(UserRequestKeywords.REVIEW)
            }
            RequestStatusTab.ACTION_REQUIRED -> requests.filter {
                it.statusDesc.contains(UserRequestKeywords.DEFECT) || it.statusDesc.contains(UserRequestKeywords.DISAPPROVAL) || it.statusDesc.contains(UserRequestKeywords.ACTION)
            }
            RequestStatusTab.COMPLETED -> requests.filter {
                it.statusDesc.contains(UserRequestKeywords.APPROVED) || it.statusDesc.contains(UserRequestKeywords.CLOSED) || it.statusDesc.contains(UserRequestKeywords.COMPLETED)
            }
        }

    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class LoadingTypes(val isLoading: Boolean) : PartialState()
        data class LoadingErrors(val isLoading: Boolean) : PartialState()
        data class LoadingSmartGuide(val isLoading: Boolean) : PartialState()
        data class RequestsLoaded(val requests: List<UserRequestPR>) : PartialState()
        data class RequestTypesLoaded(val types: List<UserRequestTypePR>) : PartialState()
        data class SmartGuideLoaded(val items: List<SmartGuidePR>, val title: String) : PartialState()
        data class ErrorsLoaded(val items: List<RequestErrorPR>, val title: String) : PartialState()
        data class TabChanged(val tab: RequestStatusTab) : PartialState()
        data class RefCodeChanged(val refCode: String) : PartialState()
        data class RequestTypeSelected(val typeId: String?, val typeName: String?) : PartialState()
        data class FilterToggled(val isOpen: Boolean) : PartialState()
        data class SmartGuideToggled(val isOpen: Boolean) : PartialState()
        data class ErrorsToggled(val isOpen: Boolean) : PartialState()
        data class InfoDialogToggled(val message: String?) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface UserRequestsIntent {
    data object LoadRequests : UserRequestsIntent
    data object LoadRequestTypes : UserRequestsIntent
    data class SelectTab(val tab: RequestStatusTab) : UserRequestsIntent
    data class UpdateRefCode(val refCode: String) : UserRequestsIntent
    data class SelectRequestType(val typeId: String?, val typeName: String?) : UserRequestsIntent
    data object ToggleFilter : UserRequestsIntent
    data object SearchRequests : UserRequestsIntent
    data class OpenSmartGuide(val requestType: Int?, val requestStatus: String?, val title: String) : UserRequestsIntent
    data class OpenErrors(val requestId: Long, val title: String) : UserRequestsIntent
    data class ShowInfoDialog(val message: String?) : UserRequestsIntent
    data object CloseSmartGuide : UserRequestsIntent
    data object CloseErrors : UserRequestsIntent
    data class ViewDetails(val request: UserRequestPR) : UserRequestsIntent
    data class CopyTrackingCode(val code: String) : UserRequestsIntent
}

sealed interface UserRequestsEvent {
    data class ShowToast(val message: String) : UserRequestsEvent
    data class NavigateToDetail(val requestId: Long, val refCode: String, val requestTypeId: Long ,val title: String) : UserRequestsEvent
}
