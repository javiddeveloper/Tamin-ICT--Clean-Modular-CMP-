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
 * Known named base URLs collected from this project's build config and from
 * old_android/'s Constants.kt (legacy, read-only reference — see docs/vault/Reference-old-android.md).
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

    val presets: Map<BaseUrlKey, List<BaseUrlPreset>> = mapOf(
        BaseUrlKey.MAIN to listOf(
            BaseUrlPreset("Production", "https://eservices.tamin.ir/api/"),
            BaseUrlPreset("Test", "https://eservices.test.org:9090/api/")
        ),
        BaseUrlKey.ACCOUNT to listOf(
            BaseUrlPreset("Production", "https://account.tamin.ir/auth/"),
            BaseUrlPreset("Test", "https://account-test.tamin.ir:9090/auth/"),
            BaseUrlPreset("Old Android Test", "http://s-naghavi.tamin.org:7002/auth/")
        ),
        BaseUrlKey.HEALTH_PROFILE to listOf(
            BaseUrlPreset("Test", "http://172.16.14.115:5700/api/")
        ),
        BaseUrlKey.AI to listOf(
            BaseUrlPreset("Production", "https://sw.tamin.ir/api/"),
            BaseUrlPreset("Dev IP", "http://172.16.15.54:9001/")
        )
    )
}
