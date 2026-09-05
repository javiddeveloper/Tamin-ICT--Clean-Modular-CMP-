package com.tamin.taminhamrah.core.datastore

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREF_KEY_PREFIX = "dev_opt_base_url_"

class DeveloperOptionsRepositoryImpl(
    private val settings: Settings,
    private val isDebug: Boolean = AppConfig.isDebug,
) : DeveloperOptionsRepository {

    private val _overrides = MutableStateFlow(loadOverrides())

    private fun loadOverrides(): Map<BaseUrlKey, String> =
        BaseUrlKey.entries.mapNotNull { key ->
            settings.getStringOrNull(prefKey(key))?.let { key to it }
        }.toMap()

    private fun prefKey(key: BaseUrlKey) = "$PREF_KEY_PREFIX${key.name}"

    /**
     * Overrides only ever apply in debug builds. A release build must never send OAuth
     * tokens or API traffic to a stored dev_opt_base_url_* value, whether it was set by a
     * previous debug install sharing the same app storage or written some other way.
     */
    override fun getEffectiveBaseUrl(key: BaseUrlKey): String =
        if (isDebug) _overrides.value[key] ?: key.defaultValue else key.defaultValue

    override fun observeOverrides(): Flow<Map<BaseUrlKey, String>> = _overrides.asStateFlow()

    override fun setOverride(key: BaseUrlKey, url: String) {
        val normalized = url.trim().let { if (it.endsWith("/")) it else "$it/" }
        settings.putString(prefKey(key), normalized)
        _overrides.value = _overrides.value + (key to normalized)
    }

    override fun clearOverride(key: BaseUrlKey) {
        settings.remove(prefKey(key))
        _overrides.value = _overrides.value - key
    }
}
