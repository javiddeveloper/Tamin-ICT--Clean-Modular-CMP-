/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.model

enum class DarkThemeConfig(val configName: String, val osValue: Int) {
    FOLLOW_SYSTEM("Follow System", -1),
    LIGHT("Light", 1),
    DARK("Dark", 2);

    companion object {
        fun fromString(value: String): DarkThemeConfig {
            return entries.find { it.configName.equals(value, ignoreCase = true) } ?: FOLLOW_SYSTEM
        }
    }
}

