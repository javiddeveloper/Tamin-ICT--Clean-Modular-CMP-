package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing


enum class LoadingButtonIconPosition {
    LEADING,
    TRAILING
}

@Composable
fun LoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    /** Defaults to every existing caller's expectation: icon before text. */
    iconPosition: LoadingButtonIconPosition = LoadingButtonIconPosition.LEADING,
    /** Shorter than the page-level default for a button that sits inside a form footer. */
    height: Dp = ButtonDimens.height,
    shape: Shape = RoundedCornerShape(CornerRadius.xl),
) {
    val taminColors = LocalTaminColors.current
    val backgroundBrush = if (enabled) {
        taminColors.buttonGradient
    } else {
        taminColors.buttonDisabledGradient
    }
    val contentColor = if (enabled) Color.White else Color.White.copy(alpha = 0.6f)
    val shadowColor = if (enabled) taminColors.shadowPrimary else Color.Transparent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (enabled) Elevation.button else Elevation.none,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .height(height)
            .clip(shape)
            .background(backgroundBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally)
        ) {
            val label = @Composable {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
            }
            val indicator = @Composable {
                Box(modifier = Modifier.size(IconSize.medium), contentAlignment = Alignment.Center) {
                    Crossfade(targetState = isLoading, animationSpec = tween(300)) { loading ->
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
                                color = contentColor,
                                strokeWidth = ButtonDimens.loadingIndicatorStroke
                            )
                        } else if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(IconSize.medium)
                            )
                        }
                    }
                }
            }
            // Reserving the indicator's slot when there's nothing to show in it (no icon, not
            // loading) pushes the label off-center — the whole point of centering the button's
            // text. Only give it space once there's actually an icon or spinner to draw.
            val showIndicator = isLoading || icon != null
            if (iconPosition == LoadingButtonIconPosition.LEADING) {
                if (showIndicator) indicator()
                label()
            } else {
                label()
                if (showIndicator) indicator()
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun LoadingButtonPreview() {
    PreviewRtlThemeContent {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            LoadingButton(
                text = "دریافت کد یکبار مصرف",
                onClick = {},
                isLoading = false
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun LoadingButtonLoadingPreview() {
    PreviewRtlThemeContent {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            LoadingButton(
                text = "دریافت کد یکبار مصرف",
                onClick = {},
                isLoading = true
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun LoadingButtonDisabledPreview() {
    PreviewRtlThemeContent {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            LoadingButton(
                text = "دریافت کد یکبار مصرف",
                onClick = {},
                enabled = false
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun LoadingButtonDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            LoadingButton(
                text = "دریافت کد یکبار مصرف",
                onClick = {},
                isLoading = false
            )
        }
    }
}
