package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.repository.UserPreferencesRepository
import com.tamin.taminhamrah.model.DarkThemeConfig

class SetThemeUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(config: DarkThemeConfig) {
        userPreferencesRepository.setDarkThemeConfig(config)
    }
}
