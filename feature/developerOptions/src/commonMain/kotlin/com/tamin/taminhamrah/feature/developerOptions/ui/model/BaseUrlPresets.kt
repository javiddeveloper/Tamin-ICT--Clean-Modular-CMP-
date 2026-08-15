package com.tamin.taminhamrah.feature.developerOptions.ui.model

import com.tamin.taminhamrah.model.BaseUrlKey

data class BaseUrlPreset(val label: String, val url: String)

/**
 * Known named base URLs collected from this project's build config and from
 * old_android/'s Constants.kt (legacy, read-only reference — see docs/vault/Reference-old-android.md).
 */
object BaseUrlPresets {

    val displayNames: Map<BaseUrlKey, String> = mapOf(
        BaseUrlKey.MAIN to "آدرس اصلی سرویس‌ها",
        BaseUrlKey.ACCOUNT to "سرویس احراز هویت",
        BaseUrlKey.HEALTH_PROFILE to "پرونده سلامت من",
        BaseUrlKey.AI to "دستیار هوشمند"
    )

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
            BaseUrlPreset("پرونده سلامت من", "http://172.16.14.115:5700/api/")
        ),
        BaseUrlKey.AI to listOf(
            BaseUrlPreset("Production", "https://sw.tamin.ir/api/"),
            BaseUrlPreset("Dev IP", "http://172.16.15.54:9001/")
        )
    )
}
