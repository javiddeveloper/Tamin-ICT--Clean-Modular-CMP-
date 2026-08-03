package com.tamin.taminhamrah.feature.agent.audio

import androidx.compose.runtime.Composable

/** Cross-platform microphone permission gate for the voice recorder. */
interface MicPermission {
    /** Whether RECORD_AUDIO (Android) / microphone (iOS) is currently granted. */
    val granted: Boolean

    /** Triggers the system permission prompt; [onResult] reports the outcome. */
    fun request(onResult: (Boolean) -> Unit = {})
}

@Composable
expect fun rememberMicPermission(): MicPermission
