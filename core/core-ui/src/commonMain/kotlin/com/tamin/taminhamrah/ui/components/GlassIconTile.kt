package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

@Composable
fun GlassIconTile(
    icon: Painter,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = modifier
            .size(IconSize.tile)
            .clip(RoundedCornerShape(CornerRadius.iconTile))
            .background(taminColors.glassIconTileBg)
            .border(1.dp, taminColors.glassIconTileBorder, RoundedCornerShape(CornerRadius.iconTile)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.TopCenter)
                .background(taminColors.glassIconTileShine)
        )
        Icon(
            painter = icon,
            contentDescription = null,
            tint = taminColors.glassIconTileIconTint,
            modifier = Modifier.size(IconSize.tileInner)
        )
    }
}

@Composable
private fun RippleRing(
    baseSize: Dp,
    borderColor: Color,
    maxScale: Float,
    durationMillis: Int,
    delayMillis: Int
) {
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(delayMillis)
        ),
        label = "rippleProgress"
    )

    Box(
        modifier = Modifier
            .size(baseSize)
            .graphicsLayer {
                val scale = lerp(1f, maxScale, progress)
                scaleX = scale
                scaleY = scale
                alpha = lerp(1f, 0f, progress)
            }
            .border(1.dp, borderColor, CircleShape)
    )
}

@Composable
fun AnimatedRingHeaderIcon(
    icon: Painter,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = modifier.size(IconSize.headerIconOuter),
        contentAlignment = Alignment.Center
    ) {
        RippleRing(
            baseSize = IconSize.headerIconOuter,
            borderColor = taminColors.glassIconRipple1,
            maxScale = 1.25f,
            durationMillis = 2200,
            delayMillis = 0
        )
        RippleRing(
            baseSize = IconSize.headerIconInner,
            borderColor = taminColors.glassIconRipple2,
            maxScale = 1.2f,
            durationMillis = 2200,
            delayMillis = 1650
        )
        GlassIconTile(icon = icon)
    }
}
