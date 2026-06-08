package com.tamin.taminhamrah.util

import com.tamin.taminhamrah.core.domain.BuildConfig

actual object AppConfig {
    actual val isDebug: Boolean = BuildConfig.DEBUG
}
