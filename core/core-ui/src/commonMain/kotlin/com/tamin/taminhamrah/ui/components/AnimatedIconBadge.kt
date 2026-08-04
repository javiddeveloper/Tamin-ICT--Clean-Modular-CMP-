package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

@Composable
fun AnimatedIconBadge(
    icon: Painter,
    modifier: Modifier = Modifier,
    iconTint: Color = LocalTaminColors.current.blueText,
    backgroundColor: Color = LocalTaminColors.current.bgSurface,
    borderColor: Color = LocalTaminColors.current.border,
    size: Dp = IconSize.badge,
    iconSize: Dp = IconSize.badgeInner,
    shadowColor: Color = LocalTaminColors.current.shadowSubtle,
    elevation: Dp = Elevation.sm
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = Elevation.md.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = Duration.extraSlow, easing = Easing.standard),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = animatedOffsetY.dp.toPx()
            }
            .size(size)
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                spotColor = shadowColor,
                ambientColor = shadowColor
            )
            .background(backgroundColor, CircleShape)
            .border(BorderStroke(Elevation.xxs, borderColor), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}
