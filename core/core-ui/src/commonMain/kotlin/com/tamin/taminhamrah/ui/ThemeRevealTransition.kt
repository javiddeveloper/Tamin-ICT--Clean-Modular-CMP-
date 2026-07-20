package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.time.Duration.Companion.milliseconds

class ThemeRevealController internal constructor(
    internal val graphicsLayer: GraphicsLayer,
    private val scope: CoroutineScope,
) {
    var frozenFrame by mutableStateOf<ImageBitmap?>(null)
        private set
    var center by mutableStateOf(Offset.Zero)
        private set
    val radius = Animatable(0f)
    internal var maxRadius = 0f
    fun trigger(origin: Offset, applyNewTheme: () -> Unit) {
        if (frozenFrame != null) return
        scope.launch {
            frozenFrame = graphicsLayer.toImageBitmap()
            center = origin
            radius.snapTo(0f)
            // 1. درخواست تغییر تم
            applyNewTheme()
            // 2. ایجاد تاخیر برای نفس کشیدن Main Thread!
            // این زمان به Compose اجازه می‌دهد ریکامپوزیشن و محاسبات Haze (Blur) را
            // کامل کند قبل از اینکه انیمیشن شروع شود.
            delay(100.milliseconds)
            // 3. شروع انیمیشن روی تم جدید که حالا کاملاً رندر و آماده شده است
            radius.animateTo(
                targetValue = maxRadius,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            )
            frozenFrame = null
        }
    }
}

@Composable
fun rememberThemeRevealController(): ThemeRevealController {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    return remember { ThemeRevealController(graphicsLayer, scope) }
}

val LocalThemeRevealController = compositionLocalOf<ThemeRevealController?> { null }


@Composable
fun ThemeRevealHost(
    controller: ThemeRevealController = rememberThemeRevealController(),
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                controller.maxRadius = hypot(
                    coords.size.width.toFloat(),
                    coords.size.height.toFloat(),
                )
            }
            .drawWithContent {
                controller.graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(controller.graphicsLayer)
            },
    ) {
        content()
        val frame = controller.frozenFrame
        if (frame != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val hole = Path().apply {
                    addOval(Rect(center = controller.center, radius = controller.radius.value))
                }
                clipPath(hole, clipOp = ClipOp.Difference) {
                    drawImage(frame)
                }
            }
        }
    }
}
