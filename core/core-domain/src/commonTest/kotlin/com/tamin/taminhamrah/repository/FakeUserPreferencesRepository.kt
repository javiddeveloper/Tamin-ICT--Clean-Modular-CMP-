package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class FakeUserPreferencesRepository : UserPreferencesRepository {

    private val _userData = MutableStateFlow(UserData.DEFAULT)

    override val userData: StateFlow<UserData>
        get() = _userData

    override val observeDarkThemeConfig: Flow<DarkThemeConfig>
        get() = _userData.map { it.darkThemeConfig }

    override val observeBiometricEnabled: Flow<Boolean>
        get() = _userData.map { it.isBiometricEnabled }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        _userData.value = _userData.value.copy(darkThemeConfig = darkThemeConfig)
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        _userData.value = _userData.value.copy(isBiometricEnabled = enabled)
    }

    override suspend fun completeBiometricEnrollmentPrompt(enabled: Boolean) {
        _userData.value = _userData.value.copy(
            isBiometricEnabled = enabled,
            hasAskedToEnableBiometric = true
        )
    }
}
