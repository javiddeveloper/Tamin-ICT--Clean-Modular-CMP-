package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Thickness

/**
 * A checkbox with no baked-in touch-target padding — unlike Material3's `Checkbox`, which always
 * reserves a 48dp interactive area around its ~20dp visual box, so the gap to an adjacent label
 * can't be tuned below that. Purely presentational (no click handling of its own); wrap the whole
 * row (checkbox + label) in `Modifier.clickable` for a touch target at least as generous as
 * Material3's default, and control the gap to the label with the row's own `Arrangement.spacedBy`.
 */
@Composable
fun TaminCheckbox1(
    checked: Boolean,
    modifier: Modifier = Modifier,
    checkedColor: Color = LocalTaminColors.current.blueText,
    uncheckedBorderColor: Color = LocalTaminColors.current.border,
    size: Dp = 20.dp,
) {
    val borderColor by animateColorAsState(if (checked) checkedColor else uncheckedBorderColor)
    val backgroundColor by animateColorAsState(if (checked) checkedColor else Color.Transparent)

    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor, RoundedCornerShape(CornerRadius.sm))
            .border(Thickness.medium, borderColor, RoundedCornerShape(CornerRadius.sm)),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * 0.7f),
            )
        }
    }
}
