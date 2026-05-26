/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.core.datastore.UserPreferencesRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.ui.contract.MainUiState
import com.tamin.taminhamrah.ui.contract.MainUiState.PartialState
import com.tamin.taminhamrah.ui.contract.MainIntent
import com.tamin.taminhamrah.ui.contract.MainEvent
import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val tokenStoreManager: TokenStoreManager,
    private val authAuthorizeUrlUseCase: AuthAuthorizeUrlUseCase
) : BaseViewModel<MainUiState, PartialState, MainEvent, MainIntent>(
    initialState = MainUiState(isLoading = true)
) {

    init {
        observeData()
        sendIntent(MainIntent.CheckAuthStatus)
    }

    private fun observeData() {
        viewModelScope.launch {
            userPreferencesRepository.userData.collect { userData ->
                sendIntent(MainIntent.UpdateDarkThemeConfig(userData.darkThemeConfig))
            }
        }
        viewModelScope.launch {
            tokenStoreManager.tokenValidFlow().collect {
                sendIntent(MainIntent.CheckAuthStatus)
            }
        }
    }

    override fun handleIntent(intent: MainIntent): Flow<PartialState> = flow {
        when (intent) {
            is MainIntent.UpdateDarkThemeConfig -> {
                emit(PartialState.SetDarkThemeConfig(intent.config))
            }
            MainIntent.Login -> {
                val url = authAuthorizeUrlUseCase()
                sendEvent(MainEvent.OpenUrl(url))
            }
            MainIntent.CheckAuthStatus -> {
                val hasToken = !tokenStoreManager.getToken().isNullOrEmpty()
                emit(PartialState.SetLoginStatus(hasToken))
            }
        }
    }

    override fun reduceState(currentState: MainUiState, partialState: PartialState): MainUiState {
        return when (partialState) {
            is PartialState.SetDarkThemeConfig -> currentState.copy(
                darkThemeConfig = partialState.config,
                isLoading = false
            )
            is PartialState.SetLoginStatus -> currentState.copy(
                isLoggedIn = partialState.isLoggedIn,
                isLoading = false
            )
            PartialState.Loading -> currentState.copy(isLoading = true)
        }
    }

    override fun createErrorState(message: String): PartialState {
        return PartialState.Loading
    }

    fun updateDarkThemeConfig(config: com.tamin.taminhamrah.model.DarkThemeConfig) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkThemeConfig(config)
        }
    }

    fun login() {
        sendIntent(MainIntent.Login)
    }
}
