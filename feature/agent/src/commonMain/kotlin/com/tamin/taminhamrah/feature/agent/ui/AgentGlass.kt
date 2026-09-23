package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.blur.safeHazeEffect
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
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
        listOf(AgentColors.ink.copy(alpha = 0.16f), AgentColors.ink.copy(alpha = 0.05f))
    )
    val borderColor: Color = AgentColors.ink.copy(alpha = 0.18f)
    val borderWidth = 1.dp
    /** Nested tiles (icon buttons, table header, striped rows) on top of a glass card. */
    val tileFill: Color = AgentColors.ink.copy(alpha = 0.10f)
    val tileFillSubtle: Color = AgentColors.ink.copy(alpha = 0.06f)
    val shadowColor: Color = AgentPalette.shadow

    val textPrimary: Color = AgentPalette.contentHigh
    val textSecondary: Color = AgentPalette.contentMedium
    val iconTint: Color = AgentPalette.contentIcon
    /** Links and table headers — the dark theme's info blue, readable on the dark glass. */
    val accent: Color = AgentPalette.accentBlue
    /** Math formulas — kept distinct from [accent] so equations don't read as links. */
    val formula: Color = AgentPalette.accentTeal
    /** Chart slices and series beside [accent], in the same lightness so none recedes. */
    val chartPalette: List<Color> = listOf(
        accent,
        AgentPalette.accentTeal,
        AgentPalette.chartAmber,
        AgentPalette.chartRose,
        AgentPalette.chartLilac,
        AgentPalette.chartSky,
    )

    /**
     * Errors. The theme's error red is tuned for a light surface and reads as a dark smear
     * on the backdrop; this is the same hue lifted to the glass palette's lightness, with a
     * tinted tile so the note is a card like everything else in a reply.
     */
    val danger: Color = AgentPalette.danger
    val dangerText: Color = AgentPalette.dangerSoft
    val dangerFill: Color = AgentPalette.dangerStrong.copy(alpha = 0.12f)
    val dangerBorder: Color = AgentPalette.danger.copy(alpha = 0.35f)

    /**
     * The blur itself. The tint is dark and opaque enough that scrolled-under text reads as a
     * soft glow, not as legible words — the old "you can read the chat through the bar" look.
     * [fallbackTint] covers Android < 12, where Haze cannot blur and only paints a scrim, and
     * [fallbackColor] the moments [safeHazeEffect] switches the blur off entirely.
     */
    private val frostColor: Color = AgentPalette.frost
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
@OptIn(ExperimentalHazeApi::class)
internal fun Modifier.agentFrostedGlassCard(
    shape: Shape,
    hazeState: HazeState,
    /** The hairline; the recorder bar swaps in [AgentGlass.danger] while the mic is live. */
    borderColor: Color = AgentGlass.borderColor,
    borderWidth: Dp = AgentGlass.borderWidth,
): Modifier =
    clip(shape)
        .safeHazeEffect(
            state = hazeState,
            style = AgentGlass.frostStyle,
            fallbackColor = AgentGlass.frostFallbackColor,
            // The frost tint sits at 72% opacity on top of the blur, so what is underneath
            // already reads as a glow rather than as detail — computing that blur at full
            // screen resolution buys nothing visible. `Auto` resolves to a 0.33 scale factor
            // at this blur radius, i.e. roughly a ninth of the pixels. It matters because
            // every frosted surface here (top bar, composer, history drawer) floats over the
            // chat list, so the blur is recomputed on every scrolled frame.
            inputScale = HazeInputScale.Auto,
        )
        .background(AgentGlass.sheen)
        .border(borderWidth, borderColor, shape)
