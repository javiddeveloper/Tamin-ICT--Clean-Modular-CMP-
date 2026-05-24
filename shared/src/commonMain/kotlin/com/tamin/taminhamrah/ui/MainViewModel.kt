/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.core.datastore.UserPreferencesRepository
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.openUrl
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val tokenStoreManager: TokenStoreManager,
    private val authAuthorizeUrlUseCase: AuthAuthorizeUrlUseCase
) : ViewModel() {

    val uiState: StateFlow<AppUiState> = combine(
        userPreferencesRepository.userData,
        tokenStoreManager.tokenValidFlow()
    ) { userData, isTokenValid ->
        AppUiState(
            darkThemeConfig = userData.darkThemeConfig,
            isLoggedIn = isTokenValid,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppUiState(isLoading = false)
    )

    fun login() {
        val url = authAuthorizeUrlUseCase()
        openUrl(url)
    }

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }
}

data class AppUiState(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false
)
