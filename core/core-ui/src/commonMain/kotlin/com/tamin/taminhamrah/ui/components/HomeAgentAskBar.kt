package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Width of the glowing ring. The neon sweep is drawn as a big rotating circle behind the pill and
 * then covered by the pill's own inner fill, so only this much of it stays visible as a border.
 */
private val NeonBorderWidth = 2.dp

/** Full sweep in this long; linear so the light travels the border at a constant speed. */
private const val NeonSweepDurationMs = 4200

/** The breathing outer glow. */
private const val GlowPulseDurationMs = 2200
private val GlowBlurMin = 6.dp
private val GlowBlurMax = 18.dp

/**
 * Neon stops for the moving border. Bespoke to this effect — there is no design-system brush that
 * carries a cyclic multi-stop sweep — first and last are equal so the rotation is seamless.
 */
private val NeonSweepColors = listOf(
    Color(0xFF33E1FF), // cyan
    Color(0xFF4C7DFF), // blue
    Color(0xFF9B6BFF), // violet
    Color(0xFF4C7DFF), // blue
    Color(0xFF33E1FF), // cyan
)

/** Opaque enough to hide the spinning sweep circle behind the pill body while reading dark-navy. */
private val AskBarInnerFill = Brush.verticalGradient(
    listOf(Color(0xF20B1B3F), Color(0xF2102A5C)),
)

/**
 * The "از یارا بپرسید…" entry point on the home header: a pill with a mic circle, a hint line and a
 * spark, wrapped in a slowly rotating neon-gradient border and a breathing glow.
 *
 * Display-only for now — the whole surface is a single [Role.Button] that runs [onClick]
 * (navigates to the assistant); there is no text field yet.
 */
@Composable
fun HomeAgentAskBar(
    hint: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "ask_bar")
    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(NeonSweepDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sweep_angle",
    )
    val glowBlur by transition.animateFloat(
        initialValue = GlowBlurMin.value,
        targetValue = GlowBlurMax.value,
        animationSpec = infiniteRepeatable(
            animation = tween(GlowPulseDurationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_blur",
    )

    val pillShape = RoundedCornerShape(CornerRadius.max)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonDimens.height)
            .coloredShadow(
                color = NeonSweepColors[1],
                borderRadius = CornerRadius.max,
                blurRadius = glowBlur.dp,
            )
            .clip(pillShape)
            // The moving border: a large sweep-gradient circle spun behind everything, then the
            // inner fill (below) covers all but the outermost NeonBorderWidth of it.
            .drawWithCache {
                onDrawBehind {
                    rotate(degrees = sweepAngle) {
                        drawCircle(
                            brush = Brush.sweepGradient(NeonSweepColors, center = center),
                            radius = size.maxDimension,
                        )
                    }
                }
            }
            .padding(NeonBorderWidth)
            .clip(pillShape)
            .background(AskBarInnerFill)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick,
            )
            .padding(horizontal = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {

        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = NeonSweepColors[0],
            modifier = Modifier.size(IconSize.medium).padding(start = 4.dp),
        )

        Text(
            text = hint,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.72f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(IconSize.textFieldIconContainer)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(NeonSweepColors[1], NeonSweepColors[2]))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.small),
            )
        }

    }
}

@PreviewRtlTheme
@Composable
private fun HomeAgentAskBarPreview() {
    PreviewRtlThemeContent {
        Box(modifier = Modifier.background(Color(0xFF12386E)).padding(Spacing.lg)) {
            HomeAgentAskBar(
                hint = "از یارا بپرسید...",
                contentDescription = "پرسش از دستیار هوشمند",
                onClick = {},
            )
        }
    }
}
