package com.tamin.taminhamrah.ui.blur

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.ui.contract.CustomNavigationBar
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint


@Composable
fun FloatingGlassNavigationBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    isBlurEnabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val glassShape = RoundedCornerShape(24.dp) // Deeply rounded pill shape
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomPadding = if (bottomInset > 0.dp) bottomInset + 12.dp else 24.dp

    Box(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = bottomPadding)
            .shadow(
                elevation = 8.dp,
                shape = glassShape,
                spotColor = Color.Black.copy(alpha = 0.05f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ).background(brush = Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.background.copy(alpha = 0.1f) , MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))))
            // 3. Apply the True Blur Effect conditionally
            .safeHazeEffect(
                state = hazeState,
                style = HazeStyle(
                    noiseFactor = 0.02f,
                    tint =  HazeTint(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        blendMode = BlendMode.Luminosity
                    ),
                    blurRadius = 24.dp // How heavy the blur is
                ),
                fallbackColor = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                isEnabled = isBlurEnabled
            )
            .clip(glassShape)
            // The Glass Edge (Optional, but helps define the shape)
            .border(
                width = 1.dp,
                color = if (isSystemInDarkTheme()) Color.Gray else Color.White,
                shape = glassShape
            )
    ) {
        // We use standard M3 NavigationBar but strip its background
        CustomNavigationBar(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp , horizontal = 4.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0), // Remove bottom padding to keep it tight
            content = content
        )
    }
}
