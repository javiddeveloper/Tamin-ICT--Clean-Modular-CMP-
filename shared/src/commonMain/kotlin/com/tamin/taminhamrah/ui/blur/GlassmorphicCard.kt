package com.tamin.taminhamrah.ui.blur

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.hazeEffect


@Composable
fun FloatingGlassNavigationBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val glassShape = RoundedCornerShape(32.dp) // Deeply rounded pill shape
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .shadow(
                elevation = 8.dp,
                shape = glassShape,
                spotColor = Color.Black.copy(alpha = 0.05f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            )
            // 3. Apply the True Blur Effect
            .hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    noiseFactor = 0.1f,
                    tint = HazeTint(color = Color.Transparent ,  blendMode = BlendMode.Luminosity), // The "frost" color
                    blurRadius = 24.dp // How heavy the blur is
                )
            )
            .clip(glassShape)
            // The Glass Edge (Optional, but helps define the shape)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.5f),
                shape = glassShape
            )
    ) {
        // We use standard M3 NavigationBar but strip its background
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0), // Remove bottom padding to keep it tight
            content = content
        )
    }
}
