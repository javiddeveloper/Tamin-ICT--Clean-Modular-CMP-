@file:OptIn(ExperimentalForeignApi::class)

package com.tamin.taminhamrah.feature.agent.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.NSNumber
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import kotlin.math.pow

// kAudioFormatMPEG4AAC fourcc ('aac ') — hardcoded to avoid an AudioToolbox cinterop import.
private const val K_AUDIO_FORMAT_MPEG4_AAC = 1633772320L
private const val AV_AUDIO_QUALITY_HIGH = 96L // AVAudioQualityHigh

private const val AMPLITUDE_POLL_MS = 80L
private const val POSITION_POLL_MS = 100L

private fun configureSession() {
    val session = AVAudioSession.sharedInstance()
    runCatching { session.setCategory(AVAudioSessionCategoryPlayAndRecord, null) }
    runCatching { session.setActive(true, null) }
}

// ─── Recorder ───────────────────────────────────────────────────────────────

private class IosVoiceRecorder : VoiceRecorder {
    private val _amplitude = MutableStateFlow(0)
    override val amplitude: StateFlow<Int> = _amplitude.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private var recorder: AVAudioRecorder? = null
    private var scope: CoroutineScope? = null

    override fun start(filePath: String) {
        if (_isRecording.value) return
        configureSession()
        val url = NSURL.fileURLWithPath(filePath)
        val settings = mapOf<Any?, Any?>(
            AVFormatIDKey to NSNumber(unsignedInt = K_AUDIO_FORMAT_MPEG4_AAC.toUInt()),
            AVSampleRateKey to NSNumber(double = 44_100.0),
            AVNumberOfChannelsKey to NSNumber(int = 1),
            AVEncoderAudioQualityKey to NSNumber(long = AV_AUDIO_QUALITY_HIGH)
        )
        val rec = AVAudioRecorder(uRL = url, settings = settings, error = null) ?: return
        rec.meteringEnabled = true
        rec.prepareToRecord()
        rec.record()
        recorder = rec
        _isRecording.value = true

        val s = CoroutineScope(Dispatchers.Main)
        scope = s
        s.launch {
            while (isActive && _isRecording.value) {
                recorder?.let {
                    it.updateMeters()
                    val db = it.averagePowerForChannel(0u)
                    // dB (-160..0) → linear (0..1) → 0..32767
                    val linear = 10.0.pow(db / 20.0).coerceIn(0.0, 1.0)
                    _amplitude.value = (linear * 32767).toInt()
                }
                delay(AMPLITUDE_POLL_MS)
            }
        }
    }

    override fun stop() {
        if (!_isRecording.value) return
        _isRecording.value = false
        scope?.cancel()
        scope = null
        runCatching { recorder?.stop() }
        recorder = null
        _amplitude.value = 0
    }

    override fun newRecordingPath(): String {
        return agentCacheDir() + "/voice_" + NSUUID().UUIDString() + ".m4a"
    }
}

// ─── Player ─────────────────────────────────────────────────────────────────

private class IosVoicePlayer : VoicePlayer {
    private val _positionMs = MutableStateFlow(0)
    override val positionMs: StateFlow<Int> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0)
    override val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var player: AVAudioPlayer? = null
    private var scope: CoroutineScope? = null
    private var positionJob: Job? = null

    override fun load(
        filePath: String,
        onReady: () -> Unit,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        release()
        configureSession()
        val url = NSURL.fileURLWithPath(filePath)
        val p = AVAudioPlayer(contentsOfURL = url, error = null)
        if (p == null) {
            onError("خطا در بارگذاری صدا")
            return
        }
        player = p
        scope = CoroutineScope(Dispatchers.Main)
        p.prepareToPlay()
        _durationMs.value = (p.duration * 1000).toInt()
        onReady()
        // AVAudioPlayer has a delegate for completion; we approximate via the position poll.
        onCompleteCallback = onComplete
    }

    private var onCompleteCallback: (() -> Unit)? = null

    override fun playPause() {
        val p = player ?: return
        if (p.playing) {
            p.pause()
            _isPlaying.value = false
            stopPositionUpdates()
        } else {
            p.play()
            _isPlaying.value = true
            startPositionUpdates()
        }
    }

    override fun seekTo(ms: Int) {
        player?.let { it.currentTime = ms / 1000.0 }
        _positionMs.value = ms
    }

    override fun stop() {
        player?.let {
            it.pause()
            it.currentTime = 0.0
        }
        _positionMs.value = 0
        _isPlaying.value = false
        stopPositionUpdates()
    }

    override fun release() {
        stopPositionUpdates()
        scope?.cancel()
        scope = null
        runCatching { player?.stop() }
        player = null
        _isPlaying.value = false
        _positionMs.value = 0
        _durationMs.value = 0
    }

    private fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = scope?.launch {
            while (isActive) {
                val p = player ?: break
                _positionMs.value = (p.currentTime * 1000).toInt()
                if (!p.playing) {
                    // Reached the end.
                    if (_positionMs.value >= _durationMs.value - POSITION_POLL_MS) {
                        _isPlaying.value = false
                        _positionMs.value = 0
                        onCompleteCallback?.invoke()
                    }
                    break
                }
                delay(POSITION_POLL_MS)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionJob?.cancel()
        positionJob = null
    }
}

// ─── Factories ────────────────────────────────────────────────────────────────

actual fun createVoiceRecorder(): VoiceRecorder = IosVoiceRecorder()

actual fun createVoicePlayer(): VoicePlayer = IosVoicePlayer()

actual fun agentCacheDir(): String = NSTemporaryDirectory()

actual fun readFileBytes(path: String): ByteArray {
    val data = NSData.dataWithContentsOfFile(path) ?: return ByteArray(0)
    val length = data.length.toInt()
    if (length == 0) return ByteArray(0)
    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length)
    }
    return bytes
}

actual fun deleteFile(path: String): Boolean =
    NSFileManager.defaultManager.removeItemAtPath(path, null)
