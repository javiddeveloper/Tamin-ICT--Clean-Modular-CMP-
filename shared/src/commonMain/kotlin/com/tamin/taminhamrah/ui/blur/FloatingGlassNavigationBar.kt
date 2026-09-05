package com.tamin.taminhamrah.ui.blur

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.contract.CustomNavigationBar
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint


@Composable
fun FloatingGlassNavigationBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    itemCount: Int = 4,
    isBlurEnabled: Boolean = true,
    /**
     * Optional button rendered as a sibling of the glass pill rather than inside it, so the pill's
     * own tab layout and highlight maths are unaffected by its presence. When absent the pill
     * simply takes the full width back.
     */
    trailingButton: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val glassShape = RoundedCornerShape(24.dp) // Deeply rounded pill shape
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomPadding = if (bottomInset > 0.dp) bottomInset + 12.dp else 24.dp

    val layoutDirection = LocalLayoutDirection.current
    val effectiveIndex = if (layoutDirection == LayoutDirection.Rtl) {
        (itemCount - 1 - selectedIndex).coerceIn(0, (itemCount - 1).coerceAtLeast(0))
    } else {
        selectedIndex.coerceIn(0, (itemCount - 1).coerceAtLeast(0))
    }

    val targetFraction = if (itemCount > 0) (effectiveIndex + 0.5f) / itemCount else 0.5f
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "BorderHighlightPosition"
    )

    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) {
        Color.DarkGray.copy(alpha = 0.4f)
    } else {
        Color.Gray.copy(alpha = 0.3f)
    }
    val primaryColor = if (isDark) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    }

    Row(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = bottomPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
    Box(
        modifier = Modifier
            .weight(1f)
            .shadow(
                elevation = 8.dp,
                shape = glassShape,
                spotColor = Color.Black.copy(alpha = 0.05f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ).background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    )
                )
            )
            // 3. Apply the True Blur Effect conditionally
            .safeHazeEffect(
                state = hazeState,
                style = HazeStyle(
                    noiseFactor = 0.02f,
                    tint = HazeTint(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        blendMode = BlendMode.Luminosity
                    ),
                    blurRadius = 24.dp // How heavy the blur is
                ),
                fallbackColor = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                isEnabled = isBlurEnabled
            )
            .clip(glassShape)
            // Deferred draw-phase border drawing to avoid invalidating composition and haze blur during animation frames
            .drawWithContent {
                drawContent()

                val fraction = animatedFraction
                val spread = 0.15f
                val startPos = (fraction - spread).coerceIn(0f, 1f)
                val peakPos = fraction.coerceIn(0f, 1f)
                val endPos = (fraction + spread).coerceIn(0f, 1f)

                val colorStops = buildList {
                    if (startPos > 0f) {
                        add(0f to baseColor)
                        add(startPos to baseColor)
                    } else {
                        add(0f to primaryColor)
                    }
                    add(peakPos to primaryColor)
                    if (endPos < 1f) {
                        add(endPos to baseColor)
                        add(1f to baseColor)
                    } else {
                        add(1f to primaryColor)
                    }
                }.toTypedArray()

                val strokeWidthPx = 1.5.dp.toPx()
                val halfStroke = strokeWidthPx / 2f
                val cornerRadiusPx = 24.dp.toPx()

                drawRoundRect(
                    brush = Brush.horizontalGradient(colorStops = colorStops),
                    topLeft = Offset(halfStroke, halfStroke),
                    size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
                    cornerRadius = CornerRadius(cornerRadiusPx - halfStroke),
                    style = Stroke(width = strokeWidthPx)
                )
            }
    ) {
        // We use standard M3 NavigationBar but strip its background
        CustomNavigationBar(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 4.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0), // Remove bottom padding to keep it tight
            content = content
        )
    }

        if (trailingButton != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailingButton()
        }
    }
}
