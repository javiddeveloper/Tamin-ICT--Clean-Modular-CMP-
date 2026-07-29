package com.tamin.taminhamrah.utils

import com.tamin.taminhamrah.data.local.models.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class TimerUtil(private val timerScope: CoroutineScope) {
    private var _timerStateFlow = MutableStateFlow(TimerState())
    val timerStateFlow: StateFlow<TimerState> = _timerStateFlow
    private var job: Job? = null
    fun timerStart(totalSeconds: Int) {
        job = if (job == null || job?.isCompleted == true) {
            timerScope.launch {
                initTimer(totalSeconds) { remainingTime -> TimerState(remainingTime, totalSeconds) }
                    .onCompletion { _timerStateFlow.emit(TimerState()) }
                    .collect { _timerStateFlow.emit(it) }
            }
        } else {
            job?.cancel()
            null
        }
    }

    fun resumeTimer(){

            job = if (job != null || job?.isCompleted == false) {
                timerScope.launch {
                val secondsRemaining =timerStateFlow.value.secondsRemaining ?: 0
                initTimer(secondsRemaining){ remainingTime -> TimerState(remainingTime, secondsRemaining) }
                    .onCompletion { _timerStateFlow.emit(TimerState()) }
                    .collect { _timerStateFlow.emit(it) }
            }
            }else
                null
        }



    /**
     * The timer emits the total seconds immediately.
     * Each second after that, it will emit the next value.
     */
    private fun <DisplayState> initTimer(totalSeconds: Int, onTick: (Int) -> DisplayState): Flow<DisplayState> =
//        generateSequence(totalSeconds - 1 ) { it - 1 }.asFlow()
        (totalSeconds - 1 downTo 0).asFlow() // Emit total - 1 because the first was emitted onStart
            .onEach { delay(1000) } // Each second later emit a number
            .onStart { emit(totalSeconds) } // Emit total seconds immediately
            .conflate() // In case the operation onTick takes some time, conflate keeps the time ticking separately
            .transform { remainingSeconds: Int -> emit(onTick(remainingSeconds))}
}