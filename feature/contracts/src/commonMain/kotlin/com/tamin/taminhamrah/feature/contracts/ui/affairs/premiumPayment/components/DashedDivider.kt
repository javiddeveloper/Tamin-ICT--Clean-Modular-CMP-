package com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = LocalTaminColors.current.border,
    thickness: Dp = 1.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 4.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = thickness.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashLength.toPx(), gapLength.toPx()),
                0f
            )
        )
    }
}
