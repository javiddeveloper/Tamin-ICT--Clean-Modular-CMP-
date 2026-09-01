package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * State holder for countdown timers across the app.
 *
 * Provides Persian formatted `mm:ss` string and low-time indication (≤ 60s).
 */
@Stable
class TaminCountdownState(
    initialSeconds: Int = 0,
    private val onFinish: (() -> Unit)? = null,
) {
    var timeLeftSeconds by mutableIntStateOf(initialSeconds)
        private set

    var isRunning by mutableStateOf(false)
        private set

    val isFinished: Boolean get() = isRunning && timeLeftSeconds == 0
    val isLowTime: Boolean get() = timeLeftSeconds in 1..60

    val formattedTime: String
        get() {
            val minutes = timeLeftSeconds / 60
            val seconds = timeLeftSeconds % 60
            val minStr = minutes.toString().padStart(2, '0')
            val secStr = seconds.toString().padStart(2, '0')
            return "$minStr:$secStr".toPersianDigits()
        }

    fun start(seconds: Int = 300) {
        timeLeftSeconds = seconds
        isRunning = true
    }

    fun stop() {
        isRunning = false
    }

    fun reset() {
        isRunning = false
        timeLeftSeconds = 0
    }

    internal suspend fun runTimer() {
        while (isRunning && timeLeftSeconds > 0) {
            delay(1.seconds)
            if (!isRunning) break
            timeLeftSeconds--
            if (timeLeftSeconds == 0) {
                isRunning = false
                onFinish?.invoke()
            }
        }
    }
}

@Composable
fun rememberTaminCountdownState(
    initialSeconds: Int = 0,
    onFinish: (() -> Unit)? = null,
): TaminCountdownState {
    val state = remember(onFinish) { TaminCountdownState(initialSeconds, onFinish) }
    LaunchedEffect(state.isRunning, state.timeLeftSeconds) {
        if (state.isRunning && state.timeLeftSeconds > 0) {
            state.runTimer()
        }
    }
    return state
}
