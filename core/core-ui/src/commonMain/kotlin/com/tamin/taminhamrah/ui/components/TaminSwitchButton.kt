package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_moon
import taminx.core.core_ui.ic_sun


private val SwitchTrackWidth = 46.dp
private val SwitchTrackHeight = 26.dp
private val SwitchThumbSize = 20.dp
private val SwitchThumbPadding = 3.dp
private val SwitchThumbIconSize = 13.dp
private const val ThemeIconAnimationDurationMillis = 250
private val SunIconTint = Color(0xFF1F4FA3)

@Immutable
data class TaminSwitchColors(
    val checkedTrackColor: Color,
    val uncheckedTrackColor: Color,
    val checkedThumbColor: Color,
    val uncheckedThumbColor: Color,
    val disabledCheckedTrackColor: Color,
    val disabledUncheckedTrackColor: Color,
    val disabledCheckedThumbColor: Color,
    val disabledUncheckedThumbColor: Color,
)

object TaminSwitchDefaults {
    @Composable
    fun colors(
        checkedTrackColor: Color = run {
            val colors = LocalTaminColors.current
            if (colors == DarkTaminColors) Color(0xFF1F4FA3) else colors.blueText
        },
        uncheckedTrackColor: Color = run {
            val colors = LocalTaminColors.current
            if (colors == DarkTaminColors) colors.outerBorder else colors.grey900
        },
        checkedThumbColor: Color = run {
            val colors = LocalTaminColors.current
            if (colors == DarkTaminColors) colors.textPrimary else Color.White
        },
        uncheckedThumbColor: Color = run {
            val colors = LocalTaminColors.current
            if (colors == DarkTaminColors) colors.textSecondary else Color.White
        },
        disabledCheckedTrackColor: Color = checkedTrackColor.copy(alpha = LocalTaminColors.current.disabledAlpha),
        disabledUncheckedTrackColor: Color = uncheckedTrackColor.copy(alpha = LocalTaminColors.current.disabledAlpha),
        disabledCheckedThumbColor: Color = checkedThumbColor,
        disabledUncheckedThumbColor: Color = uncheckedThumbColor,
    ): TaminSwitchColors = TaminSwitchColors(
        checkedTrackColor = checkedTrackColor,
        uncheckedTrackColor = uncheckedTrackColor,
        checkedThumbColor = checkedThumbColor,
        uncheckedThumbColor = uncheckedThumbColor,
        disabledCheckedTrackColor = disabledCheckedTrackColor,
        disabledUncheckedTrackColor = disabledUncheckedTrackColor,
        disabledCheckedThumbColor = disabledCheckedThumbColor,
        disabledUncheckedThumbColor = disabledUncheckedThumbColor,
    )
}


@Composable
fun TaminSwitchButton(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TaminSwitchColors = TaminSwitchDefaults.colors(),
    showThemeIcon: Boolean = false,
) {
    val trackColor by animateColorAsState(
        targetValue = when {
            checked && enabled -> colors.checkedTrackColor
            checked && !enabled -> colors.disabledCheckedTrackColor
            !checked && enabled -> colors.uncheckedTrackColor
            else -> colors.disabledUncheckedTrackColor
        },
        label = "TaminSwitchTrackColor",
    )
    val thumbColor by animateColorAsState(
        targetValue = when {
            checked && enabled -> colors.checkedThumbColor
            checked && !enabled -> colors.disabledCheckedThumbColor
            !checked && enabled -> colors.uncheckedThumbColor
            else -> colors.disabledUncheckedThumbColor
        },
        label = "TaminSwitchThumbColor",
    )
    val thumbOffset: Dp by animateDpAsState(
        targetValue = if (checked) SwitchTrackWidth - SwitchThumbSize - SwitchThumbPadding else SwitchThumbPadding,
        label = "TaminSwitchThumbOffset",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(width = SwitchTrackWidth, height = SwitchTrackHeight)
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && onCheckedChange != null,
                role = Role.Switch,
                onValueChange = { onCheckedChange?.invoke(it) },
            )
            .background(trackColor, CircleShape),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .size(SwitchThumbSize)
                .shadow(elevation = Elevation.xxs, shape = CircleShape, clip = false)
                .background(thumbColor, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (showThemeIcon) {
                SwitchThumbThemeIcon(checked = checked, tint = trackColor)
            }
        }
    }
}

@Composable
private fun SwitchThumbThemeIcon(
    checked: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val animationSpec = remember {
        tween<Float>(durationMillis = ThemeIconAnimationDurationMillis, easing = FastOutSlowInEasing)
    }
    val moonAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = animationSpec,
        label = "ThemeIconMoonAlpha",
    )
    val moonRotation by animateFloatAsState(
        targetValue = if (checked) 0f else 90f,
        animationSpec = animationSpec,
        label = "ThemeIconMoonRotation",
    )
    val sunAlpha by animateFloatAsState(
        targetValue = if (checked) 0f else 1f,
        animationSpec = animationSpec,
        label = "ThemeIconSunAlpha",
    )
    val sunRotation by animateFloatAsState(
        targetValue = if (checked) -90f else 0f,
        animationSpec = animationSpec,
        label = "ThemeIconSunRotation",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_moon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(SwitchThumbIconSize)
                .graphicsLayer {
                    alpha = moonAlpha
                    rotationZ = moonRotation
                },
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_sun),
            contentDescription = null,
            tint = SunIconTint,
            modifier = Modifier
                .size(SwitchThumbIconSize)
                .graphicsLayer {
                    alpha = sunAlpha
                    rotationZ = sunRotation
                },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminSwitchPreviewLight() {
    PreviewRtlThemeContent {
        TaminSwitchPreviewContent()
    }
}

@PreviewRtlTheme
@Composable
private fun TaminSwitchPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        TaminSwitchPreviewContent()
    }
}

@Composable
private fun TaminSwitchPreviewContent() {
    var checkedOn by remember { mutableStateOf(true) }
    var checkedOff by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(LocalTaminColors.current.bgPage)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        TaminSwitchButton(checked = checkedOn, onCheckedChange = { checkedOn = it })
        TaminSwitchButton(checked = checkedOff, onCheckedChange = { checkedOff = it })
        TaminSwitchButton(checked = true, onCheckedChange = null, enabled = false)
        TaminSwitchButton(checked = false, onCheckedChange = null, enabled = false)
    }
}
