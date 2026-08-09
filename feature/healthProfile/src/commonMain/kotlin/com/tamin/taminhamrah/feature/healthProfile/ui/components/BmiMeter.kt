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
import com.tamin.taminhamrah.util.formatDecimal
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

/**
 * A beautiful, premium BMI Gauge Meter component.
 * Displays BMI score, category, gradient track indicator, and customized health advice.
 *
 * @param bmi The Body Mass Index value (typically 15.0 to 40.0, or null if physical stats are unselected).
 */
@Composable
fun BmiMeter(
    modifier: Modifier = Modifier,
    bmi: Float?
) {
    val taminColors = LocalTaminColors.current
    val layoutDirection = androidx.compose.ui.platform.LocalLayoutDirection.current
    val isRtl = layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl

    // Determine category characteristics
    val (categoryText, categoryBgColor, categoryTextColor, adviceMsg, dotColor) = when {
        bmi == null -> BmiInfo(
            category = stringResource(Res.string.health_bmi_unselected_category),
            bgColor = taminColors.divider,
            textColor = taminColors.textMuted,
            advice = stringResource(Res.string.health_bmi_empty_advice),
            dotColor = taminColors.textMuted
        )
        bmi < 18.5f -> BmiInfo(
            category = stringResource(Res.string.health_bmi_underweight_category),
            bgColor = taminColors.blueBg,
            textColor = taminColors.blueText,
            advice = stringResource(Res.string.health_bmi_underweight_advice),
            dotColor = taminColors.blueText
        )
        bmi in 18.5f..24.9f -> BmiInfo(
            category = stringResource(Res.string.health_bmi_normal_category),
            bgColor = taminColors.greenBg,
            textColor = taminColors.greenText,
            advice = stringResource(Res.string.health_bmi_normal_advice),
            dotColor = taminColors.greenText
        )
        bmi in 25.0f..29.9f -> BmiInfo(
            category = stringResource(Res.string.health_bmi_overweight_category),
            bgColor = taminColors.orangeBg,
            textColor = taminColors.orangeText,
            advice = stringResource(Res.string.health_bmi_overweight_advice),
            dotColor = taminColors.orangeText
        )
        else -> BmiInfo(
            category = stringResource(Res.string.health_bmi_obese_category),
            bgColor = taminColors.dangerBg,
            textColor = taminColors.dangerText,
            advice = stringResource(Res.string.health_bmi_obese_advice),
            dotColor = taminColors.dangerText
        )
    }

    // Gauge track layout calculations (Min 15.0, Max 40.0)
    val gaugeMin = 15.0f
    val gaugeMax = 40.0f
    val pointerTargetFraction = if (bmi != null) ((bmi - gaugeMin) / (gaugeMax - gaugeMin)).coerceIn(0f, 1f) else 0f

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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
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
                    text = stringResource(Res.string.health_bmi_title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
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
                    text = bmi?.toDouble()?.formatDecimal(1) ?: stringResource(Res.string.health_physical_unselected_value),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (bmi != null) taminColors.textPrimary else taminColors.textMuted,
                    lineHeight = 42.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .background(categoryBgColor, RoundedCornerShape(100.dp))
                        .border(1.dp, categoryTextColor.copy(alpha = 0.3f), RoundedCornerShape(100.dp))
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

            // Gauge labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TaminText(stringResource(Res.string.health_bmi_underweight_category), style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = taminColors.textSecondary))
                TaminText(stringResource(Res.string.health_bmi_normal_category), style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = taminColors.textSecondary))
                TaminText(stringResource(Res.string.health_bmi_overweight_category), style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = taminColors.textSecondary))
                TaminText(stringResource(Res.string.health_bmi_obese_category), style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = taminColors.textSecondary))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Gradient Gauge Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                ) {
                    val trackHeight = 14.dp.toPx()
                    val w = size.width

                    // Color transitions: Blue -> Green -> Orange -> Red
                    // In RTL, Start is Right, so Blue is at Right and Red is at Left.
                    val gradientColors = if (isRtl) {
                        listOf(
                            taminColors.dangerText,
                            taminColors.orangeText,
                            taminColors.greenText,
                            taminColors.blueText
                        )
                    } else {
                        listOf(
                            taminColors.blueText,
                            taminColors.greenText,
                            taminColors.orangeText,
                            taminColors.dangerText
                        )
                    }

                    val brush = Brush.horizontalGradient(
                        colors = gradientColors,
                        startX = 0f,
                        endX = w
                    )

                    drawRoundRect(
                        brush = brush,
                        topLeft = Offset(0f, (size.height - trackHeight) / 2),
                        size = Size(w, trackHeight),
                        cornerRadius = CornerRadius(100f, 100f)
                    )

                    // Draw Pointer Indicator overlay if BMI is present
                    if (bmi != null) {
                        val effectiveFraction = if (isRtl) 1f - animatedPointerFraction else animatedPointerFraction
                        val pointerX = w * effectiveFraction
                        val pointerRadius = 9.dp.toPx()

                        // Outer ring matching card background
                        drawCircle(
                            color = taminColors.bgSurface,
                            radius = pointerRadius,
                            center = Offset(pointerX, size.height / 2)
                        )
                        // Inner active category color ring
                        drawCircle(
                            color = dotColor,
                            radius = pointerRadius - 2.5.dp.toPx(),
                            center = Offset(pointerX, size.height / 2)
                        )
                    }
                }
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
                        color = taminColors.textSecondary,
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
            BmiMeter(bmi = null) // Unselected
            BmiMeter(bmi = 16.5f) // Underweight
            BmiMeter(bmi = 22.0f) // Normal
            BmiMeter(bmi = 27.5f) // Overweight
            BmiMeter(bmi = 33.2f) // Obese
        }
    }
}
