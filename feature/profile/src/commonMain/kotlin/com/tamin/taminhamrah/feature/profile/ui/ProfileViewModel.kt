package com.tamin.taminhamrah.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserProfileImageUseCase: UserProfileImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        getUserProfileImage()
    }

    private fun getUserProfileImage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val imageBase64 = getUserProfileImageUseCase()
                _uiState.update { it.copy(profileImageBase64 = imageBase64, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

data class ProfileUiState(
    val profileImageBase64: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
