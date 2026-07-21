package com.tamin.taminhamrah.feature.healthProfile.ui.components

import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import kotlin.math.PI
import kotlin.math.sin

/**
 * A highly premium blood droplet graphic that displays selection progress,
 * EKG pulse line, and floating bubble animations.
 *
 * @param selectedLetter The selected blood group letter (A, B, AB, O).
 * @param selectedRh The selected Rh factor (+, -).
 * @param isUnknown True if the user selected "I don't know my blood group".
 */
@Composable
fun BloodDropletGraphic(
    modifier: Modifier = Modifier,
    selectedLetter: String?,
    selectedRh: String?,
    isUnknown: Boolean = false
) {
    val taminColors = LocalTaminColors.current
    
    // Determine the text display
    val displayText = when {
        isUnknown -> "?"
        selectedLetter != null && selectedRh != null -> "$selectedLetter$selectedRh"
        selectedLetter != null -> selectedLetter
        else -> "?"
    }

    // Determine the fill target percentage
    val fillTarget = when {
        isUnknown -> 0.75f
        selectedLetter != null && selectedRh != null -> 1.0f
        selectedLetter != null -> 0.5f
        else -> 0.0f
    }

    // Animate blood level fill
    val animatedFillPct by animateFloatAsState(
        targetValue = fillTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bloodFill"
    )

    // Glow transition animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )
    
    val glowOpacity by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowOpacity"
    )

    // Dotted ring rotation animation
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )

    // EKG dash offset animation
    val ekgOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ekgOffset"
    )

    // Heartbeat scale transition when selection is active
    val hasSelection = selectedLetter != null || isUnknown
    val heartbeatScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (hasSelection) 1.05f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1600
                1.0f at 0
                1.05f at 150
                0.98f at 300
                1.03f at 450
                1.0f at 600
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "heartbeatScale"
    )

    // Floating bubbles animation states
    val bubbleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleTime"
    )

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val scaleX = w / 100f
            val scaleY = h / 100f

            // 1. Draw Glow Background behind the droplet
            if (hasSelection) {
                val glowRadius = 40f * scaleX
                val colorGlow = if (isUnknown) taminColors.teal else taminColors.dangerText
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colorGlow.copy(alpha = glowOpacity * 0.35f),
                            Color.Transparent
                        ),
                        center = Offset(w / 2, h * 0.55f),
                        radius = glowRadius * glowScale
                    ),
                    center = Offset(w / 2, h * 0.55f),
                    radius = glowRadius * glowScale
                )
            }

            // 2. Draw Soft Dotted Rotating Ring
            rotate(ringRotation) {
                val ringColor = if (isUnknown) taminColors.teal.copy(alpha = 0.25f) else taminColors.dangerText.copy(alpha = 0.3f)
                drawCircle(
                    color = ringColor,
                    radius = 47f * scaleX,
                    center = Offset(w / 2, h / 2),
                    style = Stroke(
                        width = 1.5f * scaleX,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f * scaleX, 9f * scaleX), 0f)
                    )
                )
            }

            // 3. Scale Canvas to draw droplet
            scale(scaleX = heartbeatScale, scaleY = heartbeatScale, pivot = Offset(w / 2, h * 0.6f)) {
                // Construct Droplet SVG Path (Original viewport 100x100)
                val dropletPath = Path().apply {
                    moveTo(50f * scaleX, 6f * scaleY)
                    cubicTo(50f * scaleX, 6f * scaleY, 78f * scaleX, 42f * scaleY, 78f * scaleX, 63f * scaleY)
                    cubicTo(78f * scaleX, 81f * scaleY, 65.6f * scaleX, 94f * scaleY, 50f * scaleX, 94f * scaleY)
                    cubicTo(34.4f * scaleX, 94f * scaleY, 22f * scaleX, 81f * scaleY, 22f * scaleX, 63f * scaleY)
                    cubicTo(22f * scaleX, 42f * scaleY, 50f * scaleX, 6f * scaleY, 50f * scaleX, 6f * scaleY)
                    close()
                }

                // Base color of the empty heart/droplet container
                val baseDropletColor = taminColors.divider

                // Draw droplet background
                drawPath(
                    path = dropletPath,
                    color = baseDropletColor
                )

                // 4. Draw liquid fill using clipPath
                clipPath(dropletPath) {
                    val fillHeight = 94f * scaleY - (88f * scaleY * animatedFillPct)
                    val colorFillStart = if (isUnknown) taminColors.teal else taminColors.dangerText
                    val colorFillEnd = if (isUnknown) taminColors.teal.copy(alpha = 0.8f) else taminColors.dangerText.copy(alpha = 0.6f)

                    if (animatedFillPct > 0f) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(colorFillEnd, colorFillStart),
                                startY = fillHeight,
                                endY = 94f * scaleY
                            ),
                            topLeft = Offset(10f * scaleX, fillHeight),
                            size = Size(80f * scaleX, 94f * scaleY - fillHeight)
                        )

                        // Top fluid curve wave highlight
                        drawOval(
                            color = colorFillEnd,
                            topLeft = Offset(20f * scaleX, fillHeight - 3f * scaleY),
                            size = Size(60f * scaleX, 6f * scaleY)
                        )

                        // 5. Draw Animated Floating Bubbles inside the fluid
                        if (hasSelection) {
                            // Bubble 1
                            val b1Y = fillHeight + (94f * scaleY - fillHeight) * (0.6f + 0.3f * sin(bubbleTime))
                            val b1X = 50f * scaleX + 12f * scaleX * sin(bubbleTime * 1.5f)
                            drawCircle(
                                color = Color.White.copy(alpha = 0.45f),
                                radius = 2.5f * scaleX,
                                center = Offset(b1X, b1Y)
                            )

                            // Bubble 2
                            val b2Y = fillHeight + (94f * scaleY - fillHeight) * (0.3f + 0.3f * sin(bubbleTime + 1.5f))
                            val b2X = 50f * scaleX - 10f * scaleX * sin((bubbleTime + 1.5f) * 1.2f)
                            drawCircle(
                                color = Color.White.copy(alpha = 0.35f),
                                radius = 1.8f * scaleX,
                                center = Offset(b2X, b2Y)
                            )
                        }
                    }
                }

                // 6. Draw droplet outline border
                val strokeColor = if (isUnknown) taminColors.teal.copy(alpha = 0.6f) else if (hasSelection) taminColors.dangerText else taminColors.border
                drawPath(
                    path = dropletPath,
                    color = strokeColor,
                    style = Stroke(width = 3.2f * scaleX)
                )

                // 7. EKG Pulse Line Overlay
                if (hasSelection && !isUnknown) {
                    val ekgPath = Path().apply {
                        moveTo(22f * scaleX, 63f * scaleY)
                        lineTo(34f * scaleX, 63f * scaleY)
                        lineTo(40f * scaleX, 48f * scaleY)
                        lineTo(48f * scaleX, 78f * scaleY)
                        lineTo(54f * scaleX, 63f * scaleY)
                        lineTo(62f * scaleX, 63f * scaleY)
                        lineTo(68f * scaleX, 53f * scaleY)
                        lineTo(74f * scaleX, 63f * scaleY)
                        lineTo(78f * scaleX, 63f * scaleY)
                    }

                    // We clip the EKG to the droplet path to ensure it stays inside beautifully
                    clipPath(dropletPath) {
                        drawPath(
                            path = ekgPath,
                            color = Color.White.copy(alpha = 0.87f),
                            style = Stroke(
                                width = 2.4f * scaleX,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                                pathEffect = PathEffect.dashPathEffect(
                                    intervals = floatArrayOf(80f * scaleX, 400f * scaleX),
                                    phase = -ekgOffset * scaleX
                                )
                            )
                        )
                    }
                }
            }
        }

        // 8. Text Label Display
        val labelColor = when {
            isUnknown -> taminColors.textPrimary
            selectedLetter != null && selectedRh != null -> Color.White
            selectedLetter != null -> taminColors.textPrimary
            else -> taminColors.textMuted
        }
        
        TaminText(
            text = displayText,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = labelColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 18.dp)
        )
    }
}

@PreviewRtlTheme
@Composable
private fun BloodDropletGraphicPreview() {
    PreviewRtlThemeContent {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TaminText("No Selection", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                BloodDropletGraphic(
                    selectedLetter = null,
                    selectedRh = null
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TaminText("Letter Only (A)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                BloodDropletGraphic(
                    selectedLetter = "A",
                    selectedRh = null
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TaminText("Complete (O+)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                BloodDropletGraphic(
                    selectedLetter = "O",
                    selectedRh = "+"
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TaminText("Unknown (?)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                BloodDropletGraphic(
                    selectedLetter = null,
                    selectedRh = null,
                    isUnknown = true
                )
            }
        }
    }
}
