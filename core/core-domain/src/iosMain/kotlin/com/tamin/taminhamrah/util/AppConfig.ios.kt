package com.tamin.taminhamrah.util

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

actual object AppConfig {
    @OptIn(ExperimentalNativeApi::class)
    actual val isDebug: Boolean = Platform.isDebugBinary
}
