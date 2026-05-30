package com.tamin.taminhamrah.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getUserProfileImageUseCase: UserProfileImageUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadUserImage()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingIdentity = true, identityError = null) }
            identityInfoUseCase()
                .catch { e ->
                    if (e is CancellationException) throw e
                    e.printStackTrace()
                    println("REAL_ERROR_IS: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoadingIdentity = false,
                            identityInfo = null,
                            identityError = e.toSingleLineMessage().ifBlank {
                                "خطا در دریافت اطلاعات هویتی"
                            },
                        )
                    }
                }
                .collect { identity ->
                    _uiState.update {
                        it.copy(
                            isLoadingIdentity = false,
                            identityInfo = identity,
                            identityError = null,
                        )
                    }
                }
        }
    }

    private fun loadUserImage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingImage = true, imageError = null) }
            try {
                val imageBase64 = getUserProfileImageUseCase()
                _uiState.update {
                    it.copy(
                        profileImageBase64 = imageBase64,
                        isLoadingImage = false,
                        imageError = null,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingImage = false,
                        imageError = e.toSingleLineMessage().ifBlank {
                            "خطا در دریافت تصویر پروفایل"
                        },
                    )
                }
            }
        }
    }
}
