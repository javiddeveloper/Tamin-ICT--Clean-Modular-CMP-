package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ─── The agent feature's colour vocabulary ────────────────────────────────────
//
// Two layers, and only two:
//
//   [AgentPalette]  the raw hues — THE ONLY PLACE A HEX LITERAL MAY APPEAR in this
//                   feature. Re-skinning the assistant is an edit to this object.
//   [AgentColors]   what each hue is *for* — backdrop, orb, bubble, status, chart.
//                   Every value here is a palette entry plus an alpha or a gradient,
//                   never a fresh literal.
//
// [AgentGlass] is the third piece, in its own file: the roles specific to the frosted
// surfaces (sheen, border, tiles, text on glass). It draws from this palette too.
//
// ── Why this feature does not read the app theme ──
//
// The assistant always draws on [AgentBackground], a fixed dark navy/violet composition.
// It is dark in light mode and dark in dark mode — the backdrop is the product, not a
// surface that follows the user's preference. So every colour here is absolute.
//
// Reading `LocalTaminColors` or `MaterialTheme.colorScheme` inside this feature is a bug,
// even when it happens to look right today:
//
//  * `profileGradientStops` is navy (#173D7E → #1F4FA3) in light and teal (#10AEB9 →
//    #1E6FD0) in dark. The user's chat bubble was built on it, so the bubble changed
//    colour with the system theme while the backdrop behind it did not.
//  * `dangerBg` is `TaminLightSurface` in light — a near-white block, on a dark backdrop.
//  * `blueText`, `greenText`, `chevron` and the `*Bg` tints are all tuned for a light
//    page and read as dark smears here. Two call sites had already been hand-patched to
//    white with a comment saying exactly that.
//
// Tokens that are identical in both themes (`onGradient`, `buttonGradient`,
// `aiAssistantTint`) were no less wrong to read — they were one theme edit away from
// breaking this screen from a distance. They are frozen below.

/**
 * Every hue the assistant uses. Change a value here and it propagates to the backdrop,
 * the orb, the glass, the bubbles and the charts at once.
 *
 * Names describe the colour, not its job — the job lives in [AgentColors]/[AgentGlass].
 */
internal object AgentPalette {

    // ── Backdrop ramp (Figma 90:14) ──
    val backdropTop = Color(0xFF0A1633)
    val backdropUpper = Color(0xFF11264D)
    val backdropLower = Color(0xFF231A5C)

    /** Darkest stop; doubles as the vignette that closes the composition. */
    val backdropDeep = Color(0xFF080F26)

    // ── The five brand hues ──
    // These drive the conic wash, the four backdrop blobs and the orb's angular fill.
    // They are the identity of the screen; everything else is supporting cast.
    val violet = Color(0xFFBA6CFF)
    val periwinkle = Color(0xFF6B53C5)
    val blueDeep = Color(0xFF1B3A8A)
    val bluePale = Color(0xFFB6D0FF)
    val indigo = Color(0xFF5B46E4)

    // ── Glass substrate ──
    /** Tint and backdrop of the frosted bars. */
    val frost = Color(0xFF0E1B3F)

    /** Cast under floating glass; deeper than [backdropDeep] so the lift reads. */
    val shadow = Color(0xFF040A1E)

    // ── Content ──
    val contentHigh = Color(0xFFE2ECFF)
    val contentMedium = Color(0xFFA9BDE6)
    val contentIcon = Color(0xFFD5E1FA)

    /** Composer icons at rest — a step below [contentIcon] so the send button leads. */
    val contentMuted = Color(0xFFBFD0F0)

    /** Text on the violet-tinted surfaces: drawer rows and the model chip. */
    val contentOnViolet = Color(0xFFE6DEFF)
    val contentOnChip = Color(0xFFD8CFFF)

    // ── Accents ──
    /** Links and table headers. */
    val accentBlue = Color(0xFF7FB0FF)

    /** Formulas — deliberately not [accentBlue], so equations do not read as links. */
    val accentTeal = Color(0xFF6FDDC4)

    val accentPurple = Color(0xFF7C5CFF)
    val accentBlueMid = Color(0xFF3B6FD4)
    val accentPurpleSoft = Color(0xFFA78BFA)

