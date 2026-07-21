package com.tamin.taminhamrah.ui.aiAgent.ui.tools

import android.media.MediaPlayer
import java.io.IOException

class MediaPlayerManager {
    var mediaPlayer: MediaPlayer? = null
    var currentAudioPath: String? = null
    val currentPosition: Int
        get() = mediaPlayer?.currentPosition ?: 0
    val duration: Int
        get() = mediaPlayer?.duration ?: 0
    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying ?: false

    fun loadAudio(
        audioPath: String?,
        onPrepared: (Int) -> Unit,
        onCompletion: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        if (audioPath == null) return
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer()
        mediaPlayer!!.apply {
            try {
                setOnPreparedListener { mp ->
                    onPrepared(mp.duration)
                }

                setOnCompletionListener { mp ->
                    onCompletion()
                }

                setOnErrorListener { mp, what, extra ->
                    releaseMediaPlayer()
                    onError("خطا در پخش")
                    true
                }
                currentAudioPath = audioPath
                reset()
                setDataSource(audioPath) // Use Uri.parse for file paths
                prepareAsync()


            } catch (e: IOException) {
                e.printStackTrace()
                releaseMediaPlayer()
                onError("خطا در بارگزاری")
            }
        }
    }

    fun playOrPauseAudio(onPlay: () -> Unit, onPause: () -> Unit) {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            onPause()
        } else {
            mediaPlayer?.start()
            onPlay()
        }
    }

    fun stopAudio() {
        currentAudioPath = ""
        mediaPlayer?.stop()
    }

    fun pauseAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun resumeAudio() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    private fun resetAndRelease() {
        mediaPlayer?.reset()
        releaseMediaPlayer()
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentAudioPath = ""
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)

    }


}