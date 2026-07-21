package com.tamin.taminhamrah.data.local.models

data class TimerState(
    val secondsRemaining: Int? = null,
    val totalSeconds: Int = 0,
    val textWhenStopped: String = "00:00"
) {
    val displaySeconds: String = (secondsRemaining ?: textWhenStopped).toString()

    // Show 100% if seconds remaining is null
    val progressPercentage: Float = (secondsRemaining ?: totalSeconds) / totalSeconds.toFloat()

    override fun toString(): String =
        "Seconds Remaining $secondsRemaining, totalSeconds: $totalSeconds, progress: $progressPercentage"

}
