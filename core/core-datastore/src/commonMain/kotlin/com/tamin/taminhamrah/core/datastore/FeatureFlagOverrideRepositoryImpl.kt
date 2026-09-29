package com.tamin.taminhamrah.core.datastore

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.repository.feature.FeatureFlagOverrideRepository
import com.tamin.taminhamrah.util.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREF_KEY_PREFIX = "dev_opt_feature_flag_"
private const val KIND_ENABLED = "ENABLED"
private const val KIND_DISABLED = "DISABLED"
private const val KIND_TEMPORARY_DISABLED = "TEMPORARY_DISABLED"
private const val KIND_ENABLED_WITH_ERROR = "ENABLED_WITH_ERROR"
private const val KIND_WEB_VIEW = "WEB_VIEW"

/**
 * Persists feature-flag overrides the same way [DeveloperOptionsRepositoryImpl] persists base-URL
 * overrides: one [Settings] entry per flag ([FeatureFlag.entries] is the whole address space, so
 * there is nothing to enumerate), read once into an in-memory cache and mirrored to storage on write.
 */
class FeatureFlagOverrideRepositoryImpl(
    private val settings: Settings,
    private val isDebug: Boolean = AppConfig.isDebug,
) : FeatureFlagOverrideRepository {

    private val _overrides = MutableStateFlow(if (isDebug) loadOverrides() else emptyMap())

    private fun loadOverrides(): Map<FeatureFlag, FeatureStatus> =
        FeatureFlag.entries.mapNotNull { flag -> readOverride(flag)?.let { flag to it } }.toMap()

    private fun readOverride(flag: FeatureFlag): FeatureStatus? {
        val kind = settings.getStringOrNull(kindKey(flag)) ?: return null
        val text = settings.getStringOrNull(textKey(flag))
        return when (kind) {
            KIND_ENABLED -> FeatureStatus.Enabled
            KIND_DISABLED -> FeatureStatus.Disabled(text)
            KIND_TEMPORARY_DISABLED -> FeatureStatus.TemporaryDisabled(text)
            KIND_ENABLED_WITH_ERROR -> FeatureStatus.EnabledWithError(text)
            // A web view with no URL saved makes no sense to replay; drop it rather than crash later.
            KIND_WEB_VIEW -> text?.let { FeatureStatus.WebView(it) }
            else -> null
        }
    }

    private fun kindKey(flag: FeatureFlag) = "$PREF_KEY_PREFIX${flag.id}_kind"
    private fun textKey(flag: FeatureFlag) = "$PREF_KEY_PREFIX${flag.id}_text"

    override fun observeOverrides(): Flow<Map<FeatureFlag, FeatureStatus>> = _overrides.asStateFlow()

    /**
     * A release build must never let a value left in shared app storage by a previous debug
     * install force a real flag's state — the same guard [DeveloperOptionsRepositoryImpl] applies
     * to base-URL overrides.
     */
    override fun setOverride(flag: FeatureFlag, status: FeatureStatus) {
        if (!isDebug) return
        val (kind, text) = when (status) {
            FeatureStatus.Enabled -> KIND_ENABLED to null
            is FeatureStatus.Disabled -> KIND_DISABLED to status.message
            is FeatureStatus.TemporaryDisabled -> KIND_TEMPORARY_DISABLED to status.message
            is FeatureStatus.EnabledWithError -> KIND_ENABLED_WITH_ERROR to status.message
            is FeatureStatus.WebView -> KIND_WEB_VIEW to status.url
        }
        settings.putString(kindKey(flag), kind)
        if (text != null) settings.putString(textKey(flag), text) else settings.remove(textKey(flag))
        _overrides.value += (flag to status)
    }

    override fun clearOverride(flag: FeatureFlag) {
        settings.remove(kindKey(flag))
        settings.remove(textKey(flag))
        _overrides.value -= flag
    }

    override fun clearAllOverrides() {
        FeatureFlag.entries.forEach { clearOverride(it) }
    }
}