    /** The assistant's own mark — the send button once there is something to send. */
    val accentPurpleDeep = Color(0xFF3B1E86)

    // ── Chart series (beside [accentBlue] and [accentTeal], same lightness) ──
    val chartAmber = Color(0xFFFFC46B)
    val chartRose = Color(0xFFF08BAB)
    val chartLilac = Color(0xFFB69CFF)
    val chartSky = Color(0xFF9AD0FF)

    // ── Status ──
    val success = Color(0xFF3DDC84)
    val danger = Color(0xFFFF8E8E)
    val dangerStrong = Color(0xFFFF6B6B)
    val dangerSoft = Color(0xFFFFC4C4)
    val dangerGradientStart = Color(0xFFF0635F)
    val dangerGradientEnd = Color(0xFFC42121)

    // ── The user's bubble, and the buttons that match it ──
    // Frozen from the light theme's brand navy, which is what this screen has always
    // shown in light mode. See the note at the top of this file.
    val brandNavyDeep = Color(0xFF173D7E)
    val brandNavy = Color(0xFF1F4FA3)
}

/**
 * What the palette is used for. Glass-surface roles live in [AgentGlass] instead.
 */
internal object AgentColors {

    /**
     * The colour every scrim, sheen, hairline and white label on this screen is built from.
     * Call sites keep their own alpha — those are depth decisions, not palette ones —
     * but the hue itself is here, so a light re-skin is one edit rather than seventy.
     */
    val ink: Color = Color.White

    /**
     * The opposite pole: media letterboxing, drawer scrims, and the wash behind an inline
     * code span. Kept separate from [ink] because these darken whatever is under them
     * rather than carrying the palette's identity.
     */
    val scrim: Color = Color.Black

    /** The wash behind `inline code` in a reply. */
    val inlineCodeFill: Color = scrim.copy(alpha = 0.094f)

    // ── Backdrop (AgentBackground.kt owns the geometry; these are its colours) ──

    /** 90:14 — the 168° base ramp, navy → indigo → violet → near-black. */
    val backdropStops: Array<Pair<Float, Color>> = arrayOf(
        0.00f to AgentPalette.backdropTop,
        0.36f to AgentPalette.backdropUpper,
        0.68f to AgentPalette.backdropLower,
        1.00f to AgentPalette.backdropDeep,
    )

    /** 90:15 — the conic wash, one turn through the brand hues. */
    val backdropSweepStops: Array<Pair<Float, Color>> = arrayOf(
        0.000f to AgentPalette.violet.copy(alpha = 0.22f),
        0.125f to AgentPalette.periwinkle.copy(alpha = 0.26f),
        0.250f to AgentPalette.blueDeep.copy(alpha = 0.30f),
        0.500f to AgentPalette.bluePale.copy(alpha = 0.14f),
        0.750f to AgentPalette.indigo.copy(alpha = 0.30f),
        1.000f to AgentPalette.violet.copy(alpha = 0.22f),
    )

    /** 90:16 … 90:19 — the four radial blobs, in Figma's child order. */
    val backdropBlobViolet: Color = AgentPalette.violet.copy(alpha = 0.62f)
    val backdropBlobIndigo: Color = AgentPalette.indigo.copy(alpha = 0.58f)
    val backdropBlobBlueDeep: Color = AgentPalette.blueDeep.copy(alpha = 0.60f)
    val backdropBlobBluePale: Color = AgentPalette.bluePale.copy(alpha = 0.34f)

    /** 90:20 — the vignette that darkens the composition downwards. */
    val backdropVignette: Color = AgentPalette.backdropDeep

    // ── The orb (AgentOrb.kt owns the ramps; these are its colours) ──

    /** 90:43 — the halo around the sphere. */
    val orbHalo: Color = AgentPalette.violet

    /**
     * 90:42 — the four corners of the sphere's angular fill, in `sweepGradient` order
     * (3, 6, 9, 12 o'clock).
     */
    val orbCorners: List<Color> = listOf(
        AgentPalette.violet,
        AgentPalette.blueDeep,
        AgentPalette.bluePale,
        AgentPalette.indigo,
    )

