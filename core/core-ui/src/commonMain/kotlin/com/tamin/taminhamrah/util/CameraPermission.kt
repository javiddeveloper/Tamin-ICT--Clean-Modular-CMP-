package com.tamin.taminhamrah.util

import androidx.compose.runtime.Composable

interface CameraPermission {
    val granted: Boolean

    fun request(onResult: (Boolean) -> Unit = {})
}

@Composable
expect fun rememberCameraPermission(): CameraPermission


