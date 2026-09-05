package com.tamin.taminhamrah.feature.developerOptions.ui.model

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.model.BaseUrlKey
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.developer_options_service_account
import taminx.core.core_ui.developer_options_service_ai
import taminx.core.core_ui.developer_options_service_health_profile
import taminx.core.core_ui.developer_options_service_main

data class BaseUrlPreset(val label: String, val url: String)

@Composable
fun BaseUrlKey.displayName(): String = when (this) {
    BaseUrlKey.MAIN -> stringResource(Res.string.developer_options_service_main)
    BaseUrlKey.ACCOUNT -> stringResource(Res.string.developer_options_service_account)
    BaseUrlKey.HEALTH_PROFILE -> stringResource(Res.string.developer_options_service_health_profile)
    BaseUrlKey.AI -> stringResource(Res.string.developer_options_service_ai)
}

/**
 * One-tap shortcuts back to each service's shipped default.
 *
 * ⚠️ **Only ever list a URL that is already a `NetworkConstants` compile-time default.**
 *
 * `feature:developerOptions` is an unconditional `api(project(...))` dependency of `:shared`, and
 * the release build type sets `isMinifyEnabled = false` (`androidApp/build.gradle.kts`), so nothing
 * here is stripped: every string literal in this file is readable in the published APK/IPA with
 * `strings` on the extracted dex. Internal test hostnames and private-range IPs listed as presets
 * would therefore be handed to anyone who downloads the app — which is why the test/staging
 * addresses that used to live here were removed. Type them into the "آدرس دلخواه" field instead;
 * `DeveloperOptionsRepository.setOverride` normalises and persists whatever is entered, so a test
 * host survives an app restart exactly like a preset would.
 */
object BaseUrlPresets {

    /**
     * MAIN / HEALTH_PROFILE / AI are baked into HttpClient/Ktorfit singletons built once by Koin,
     * so an override only takes effect after the app process restarts. ACCOUNT is resolved fresh
     * on every call and applies immediately.
     */
    val requiresRestart: Map<BaseUrlKey, Boolean> = mapOf(
        BaseUrlKey.MAIN to true,
        BaseUrlKey.ACCOUNT to false,
        BaseUrlKey.HEALTH_PROFILE to true,
        BaseUrlKey.AI to true
    )

    /**
     * Built from [BaseUrlKey.defaultValue] rather than repeated as literals, so this list cannot
     * drift from `NetworkConstants` and cannot grow a non-shipping address by accident.
     */
    val presets: Map<BaseUrlKey, List<BaseUrlPreset>> =
        BaseUrlKey.entries.associateWith { key ->
            listOf(BaseUrlPreset(label = DEFAULT_PRESET_LABEL, url = key.defaultValue))
        }

    private const val DEFAULT_PRESET_LABEL = "Default"
}
