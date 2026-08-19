package com.tamin.taminhamrah.feature.taminServices.occurrence.camera

import androidx.compose.runtime.Composable

/** Cross-platform camera permission gate for Step6's "take a photo" document source. */
interface CameraPermission {
    /** Whether CAMERA (Android) / camera (iOS) is currently granted. */
    val granted: Boolean

    /** Triggers the system permission prompt; [onResult] reports the outcome. */
    fun request(onResult: (Boolean) -> Unit = {})
}

@Composable
expect fun rememberCameraPermission(): CameraPermission
