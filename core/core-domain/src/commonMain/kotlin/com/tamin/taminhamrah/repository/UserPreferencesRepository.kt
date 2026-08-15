package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserPreferencesRepository {
    val userData: StateFlow<UserData>
    val observeDarkThemeConfig: Flow<DarkThemeConfig>
    val observeFontSize: Flow<FontSizeOption>
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
    suspend fun setFontSize(fontSize: FontSizeOption)
}
