package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.blur.safeHazeEffect
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint

/**
 * The one glass look every floating surface on the assistant screen shares — the top bar,
 * the input bar, and the formula/table cards inside a reply — pulled from the Figma nodes
 * 90:87 / 90:120 ("Background+Border+Shadow+OverlayBlur"). The screen always draws on the
 * fixed-dark [AgentBackground], so these are literal colors rather than theme tokens: the
 * light/dark `TaminColors` would put dark text on a dark backdrop in light mode.
 */
internal object AgentGlass {
    /** Diagonal white sheen laid over the blurred/backdrop content. */
    val sheen: Brush = Brush.linearGradient(
        listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.05f))
    )
    val borderColor: Color = Color.White.copy(alpha = 0.18f)
    val borderWidth = 1.dp
    /** Nested tiles (icon buttons, table header, striped rows) on top of a glass card. */
    val tileFill: Color = Color.White.copy(alpha = 0.10f)
    val tileFillSubtle: Color = Color.White.copy(alpha = 0.06f)
    val shadowColor: Color = Color(0xFF040A1E)

    val textPrimary: Color = Color(0xFFE2ECFF)
    val textSecondary: Color = Color(0xFFA9BDE6)
    val iconTint: Color = Color(0xFFD5E1FA)
    /** Links, math and table headers — the dark theme's info blue, readable on the dark glass. */
    val accent: Color = Color(0xFF7FB0FF)

    /**
     * The blur itself. The tint is dark and opaque enough that scrolled-under text reads as a
     * soft glow, not as legible words — the old "you can read the chat through the bar" look.
     * [fallbackTint] covers Android < 12, where Haze cannot blur and only paints a scrim, and
     * [fallbackColor] the moments [safeHazeEffect] switches the blur off entirely.
     */
    private val frostColor: Color = Color(0xFF0E1B3F)
    val frostStyle: HazeStyle = HazeStyle(
        backgroundColor = frostColor,
        tint = HazeTint(color = frostColor.copy(alpha = 0.72f)),
        blurRadius = 32.dp,
        noiseFactor = 0.02f,
        fallbackTint = HazeTint(color = frostColor.copy(alpha = 0.94f)),
    )
    val frostFallbackColor: Color = frostColor.copy(alpha = 0.94f)
}

/**
 * Clip, sheen and hairline border of a glass card, for content that sits inside the chat's
 * haze *source* (a formula/table card in a reply) and therefore cannot blur what is beneath
 * it — there is nothing beneath it but the backdrop, so the sheen alone gives the same look
 * as the bars.
 */
internal fun Modifier.agentGlassCard(shape: Shape): Modifier =
    clip(shape)
        .background(AgentGlass.sheen)
        .border(AgentGlass.borderWidth, AgentGlass.borderColor, shape)

/**
 * A glass card that floats *over* the haze source (the top/input bars): frosted blur of
 * whatever scrolls beneath, then the same sheen and border as [agentGlassCard].
 */
internal fun Modifier.agentFrostedGlassCard(shape: Shape, hazeState: HazeState): Modifier =
    clip(shape)
        .safeHazeEffect(
            state = hazeState,
            style = AgentGlass.frostStyle,
            fallbackColor = AgentGlass.frostFallbackColor,
        )
        .background(AgentGlass.sheen)
        .border(AgentGlass.borderWidth, AgentGlass.borderColor, shape)
