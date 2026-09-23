package com.tamin.taminhamrah.ui.blur

import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeInputScale
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
 * @param inputScale Resolution the blur is computed at. Haze's own default is
 *   [HazeInputScale.Default] (= no downsampling), so leaving this out keeps every existing
 *   caller rendering exactly as before. A surface whose tint is opaque enough that the blurred
 *   detail underneath is not actually legible — the assistant's glass bars, for instance — can
 *   pass [HazeInputScale.Auto] and get the same look for a fraction of the fill rate: `Auto`
 *   resolves to a 0.33 scale factor for any blur radius of 7.dp or more, i.e. roughly a ninth
 *   of the pixels. Only opt in where you have looked at the result; on a lightly tinted surface
 *   the downsampling is visible.
 */
@OptIn(ExperimentalHazeApi::class)
fun Modifier.safeHazeEffect(
    state: HazeState,
    style: HazeStyle,
    fallbackColor: Color = Color.Unspecified,
    isEnabled: Boolean = true,
    inputScale: HazeInputScale = HazeInputScale.Default,
): Modifier = composed {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()
    val showBlur = isEnabled && lifecycleState >= Lifecycle.State.STARTED

    // Remembered so the block keeps its identity across recompositions: HazeEffectNodeElement is
    // a data class holding it, and a fresh lambda each time would make every element comparison
    // fail and re-run the node's update() for nothing.
    val effectBlock: (HazeEffectScope.() -> Unit)? = remember(inputScale) {
        if (inputScale == HazeInputScale.Default) {
            null
        } else {
            // Bound to a differently-named local on purpose: inside a HazeEffectScope lambda an
            // unqualified `inputScale` resolves to the receiver's own property, so
            // `this.inputScale = inputScale` would assign the field to itself.
            val scale = inputScale
            val block: HazeEffectScope.() -> Unit = { this.inputScale = scale }
            block
        }
    }

    if (showBlur) {
        this.hazeEffect(state = state, style = style, block = effectBlock)
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
