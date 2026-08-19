package com.tamin.taminhamrah.feature.orotezprotez.camera

import androidx.compose.runtime.Composable

/** Cross-platform camera permission gate for step 3's "take a photo" document source, mirroring `MicPermission` in feature/agent. */
interface CameraPermission {
    /** Whether CAMERA (Android) / camera (iOS) is currently granted. */
    val granted: Boolean

    /** Triggers the system permission prompt; [onResult] reports the outcome. */
    fun request(onResult: (Boolean) -> Unit = {})
}

@Composable
expect fun rememberCameraPermission(): CameraPermission
