package com.tamin.taminhamrah.feature.agent.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * Cross-platform microphone recorder. Ported from old_Android's `AudioRecorder`
 * (which used the WaveRecorder library) to an expect/actual engine so both Android
 * and iOS share the same ViewModel logic.
 *
 * Amplitude is exposed as a [StateFlow] so the Compose waveform can render the live
 * signal; the last-5-seconds red colouring and the 20s cap are driven in the ViewModel.
 */
interface VoiceRecorder {
    /** Latest microphone amplitude (roughly 0..32767) while recording, else 0. */
    val amplitude: StateFlow<Int>

    /** Whether a recording is currently in progress. */
    val isRecording: StateFlow<Boolean>

    /** Begins recording into [filePath] (a path returned by [newRecordingPath]). */
    fun start(filePath: String)

    /** Stops and finalizes the current recording. Safe to call when idle. */
    fun stop()

    /** A fresh, unique absolute file path inside the platform cache directory. */
    fun newRecordingPath(): String
}

/**
 * Cross-platform audio player for previewing a pending recording and for playing
 * voice bubbles already in the chat list. Ported from old_Android's `MediaPlayerManager`.
 * Only one instance should be "active" at a time — the ViewModel enforces single playback.
 */
interface VoicePlayer {
    /** Current playback position in milliseconds. */
    val positionMs: StateFlow<Int>

    /** Total duration of the loaded clip in milliseconds (0 until prepared). */
    val durationMs: StateFlow<Int>

    /** Whether audio is currently playing. */
    val isPlaying: StateFlow<Boolean>

    /**
     * Prepares [filePath] for playback. [onReady] fires once prepared (with duration
     * available via [durationMs]); [onComplete] fires at the end; [onError] on failure.
     */
    fun load(
        filePath: String,
        onReady: () -> Unit = {},
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    )

    /** Toggles play/pause on the loaded clip. */
    fun playPause()

    /** Seeks to [ms] within the loaded clip. */
    fun seekTo(ms: Int)

    /** Stops playback and resets position to 0. */
    fun stop()

    /** Releases native resources. Call when the clip is no longer needed. */
    fun release()
}

/** Creates the platform microphone recorder. */
expect fun createVoiceRecorder(): VoiceRecorder

/** Creates a platform audio player. */
expect fun createVoicePlayer(): VoicePlayer

/** Absolute path of the platform cache directory used to store voice recordings. */
expect fun agentCacheDir(): String

/** Reads the whole file at [path] into memory — used to build the upload multipart. */
expect fun readFileBytes(path: String): ByteArray

/** Deletes the file at [path] (e.g. a discarded recording). Returns true on success. */
expect fun deleteFile(path: String): Boolean
