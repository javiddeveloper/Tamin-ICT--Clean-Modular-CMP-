package com.tamin.taminhamrah.feature.agent.audio

import androidx.compose.runtime.Composable
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted

@Composable
actual fun rememberMicPermission(): MicPermission = object : MicPermission {
    override val granted: Boolean
        get() = AVAudioSession.sharedInstance().recordPermission == AVAudioSessionRecordPermissionGranted

    override fun request(onResult: (Boolean) -> Unit) {
        AVAudioSession.sharedInstance().requestRecordPermission { isGranted ->
            onResult(isGranted)
        }
    }
}
