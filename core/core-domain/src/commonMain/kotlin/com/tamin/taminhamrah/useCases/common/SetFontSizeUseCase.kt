package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.repository.UserPreferencesRepository

class SetFontSizeUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(fontSize: FontSizeOption) {
        userPreferencesRepository.setFontSize(fontSize)
    }
}
