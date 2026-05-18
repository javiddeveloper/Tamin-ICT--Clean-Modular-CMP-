/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.core.datastore

import kotlinx.coroutines.flow.Flow
import com.tamin.taminhamrah.domain.model.DarkThemeConfig
import com.tamin.taminhamrah.domain.model.UserData
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing user preferences with reactive
 * capabilities.
 *
 * This interface provides reactive access to user preferences including
 * theme settings, dark mode configuration, and dynamic color preferences.
 */
interface UserPreferencesRepository {
    val userData: StateFlow<UserData>
    val observeDarkThemeConfig: Flow<DarkThemeConfig>
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
}

