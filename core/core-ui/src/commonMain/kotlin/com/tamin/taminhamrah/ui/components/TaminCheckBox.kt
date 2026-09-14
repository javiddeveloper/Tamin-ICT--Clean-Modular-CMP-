package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

@Immutable
data class TaminCheckBoxColors(
    val checkedBackgroundColor: Color,
    val uncheckedBackgroundColor: Color,
    val checkedBorderColor: Color,
    val uncheckedBorderColor: Color,
    val checkmarkColor: Color,
    val disabledCheckedBackgroundColor: Color,
    val disabledUncheckedBackgroundColor: Color,
    val disabledCheckedBorderColor: Color,
    val disabledUncheckedBorderColor: Color,
    val disabledCheckmarkColor: Color,
)

object TaminCheckBoxDefaults {
    @Composable
    fun colors(
        checkedBackgroundColor: Color = LocalTaminColors.current.blueText,
        uncheckedBackgroundColor: Color = LocalTaminColors.current.bgSurface,
        checkedBorderColor: Color = LocalTaminColors.current.blueText,
        uncheckedBorderColor: Color = LocalTaminColors.current.border,
        checkmarkColor: Color = LocalTaminColors.current.bgSurface,
        disabledCheckedBackgroundColor: Color =
            LocalTaminColors.current.blueText.copy(alpha = LocalTaminColors.current.disabledAlpha),
        disabledUncheckedBackgroundColor: Color =
            LocalTaminColors.current.bgSurface.copy(alpha = LocalTaminColors.current.disabledAlpha),
        disabledCheckedBorderColor: Color =
            LocalTaminColors.current.blueText.copy(alpha = LocalTaminColors.current.disabledAlpha),
        disabledUncheckedBorderColor: Color =
            LocalTaminColors.current.border.copy(alpha = LocalTaminColors.current.disabledAlpha),
        disabledCheckmarkColor: Color =
            LocalTaminColors.current.bgSurface.copy(alpha = LocalTaminColors.current.disabledAlpha),
    ): TaminCheckBoxColors = TaminCheckBoxColors(
        checkedBackgroundColor = checkedBackgroundColor,
        uncheckedBackgroundColor = uncheckedBackgroundColor,
        checkedBorderColor = checkedBorderColor,
        uncheckedBorderColor = uncheckedBorderColor,
        checkmarkColor = checkmarkColor,
        disabledCheckedBackgroundColor = disabledCheckedBackgroundColor,
        disabledUncheckedBackgroundColor = disabledUncheckedBackgroundColor,
        disabledCheckedBorderColor = disabledCheckedBorderColor,
        disabledUncheckedBorderColor = disabledUncheckedBorderColor,
        disabledCheckmarkColor = disabledCheckmarkColor,
    )
}

/**
 * Tamin checkbox with an [CornerRadius.md] (8dp) rounded border — Material's stock checkbox
 * cannot customize that radius.
 */
@Composable
fun TaminCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TaminCheckBoxColors = TaminCheckBoxDefaults.colors(),
) {
    val shape = RoundedCornerShape(CornerRadius.md)
    val backgroundColor by animateColorAsState(
        targetValue = when {
            checked && enabled -> colors.checkedBackgroundColor
            checked && !enabled -> colors.disabledCheckedBackgroundColor
            !checked && enabled -> colors.uncheckedBackgroundColor
            else -> colors.disabledUncheckedBackgroundColor
        },
        label = "TaminCheckBoxBackground",
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            checked && enabled -> colors.checkedBorderColor
            checked && !enabled -> colors.disabledCheckedBorderColor
            !checked && enabled -> colors.uncheckedBorderColor
            else -> colors.disabledUncheckedBorderColor
        },
        label = "TaminCheckBoxBorder",
    )
    val checkColor by animateColorAsState(
        targetValue = if (enabled) colors.checkmarkColor else colors.disabledCheckmarkColor,
        label = "TaminCheckBoxCheckmark",
    )

    Box(
        modifier = modifier
            .size(IconSize.checkbox)
            .clip(shape)
            .background(backgroundColor, shape)
            .border(Thickness.medium, borderColor, shape)
            .then(
                if (onCheckedChange != null) {
                    Modifier.toggleable(
                        value = checked,
                        enabled = enabled,
                        role = Role.Checkbox,
                        onValueChange = onCheckedChange,
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = checkColor,
                modifier = Modifier.size(IconSize.checkboxCheck),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun TaminCheckBoxPreview() {
    PreviewRtlThemeContent {
        var checked by remember { mutableStateOf(true) }
        var unchecked by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TaminCheckBox(checked = checked, onCheckedChange = { checked = it })
            TaminCheckBox(checked = unchecked, onCheckedChange = { unchecked = it })
            TaminCheckBox(checked = true, onCheckedChange = null, enabled = false)
            TaminCheckBox(checked = false, onCheckedChange = null, enabled = false)
        }
    }
}
