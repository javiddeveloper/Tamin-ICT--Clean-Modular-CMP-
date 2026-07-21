package com.tamin.taminhamrah.ui.aiAgent.ui.tools

import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import com.tamin.taminhamrah.utils.extentions.randomUUID
import java.io.File

class AudioRecorder {
    private var waveRecorder: WaveRecorder? = null

    fun recordAudio(
        cacheDirectory: String,
        onStop: () -> Unit,
        onAmplitude: (Int) -> Unit,
        onFilePathCreated: (String) -> Unit,
        onRecorde: () -> Unit,
        onRecreate: () -> Unit
    ) {
        val filePath = "$cacheDirectory${File.separator}${randomUUID()}.wav"
        if (waveRecorder == null) {
            waveRecorder = WaveRecorder(filePath)
        }else {
            waveRecorder!!.changeFilePath(filePath)
        }
        onFilePathCreated(filePath)
        waveRecorder?.let {
            onRecreate()
            it.startRecording()
            it.onAmplitudeListener = {
                onAmplitude(it)
            }
            it.onStateChangeListener = {
                when (it) {
//                RecorderState.RECORDING -> mViewModel.updateChatType(ChatType.VOICE_RECORDING)
                    RecorderState.STOP -> onStop()
                    RecorderState.RECORDING -> onRecorde()

                    else -> {}
                }
            }

        }

    }


    fun stopRecording() {
        waveRecorder?.stopRecording()

    }


}