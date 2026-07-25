package com.tamin.taminhamrah.feature.healthProfile.ui.components

import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * A beautiful, premium BMI Gauge Meter component.
 * Displays BMI score, category, gradient track indicator, and customized health advice.
 *
 * @param bmi The Body Mass Index value (typically 15.0 to 40.0).
 */
@Composable
fun BmiMeter(
    modifier: Modifier = Modifier,
    bmi: Float
) {
    val taminColors = LocalTaminColors.current

    // Determine category characteristics
    val (categoryText, categoryBgColor, categoryTextColor, adviceMsg, dotColor) = when {
        bmi < 18.5f -> BmiInfo(
            category = "کمبود وزن",
            bgColor = taminColors.blueBg,
            textColor = taminColors.blueText,
            advice = "شما کمبود وزن دارید. تغذیه مناسب و متعادل توصیه می‌شود.",
            dotColor = taminColors.blueText
        )
        bmi in 18.5f..24.9f -> BmiInfo(
            category = "وزن طبیعی",
            bgColor = taminColors.greenBg,
            textColor = taminColors.greenText,
            advice = "وزن شما طبیعی است. برای حفظ شیوه زندگی فعال و وزن مناسب تلاش کنید.",
            dotColor = taminColors.greenText
        )
        bmi in 25.0f..29.9f -> BmiInfo(
            category = "اضافه وزن",
            bgColor = taminColors.orangeBg,
            textColor = taminColors.orangeText,
            advice = "شما دچار اضافه وزن خفیف هستید. افزایش فعالیت بدنی و کنترل کالری توصیه می‌شود.",
            dotColor = taminColors.orangeText
        )
        else -> BmiInfo(
            category = "چاقی شدید",
            bgColor = taminColors.dangerBorder.copy(alpha = 0.25f),
            textColor = taminColors.dangerText,
            advice = "شاخص توده بدنی نشان‌دهنده چاقی است. مشاوره با متخصص تغذیه پیشنهاد می‌شود.",
            dotColor = taminColors.dangerText
        )
    }

    // Gauge track layout calculations (Min 15.0, Max 40.0)
    val gaugeMin = 15.0f
    val gaugeMax = 40.0f
    val pointerTargetFraction = ((bmi - gaugeMin) / (gaugeMax - gaugeMin)).coerceIn(0f, 1f)

    // Smoothly animate pointer movement on gauge track
    val animatedPointerFraction by animateFloatAsState(
        targetValue = pointerTargetFraction,
        animationSpec = tween(durationMillis = 1000),
        label = "pointerMove"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = BorderStroke(1.dp, taminColors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Title with Pulse/EKG Line Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Heartbeat / EKG pulse line icon
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(taminColors.blueBg, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val w = size.width
                        val h = size.height
                        val path = Path().apply {
                            moveTo(0f, h * 0.5f)
                            lineTo(w * 0.25f, h * 0.5f)
                            lineTo(w * 0.35f, h * 0.15f)
                            lineTo(w * 0.48f, h * 0.85f)
                            lineTo(w * 0.58f, h * 0.5f)
                            lineTo(w * 0.70f, h * 0.5f)
                            lineTo(w * 0.80f, h * 0.3f)
                            lineTo(w * 0.88f, h * 0.5f)
                            lineTo(w, h * 0.5f)
                        }
                        drawPath(
                            path = path,
                            color = taminColors.blueText,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                TaminText(
                    text = "شاخص تودهٔ بدنی (BMI)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textPrimary
                    )
                )
            }

            // Big BMI score display & Category badge
            Column(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TaminText(
                    text = String.format("%.1f", bmi),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary,
                    lineHeight = 42.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .background(categoryBgColor, RoundedCornerShape(100.dp))
                        .border(1.5.dp, categoryTextColor.copy(alpha = 0.3f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    TaminText(
                        text = categoryText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryTextColor
                        )
                    )
                }
            }

            // Gradient Gauge Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                ) {
                    val trackHeight = 10f
                    val w = size.width

                    // Draw rounded color track representing underweight -> obese
                    // Color transitions: Blue -> Green -> Orange -> Red
                    val brush = Brush.horizontalGradient(
                        colors = listOf(
                            taminColors.blueText, // Underweight
                            taminColors.greenText, // Normal
                            taminColors.orangeText, // Overweight
                            taminColors.dangerText  // Obese
                        ),
                        startX = 0f,
                        endX = w
                    )

                    drawRoundRect(
                        brush = brush,
                        topLeft = Offset(0f, (size.height - trackHeight) / 2),
                        size = Size(w, trackHeight),
                        cornerRadius = CornerRadius(100f, 100f)
                    )

                    // Draw Pointer Indicator overlay
                    val pointerX = w * animatedPointerFraction
                    val pointerRadius = 7f

                    // Outer white ring
                    drawCircle(
                        color = Color.White,
                        radius = pointerRadius + 3f,
                        center = Offset(pointerX, size.height / 2)
                    )
                    // Inner colored center
                    drawCircle(
                        color = dotColor,
                        radius = pointerRadius,
                        center = Offset(pointerX, size.height / 2)
                    )
                }
            }

            // Gauge labels (in English/Standard layout)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TaminText("کمبود", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, color = taminColors.textMuted))
                TaminText("طبیعی", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, color = taminColors.textMuted))
                TaminText("اضافه", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, color = taminColors.textMuted))
                TaminText("چاقی", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, color = taminColors.textMuted))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Advice banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(taminColors.divider, RoundedCornerShape(13.dp))
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Colored dot showing active status
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(dotColor, RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(10.dp))
                TaminText(
                    text = adviceMsg,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textTertiary,
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Data holder for BMI styling
private data class BmiInfo(
    val category: String,
    val bgColor: Color,
    val textColor: Color,
    val advice: String,
    val dotColor: Color
)

@PreviewRtlTheme
@Composable
private fun BmiMeterPreview() {
    PreviewRtlThemeContent {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BmiMeter(bmi = 16.5f) // Underweight
            BmiMeter(bmi = 22.0f) // Normal
            BmiMeter(bmi = 27.5f) // Overweight
            BmiMeter(bmi = 33.2f) // Obese
        }
    }
}
