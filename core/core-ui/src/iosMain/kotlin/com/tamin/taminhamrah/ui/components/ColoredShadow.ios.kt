package com.tamin.taminhamrah.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import org.jetbrains.skia.ImageFilter

actual fun Modifier.coloredShadow(
    color: Color,
    borderRadius: Dp,
    blurRadius: Dp,
    offsetY: Dp,
    offsetX: Dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = Color.Transparent
            val frameworkPaint = this.asFrameworkPaint()
            val sigma = if (blurRadius.toPx() > 0) blurRadius.toPx() / 2f else 0f
            frameworkPaint.imageFilter = ImageFilter.makeDropShadow(
                dx = offsetX.toPx(),
                dy = offsetY.toPx(),
                sigmaX = sigma,
                sigmaY = sigma,
                color = color.toArgb()
            )
        }
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint = paint,
        )
    }
}
