package com.tamin.taminhamrah.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val darkThemeConfig: DarkThemeConfig,
) {
    companion object {
        val DEFAULT = UserData(darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM)
    }
}
