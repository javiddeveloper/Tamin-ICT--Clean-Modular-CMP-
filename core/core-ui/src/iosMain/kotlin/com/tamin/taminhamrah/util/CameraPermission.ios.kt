package com.tamin.taminhamrah.util

import androidx.compose.runtime.Composable
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberCameraPermission(): CameraPermission = object : CameraPermission {
    override val granted: Boolean
        get() = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) ==
            AVAuthorizationStatusAuthorized

    override fun request(onResult: (Boolean) -> Unit) {
        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { isGranted ->
            // The completion handler runs on an arbitrary background queue per Apple's docs, not
            // guaranteed to be the main thread — hop back before invoking a callback that may
            // drive UI (e.g. launching the camera picker).
            dispatch_async(dispatch_get_main_queue()) {
                onResult(isGranted)
            }
        }
    }
}
