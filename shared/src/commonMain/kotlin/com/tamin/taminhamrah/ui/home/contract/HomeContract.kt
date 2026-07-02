package com.tamin.taminhamrah.ui.home.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val menuItems: List<MainServiceDN> = emptyList(),
    val error: String? = null
){
    sealed interface HomePartialState {
        data class Loading(val isLoading: Boolean) : HomePartialState
        data class MenuLoaded(val menuItems: List<MainServiceDN>) : HomePartialState
        data class Error(val message: String?) : HomePartialState
    }
}



sealed interface HomeIntent {
    object LoadMenu : HomeIntent
    data class OnServiceClick(val service: MainServiceDN) : HomeIntent
}

sealed interface HomeEvent {
    data class ShowMessage(val message: String) : HomeEvent
    data class NavigateToWeb(val url: String) : HomeEvent
    data class NavigateToService(val flag: FeatureFlag) : HomeEvent
}
