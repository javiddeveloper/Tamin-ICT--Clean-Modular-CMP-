package com.tamin.taminhamrah.ui.motion

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.util.lerp as lerpFloat

/**
 * global (progress کلی صفحه، 0f..1f) را به یک بازه‌ی محلی [startProgress]..[endProgress]
 * نگاشت می‌کند. این یعنی هر Modifier می‌تواند "پنجره‌ی" خودش از اسکرول را تعریف کند؛
 * مثلاً یک عنوان فقط بعد از اینکه هدر ۵۰٪ جمع شد شروع به محو شدن کند.
 */
private fun localProgress(global: Float, startProgress: Float, endProgress: Float): Float {
    if (endProgress <= startProgress) return if (global >= endProgress) 1f else 0f
    return ((global - startProgress) / (endProgress - startProgress)).coerceIn(0f, 1f)
}

/**
 * المان را هنگام اسکرول محو می‌کند.
 *
 * نکته‌ی کلیدی Performance: از overload لامبدایی graphicsLayer { } استفاده شده، نه
 * پارامترهای مستقیم. این لامبدا در فاز Layer/Draw اجرا می‌شود، نه در فاز Composition —
 * یعنی خواندن [ScrollMotionState.progress] اینجا هرگز باعث Recomposition این کامپوزبل
 * نمی‌شود؛ فقط لایه‌ی گرافیکی‌اش دوباره رکورد می‌شود. این همان تکنیک اصلی درخواستی است.
 */
fun Modifier.motionFade(
    state: ScrollMotionState,
    startProgress: Float = 0f,
    endProgress: Float = 1f
): Modifier = this.graphicsLayer {
    val p = localProgress(state.progress, startProgress, endProgress)
    alpha = 1f - p
}

/**
 * المان (مثلاً یک عنوان یا آواتار) را هنگام اسکرول کوچک می‌کند.
 */
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

/**
 * پارالاکس کلاسیک: المان (معمولاً تصویر هدر) با کسری از سرعت اسکرول جابه‌جا می‌شود و
 * حس عمق ایجاد می‌کند. [parallaxPx] حداکثر جابه‌جایی است که وقتی progress به 1f برسد
 * اعمال می‌شود.
 */
fun Modifier.motionParallax(
    state: ScrollMotionState,
    parallaxPx: Float = 80f,
    startProgress: Float = 0f,
    endProgress: Float = 1f
): Modifier = this.graphicsLayer {
    val p = localProgress(state.progress, startProgress, endProgress)
    translationY = -(parallaxPx * p)
}

/**
 * نسخه‌ی Dp-محور همان motionParallax — برای هماهنگی با توکن‌های Spacing که معمولاً بر
 * حسب Dp تعریف می‌شوند. تبدیل به px همین‌جا در فاز Draw انجام می‌شود؛ چون
 * GraphicsLayerScope خودش یک Density است، نیازی به LocalDensity نیست.
 */
fun Modifier.motionParallax(
    state: ScrollMotionState,
    parallaxDistance: Dp,
    startProgress: Float = 0f,
    endProgress: Float = 1f
): Modifier = this.graphicsLayer {
    val p = localProgress(state.progress, startProgress, endProgress)
    translationY = -(parallaxDistance.toPx() * p)
}

/**
 * جایزه: تغییر رنگ (مثلاً یک Scrim پشت TopBar یا تغییر رنگ Background) از طریق
 * graphicsLayer ممکن نیست چون آن API پراپرتی رنگ ندارد. اما همان ترفند "بدون
 * Recomposition" این‌جا هم صدق می‌کند: لامبدای drawBehind هم در فاز Draw اجرا می‌شود.
 * خواندن progress این‌جا دقیقاً به همان اندازه‌ی graphicsLayer ارزان است.
 */
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
