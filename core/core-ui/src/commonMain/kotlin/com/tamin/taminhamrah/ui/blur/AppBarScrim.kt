package com.tamin.taminhamrah.ui.blur

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shared scrim gradients that sit behind the app's floating bars (bottom navigation,
 * agent input bar, status-bar strip). Centralised here so every screen uses the exact
 * same wash — the "global" look the product wants — instead of re-declaring brushes.
 *
 * These are deliberately fixed black alphas (iOS-style dark scrim) regardless of theme,
 * so translucent bars keep enough contrast over any scrolling content beneath them.
 */
object AppBarScrim {
    /** Fades from transparent (top) into a dark wash (bottom) — sits under bottom bars. */
    val bottomGradient: Brush = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
    )

    /**
     * Heavier [bottomGradient] for when the OS shows 3-button navigation: darkens sooner and
     * deeper so the system buttons stay readable over scrolling content.
     */
    val bottomGradientStrong: Brush = Brush.verticalGradient(
        0f to Color.Transparent,
        0.4f to Color.Black.copy(alpha = 0.5f),
        1f to Color.Black.copy(alpha = 0.85f),
    )

    /** Fades from a dark wash (top) into transparent (bottom) — sits under the status bar. */
    val topGradient: Brush = Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)
    )
}

/** Convenience scrim placed behind a top bar / status-bar strip. */
@Composable
fun TopBarScrim(modifier: Modifier = Modifier, height: Dp = 60.dp) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(AppBarScrim.topGradient)
    )
}
