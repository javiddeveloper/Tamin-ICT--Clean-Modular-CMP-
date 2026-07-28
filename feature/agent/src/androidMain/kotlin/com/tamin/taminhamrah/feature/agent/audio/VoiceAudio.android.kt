package com.tamin.taminhamrah.feature.agent.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import java.io.File
import java.util.UUID

private fun appContext(): Context = GlobalContext.get().get()

private const val AMPLITUDE_POLL_MS = 80L
private const val POSITION_POLL_MS = 100L

// ─── Recorder ───────────────────────────────────────────────────────────────

private class AndroidVoiceRecorder : VoiceRecorder {
    private val _amplitude = MutableStateFlow(0)
    override val amplitude: StateFlow<Int> = _amplitude.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private var recorder: MediaRecorder? = null
    private var scope: CoroutineScope? = null

    override fun start(filePath: String) {
        if (_isRecording.value) return
        @Suppress("DEPRECATION")
        val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(appContext())
        } else {
            MediaRecorder()
        }
        mr.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128_000)
            setAudioSamplingRate(44_100)
            setOutputFile(filePath)
            prepare()
            start()
        }
        recorder = mr
        _isRecording.value = true

        val s = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        scope = s
        s.launch {
            while (isActive && _isRecording.value) {
                _amplitude.value = runCatching { recorder?.maxAmplitude ?: 0 }.getOrDefault(0)
                delay(AMPLITUDE_POLL_MS)
            }
        }
    }

    override fun stop() {
        if (!_isRecording.value) return
        _isRecording.value = false
        scope?.cancel()
        scope = null
        runCatching {
            recorder?.stop()
        }
        runCatching { recorder?.release() }
        recorder = null
        _amplitude.value = 0
    }

    override fun newRecordingPath(): String {
        val dir = File(agentCacheDir())
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "voice_${UUID.randomUUID()}.m4a").absolutePath
    }
}

// ─── Player ─────────────────────────────────────────────────────────────────

private class AndroidVoicePlayer : VoicePlayer {
    private val _positionMs = MutableStateFlow(0)
    override val positionMs: StateFlow<Int> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0)
    override val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var player: MediaPlayer? = null
    private var scope: CoroutineScope? = null
    private var positionJob: Job? = null

    override fun load(
        filePath: String,
        onReady: () -> Unit,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        release()
        val mp = MediaPlayer()
        player = mp
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        mp.apply {
            setOnPreparedListener {
                _durationMs.value = it.duration
                onReady()
            }
            setOnCompletionListener {
                _isPlaying.value = false
                _positionMs.value = 0
                stopPositionUpdates()
                onComplete()
            }
            setOnErrorListener { _, what, extra ->
                // The player is now in its error state, where every accessor throws.
                // Tear it down before anything else can touch it.
                release()
                onError("پخش این فایل صوتی ممکن نشد. ($what:$extra)")
                true
            }
            runCatching {
                reset()
                setDataSource(filePath)
                prepareAsync()
            }.onFailure {
                release()
                onError("خطا در بارگذاری صدا")
            }
        }
    }

    override fun playPause() {
        val mp = player ?: return
        // Every MediaPlayer accessor throws IllegalStateException once the player has
        // errored or been released, so nothing here may touch it unguarded.
        val playing = mp.isPlayingOrFalse()
        val changed = runCatching {
            if (playing) mp.pause() else mp.start()
        }.isSuccess

        if (!changed) {
            release()
            return
        }
        _isPlaying.value = !playing
        if (playing) stopPositionUpdates() else startPositionUpdates()
    }

    override fun seekTo(ms: Int) {
        runCatching { player?.seekTo(ms) }
        _positionMs.value = ms
    }

    override fun stop() {
        runCatching { player?.pause() }
        runCatching { player?.seekTo(0) }
        _positionMs.value = 0
        _isPlaying.value = false
        stopPositionUpdates()
    }

    override fun release() {
        stopPositionUpdates()
        scope?.cancel()
        scope = null
        // Drop the reference first: the polling coroutine may not have observed the
        // cancellation yet, and a released player would throw the moment it looks.
        val released = player
        player = null
        runCatching { released?.release() }
        _isPlaying.value = false
        _positionMs.value = 0
        _durationMs.value = 0
    }

    private fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = scope?.launch {
            while (isActive) {
                val mp = player ?: break
                if (!mp.isPlayingOrFalse()) break
                _positionMs.value = runCatching { mp.currentPosition }.getOrDefault(0)
                delay(POSITION_POLL_MS)
            }
        }
    }

    /** `isPlaying` throws in the error/released state; treat that as "not playing". */
    private fun MediaPlayer.isPlayingOrFalse(): Boolean =
        runCatching { isPlaying }.getOrDefault(false)

    private fun stopPositionUpdates() {
        positionJob?.cancel()
        positionJob = null
    }
}

// ─── Factories ────────────────────────────────────────────────────────────────

actual fun createVoiceRecorder(): VoiceRecorder = AndroidVoiceRecorder()

actual fun createVoicePlayer(): VoicePlayer = AndroidVoicePlayer()

actual fun agentCacheDir(): String = appContext().cacheDir.absolutePath

actual fun readFileBytes(path: String): ByteArray =
    runCatching { File(path).readBytes() }.getOrDefault(ByteArray(0))

actual fun deleteFile(path: String): Boolean =
    runCatching { File(path).delete() }.getOrDefault(false)
