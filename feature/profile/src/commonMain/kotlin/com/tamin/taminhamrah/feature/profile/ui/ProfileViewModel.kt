package com.tamin.taminhamrah.feature.profile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.repository.TokenStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileViewModel(
    private val tokenStoreManager: TokenStoreManager
) : BaseViewModel<ProfileUiState, PartialState, ProfileEvent, ProfileIntent>(
    initialState = ProfileUiState()
) {

    override fun handleIntent(intent: ProfileIntent): Flow<PartialState> = flow {
        when (intent) {
            is ProfileIntent.LoadProfile -> {
                emit(PartialState.Loading)
                val userId = intent.userId ?: tokenStoreManager.getUserId()
                emit(PartialState.SetUserId(userId))
            }
            ProfileIntent.Logout -> {
                tokenStoreManager.saveToken(null)
                tokenStoreManager.saveRefreshToken(null)
                tokenStoreManager.setTokenValid(isValid = false)
                sendEvent(ProfileEvent.NavigateBack)
            }
            is ProfileIntent.OnItemClick -> {
                when (intent.title) {
                    "تنظیمات" -> sendEvent(ProfileEvent.NavigateToSettings)
                    "خروج از حساب کاربری" -> sendIntent(ProfileIntent.Logout)
                    else -> sendEvent(ProfileEvent.ShowToast("کلیک بر روی: ${intent.title}"))
                }
            }
        }
    }

    override fun reduceState(
        currentState: ProfileUiState,
        partialState: PartialState
    ): ProfileUiState {
        return when (partialState) {
            is PartialState.Loading -> currentState.copy(isLoading = true, errorMessage = null)
            is PartialState.SetUserId -> currentState.copy(isLoading = false, userId = partialState.userId)
            is PartialState.Error -> currentState.copy(isLoading = false, errorMessage = partialState.message)
        }
    }

    override fun createErrorState(message: String): PartialState {
        return PartialState.Error(message)
    }
}
