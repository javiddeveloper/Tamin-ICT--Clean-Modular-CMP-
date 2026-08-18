package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * The frame every screen the workshop list launches into shares: the gradient bar, its back
 * control, and the workshop it is about named underneath.
 *
 * The subtitles are what the old app pushed through toolbar bundle keys from screen to screen.
 * Here they are plain parameters, so a destination cannot be opened without them.
 */
@Composable
fun WorkshopScreenShell(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    secondarySubtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    // The same navy the list header wears, so a workshop service does not change color when it
    // is opened from one.
    val headerGradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }
    Column(modifier = modifier.fillMaxSize()) {
        TaminTopAppBar(
            title = title,
            background = headerGradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    onClick = onBack,
                )
            },
            action = action,
        ) {
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textHeaderSubtitle,
                )
            }
            secondarySubtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textHeaderSubtitle,
                )
            }
        }
        content()
    }
}
