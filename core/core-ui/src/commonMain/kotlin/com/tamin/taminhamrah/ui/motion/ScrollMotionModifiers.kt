package com.tamin.taminhamrah.ui.motion

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.util.lerp as lerpFloat


private fun localProgress(global: Float, startProgress: Float, endProgress: Float): Float {
    if (endProgress <= startProgress) return if (global >= endProgress) 1f else 0f
    return ((global - startProgress) / (endProgress - startProgress)).coerceIn(0f, 1f)
}


fun Modifier.motionFade(
    state: ScrollMotionState,
    startProgress: Float = 0f,
    endProgress: Float = 1f
): Modifier = this.graphicsLayer {
    val p = localProgress(state.progress, startProgress, endProgress)
    alpha = 1f - p
}

fun Modifier.motionScale(
    state: ScrollMotionState,
    minScale: Float = 0.8f,
    maxScale: Float = 1f,
    startProgress: Float = 0f,
    endProgress: Float = 1f,
    transformOrigin: TransformOrigin = TransformOrigin.Center
): Modifier = this.graphicsLayer {
    val p = localProgress(state.progress, startProgress, endProgress)
    val scale = lerpFloat(maxScale, minScale, p)
    scaleX = scale
    scaleY = scale
    this.transformOrigin = transformOrigin
}


fun Modifier.motionParallax(
    state: ScrollMotionState,
    parallaxPx: Float = 80f,
    startProgress: Float = 0f,
    endProgress: Float = 1f
): Modifier = this.graphicsLayer {
    val p = localProgress(state.progress, startProgress, endProgress)
    translationY = -(parallaxPx * p)
}


fun Modifier.motionParallax(
    state: ScrollMotionState,
    parallaxDistance: Dp,
    startProgress: Float = 0f,
    endProgress: Float = 1f
): Modifier = this.graphicsLayer {
    val p = localProgress(state.progress, startProgress, endProgress)
    translationY = -(parallaxDistance.toPx() * p)
}


fun Modifier.motionBackgroundColor(
    state: ScrollMotionState,
    startColor: Color,
    endColor: Color,
    startProgress: Float = 0f,
    endProgress: Float = 1f
): Modifier = this.drawBehind {
    val p = localProgress(state.progress, startProgress, endProgress)
    val color = lerpColor(startColor, endColor, p)
    drawRect(color = color)
}