    /**
     * What the sphere's 16 dp layer blur leaves at the centre: the mean of [orbCorners],
     * which is what an angular kernel wider than the whole sweep averages to.
     *
     * Computed rather than pinned to `#7A6FDB` so it keeps following the corners when the
     * palette changes — otherwise a re-skin leaves a stale dot at the most-looked-at pixel
     * on the screen.
     */
    val orbCore: Color = orbCorners.meanColor()

    // ── The user's chat bubble ──

    /**
     * The brand gradient the user's card sits on. Absolute, not `taminTopAppBarGradient()`:
     * that one swings from navy to teal between light and dark, so the bubble used to
     * change colour underneath a backdrop that never does.
     */
    val bubbleGradient: Brush = Brush.horizontalGradient(
        listOf(AgentPalette.brandNavyDeep, AgentPalette.brandNavy)
    )

    /** Text and waveform inside the user's bubble. */
    val onBubble: Color = ink

    // ── Action buttons (the assistant's suggested next steps, media play controls) ──

    val actionGradient: Brush = Brush.verticalGradient(
        listOf(AgentPalette.accentBlueMid, AgentPalette.brandNavyDeep)
    )

    /** A target the app cannot act on; matches the app's own disabled alpha. */
    const val DISABLED_ALPHA = 0.38f

    // ── Top bar and history drawer ──

    /** The assistant's badge and the drawer's accent — one gradient, used in both. */
    val brandGradient: Brush = Brush.linearGradient(
        listOf(AgentPalette.accentPurple, AgentPalette.accentBlueMid)
    )

    val brandGradientSoft: Brush = Brush.linearGradient(
        listOf(
            AgentPalette.accentPurple.copy(alpha = 0.34f),
            AgentPalette.accentBlueMid.copy(alpha = 0.26f),
        )
    )

    /** Lifts the drawer's selected row off the glass. */
    val drawerGlow: Color = AgentPalette.indigo.copy(alpha = 0.45f)
    val drawerAccentSoft: Color = AgentPalette.accentPurpleSoft
    val onDrawerAccent: Color = AgentPalette.contentOnViolet

    /** The model chip in the top bar. */
    val chipFill: Color = AgentPalette.accentPurpleSoft.copy(alpha = 0.22f)
    val chipBorder: Color = AgentPalette.accentPurpleSoft.copy(alpha = 0.40f)
    val onChip: Color = AgentPalette.contentOnChip

    /** The "online" dot beside the assistant's name. */
    val onlineDot: Color = AgentPalette.success

    // ── Composer ──

    /** Mic and attachment at rest. */
    val composerMutedIcon: Color = AgentPalette.contentMuted

    /** The send button once the field has something in it. */
    val composerSendActive: Color = AgentPalette.accentPurpleDeep

    val composerText: Color = AgentPalette.contentHigh
    val composerPlaceholder: Color = AgentPalette.contentHigh

    // ── Status ──

    /** A completed processing step. Was the theme's `greenText`, which is a light-page green. */
    val success: Color = AgentPalette.success

    /** The tile behind the "not permitted" icon. */
    val dangerGradient: Brush = Brush.verticalGradient(
        listOf(AgentPalette.dangerGradientStart, AgentPalette.dangerGradientEnd)
    )

    // ── Opening suggestions ──

    /**
     * The four starter prompts' icon tiles. Was the theme's `blueBg`/`tealBg`/
     * `fuchsiaBlueBg`/`greenBg` — pale light-page fills that all but vanished here.
     */
    val suggestionTints: List<Color> = listOf(
        AgentPalette.accentBlue,
        AgentPalette.accentTeal,
        AgentPalette.accentPurpleSoft,
        AgentPalette.success,
    )
}

/**
 * The component-wise mean of a set of colours, in straight (non-premultiplied) sRGB —
 * which is the space Figma's layer blur averages in.
 */
private fun List<Color>.meanColor(): Color = Color(
    red = sumOf { it.red.toDouble() }.toFloat() / size,
    green = sumOf { it.green.toDouble() }.toFloat() / size,
    blue = sumOf { it.blue.toDouble() }.toFloat() / size,
    alpha = sumOf { it.alpha.toDouble() }.toFloat() / size,
)
