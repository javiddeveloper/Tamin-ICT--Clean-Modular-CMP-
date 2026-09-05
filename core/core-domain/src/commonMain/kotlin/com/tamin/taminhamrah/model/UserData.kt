package com.tamin.taminhamrah.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val isBiometricEnabled: Boolean = false,
    val hasAskedToEnableBiometric: Boolean = false,
    val fontSize: FontSizeOption = FontSizeOption.MEDIUM,
) {
    companion object {
        val DEFAULT = UserData(
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            fontSize = FontSizeOption.MEDIUM,
        )
    }
}
