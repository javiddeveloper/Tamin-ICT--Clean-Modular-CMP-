/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)

package com.tamin.taminhamrah.core.datastore

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.model.UserData
import com.tamin.taminhamrah.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi

private const val USER_DATA_KEY = "user_data_key"

class UserPreferencesRepositoryImpl(private val settings: Settings, ) : UserPreferencesRepository {

    private val _userData = MutableStateFlow(UserData.DEFAULT)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val data = settings.decodeValue(
                key = USER_DATA_KEY,
                serializer = UserData.serializer(),
                defaultValue = settings.decodeValueOrNull(
                    key = USER_DATA_KEY,
                    serializer = UserData.serializer(),
                ) ?: UserData.DEFAULT,
            )
            _userData.value = data
        }
    }

    override val userData: StateFlow<UserData>
        get() = _userData.asStateFlow()

    override val observeDarkThemeConfig: Flow<DarkThemeConfig>
        get() = _userData.map { it.darkThemeConfig }

    override val observeFontSize: Flow<FontSizeOption>
        get() = _userData.map { it.fontSize }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) =
        withContext(Dispatchers.IO) {
            val currentPreference = settings.getUserPreference()
            val newPreference = currentPreference.copy(darkThemeConfig = darkThemeConfig)
            settings.putUserPreference(newPreference)
            _userData.value = newPreference
        }

    override suspend fun setFontSize(fontSize: FontSizeOption) =
        withContext(Dispatchers.IO) {
            val currentPreference = settings.getUserPreference()
            val newPreference = currentPreference.copy(fontSize = fontSize)
            settings.putUserPreference(newPreference)
            _userData.value = newPreference
        }
}

private fun Settings.getUserPreference(): UserData {
    return decodeValue(
        key = USER_DATA_KEY,
        serializer = UserData.serializer(),
        defaultValue = UserData.DEFAULT,
    )
}

private fun Settings.putUserPreference(preference: UserData) {
    encodeValue(
        key = USER_DATA_KEY,
        serializer = UserData.serializer(),
        value = preference,
    )
}
