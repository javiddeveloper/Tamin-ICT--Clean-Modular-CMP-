package com.tamin.taminhamrah.ui.blur

import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

/**
 * A lifecycle-aware wrapper around Haze's hazeEffect modifier.
 * It automatically disables the effect when the application is backgrounded
 * (Lifecycle < STARTED) to prevent EGL_BAD_ACCESS crashes on Android RenderNodes.
 * 
 * @param state The HazeState to use.
 * @param style The HazeStyle to apply.
 * @param fallbackColor An optional color to render as a background when the blur is disabled.
 */
fun Modifier.safeHazeEffect(
    state: HazeState,
    style: HazeStyle,
    fallbackColor: Color = Color.Unspecified,
    isEnabled: Boolean = true
): Modifier = composed {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()
    val showBlur = isEnabled && lifecycleState >= Lifecycle.State.STARTED

    if (showBlur) {
        this.hazeEffect(state = state, style = style)
    } else {
        if (fallbackColor != Color.Unspecified) {
            this.background(fallbackColor)
        } else {
            this
        }
    }
}

/**
 * A lifecycle-aware wrapper around Haze's hazeSource modifier.
 * It automatically disables the source capture when the application is backgrounded
 * (Lifecycle < STARTED) to prevent EGL_BAD_ACCESS crashes on Android RenderNodes.
 * 
 * @param state The HazeState to register to.
 * @param isEnabled Whether the source should be active.
 */
fun Modifier.safeHazeSource(
    state: HazeState,
    isEnabled: Boolean = true
): Modifier = composed {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()
    val showBlur = isEnabled && lifecycleState >= Lifecycle.State.STARTED

    if (showBlur) {
        this.hazeSource(state = state)
    } else {
        this
    }
}
