package com.tamin.taminhamrah.core.datastore

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREF_KEY_PREFIX = "dev_opt_base_url_"

class DeveloperOptionsRepositoryImpl(private val settings: Settings) : DeveloperOptionsRepository {

    private val _overrides = MutableStateFlow(loadOverrides())

    private fun loadOverrides(): Map<BaseUrlKey, String> =
        BaseUrlKey.entries.mapNotNull { key ->
            settings.getStringOrNull(prefKey(key))?.let { key to it }
        }.toMap()

    private fun prefKey(key: BaseUrlKey) = "$PREF_KEY_PREFIX${key.name}"

    override fun getEffectiveBaseUrl(key: BaseUrlKey): String =
        _overrides.value[key] ?: key.defaultValue

    override fun observeOverrides(): Flow<Map<BaseUrlKey, String>> = _overrides.asStateFlow()

    override fun setOverride(key: BaseUrlKey, url: String) {
        settings.putString(prefKey(key), url)
        _overrides.value = _overrides.value + (key to url)
    }

    override fun clearOverride(key: BaseUrlKey) {
        settings.remove(prefKey(key))
        _overrides.value = _overrides.value - key
    }
}
