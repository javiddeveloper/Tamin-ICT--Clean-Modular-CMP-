package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.repository.UserPreferencesRepository

class SetBiometricEnabledUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        userPreferencesRepository.setBiometricEnabled(enabled)
    }
}
