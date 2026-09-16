package com.tamin.taminhamrah.feature.agent.audio

import kotlinx.coroutines.withContext
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
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
import java.io.RandomAccessFile
import kotlin.math.abs
import java.util.UUID

private fun appContext(): Context = GlobalContext.get().get()

private const val MIN_BUFFER_BYTES = 3_200 // 100 ms of 16 kHz 16-bit mono
private const val WRITER_JOIN_TIMEOUT_MS = 1_000L
private const val POSITION_POLL_MS = 100L

// ─── Recorder ───────────────────────────────────────────────────────────────

/**
 * Records raw 16 kHz mono PCM with [AudioRecord] straight into a WAV file ([WavFormat]); the
 * header's sizes are filled in when recording stops. MediaRecorder cannot write WAV.
 */
private class AndroidVoiceRecorder : VoiceRecorder {
    private val _amplitude = MutableStateFlow(0)
    override val amplitude: StateFlow<Int> = _amplitude.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private var recorder: AudioRecord? = null
    private var writer: Thread? = null

    /** Set by [cancel]: the writer deletes the file instead of finishing it. */
    @Volatile
    private var discard = false

    @SuppressLint("MissingPermission") // The screen asks for RECORD_AUDIO before recording starts.
    override fun start(filePath: String) {
        if (_isRecording.value) return
        val bufferSize = maxOf(
            AudioRecord.getMinBufferSize(WavFormat.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT),
            MIN_BUFFER_BYTES,
        )
        val record = runCatching {
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                WavFormat.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
            ).takeIf { it.state == AudioRecord.STATE_INITIALIZED }
        }.getOrNull() ?: return

        if (runCatching { record.startRecording() }.isFailure) {
            record.release()
            return
        }
        recorder = record
        discard = false
        _isRecording.value = true
        writer = Thread({ writeWav(record, filePath, bufferSize) }, "agent-voice-writer").apply { start() }
    }

    /**
     * Runs on the writer thread and owns the [AudioRecord] from here on: it releases it when the loop
     * ends, so nobody has to wait for the loop before releasing.
     */
    private fun writeWav(record: AudioRecord, filePath: String, bufferSize: Int) {
        val target = File(filePath)
        runCatching {
            RandomAccessFile(target, "rw").use { file ->
                file.setLength(0)
                file.write(ByteArray(WavFormat.HEADER_SIZE))
                val buffer = ByteArray(bufferSize)
                var dataSize = 0
                while (_isRecording.value) {
                    val read = record.read(buffer, 0, buffer.size)
                    if (read <= 0) continue
                    file.write(buffer, 0, read)
                    dataSize += read
                    _amplitude.value = peakOf(buffer, read)
                }
                file.seek(0)
                file.write(WavFormat.header(dataSize))
            }
        }
        runCatching { record.release() }
        if (discard) runCatching { target.delete() }
    }

    /** Loudest 16-bit little-endian sample in the chunk, 0..32767 like MediaRecorder's maxAmplitude. */
    private fun peakOf(buffer: ByteArray, length: Int): Int {
        var peak = 0
        var i = 0
        while (i + 1 < length) {
            val sample = (buffer[i].toInt() and 0xFF) or (buffer[i + 1].toInt() shl 8)
            peak = maxOf(peak, abs(sample))
            i += 2
        }
        return peak.coerceAtMost(Short.MAX_VALUE.toInt())
    }

    override suspend fun stop() {
        val finishing = endRecording() ?: return
        // The file is read for upload right after this returns, so its header must be written. The
        // wait happens off the caller's thread.
        withContext(Dispatchers.IO) { finishing.join(WRITER_JOIN_TIMEOUT_MS) }
    }

    override fun cancel() {
        discard = true
        endRecording()
    }

    /** Ends the read loop and hands back the writer thread, which finishes and releases on its own. */
    private fun endRecording(): Thread? {
        if (!_isRecording.value) return null
        _isRecording.value = false
        runCatching { recorder?.stop() }
        recorder = null
        _amplitude.value = 0
        return writer.also { writer = null }
    }

    override fun newRecordingPath(): String {
        val dir = File(agentCacheDir())
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "${UUID.randomUUID()}.${WavFormat.EXTENSION}").absolutePath
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
