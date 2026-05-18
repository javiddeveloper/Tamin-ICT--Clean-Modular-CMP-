/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.core.datastore.UserPreferencesRepository
import com.tamin.taminhamrah.domain.model.DarkThemeConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    val uiState: StateFlow<AppUiState> = userPreferencesRepository.userData
        .map { userData ->
            AppUiState(darkThemeConfig = userData.darkThemeConfig)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppUiState()
        )

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }
}

data class AppUiState(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
)
