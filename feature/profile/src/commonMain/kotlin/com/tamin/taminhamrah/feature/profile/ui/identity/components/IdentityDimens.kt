package com.tamin.taminhamrah.feature.profile.ui.identity.components

import androidx.compose.ui.unit.dp

/**
 * Identity-screen layout numbers, taken from the design's SVG export and kept in one place
 * instead of scattered through the card's measure block. Offsets are measured from the card's
 * own top-start corner, so they read the same way the export does.
 *
 * Shared design tokens (spacing, radii, colors, type) live in core-ui's theme; these are the
 * ones only the insured-person card needs.
 */
internal object IdentityDimens {

    /** How much drag folds the header from open to collapsed. */
    val headerCollapseDistance = 220.dp

    /**
     * The width the design was drawn at. Every dp below is measured on that artboard, so the card
     * scales them by whatever width it is actually handed — otherwise a narrower screen gets the
     * artboard's absolute sizes and the card reads oversized.
     */
    val designCardWidth = 374.dp

    /**
     * Trims the whole card below the artboard's proportions.
     */
    const val cardScale = 0.92f

    // The card, expanded and folded.
    val cardExpandedHeight = 236.dp
    val cardCollapsedHeight = 64.dp
    val cardCorner = 22.dp

    /** The large rounded bottom of the hero header. */
    val headerCorner = 40.dp

    /** The card's own inset. Everything on its face lines up to this. */
    val cardPadding = 20.dp

    /** How far the card rides up into the header when open. */
    val cardOverlap = 32.dp

    /**
     * Folded, the bar rides up by half its own height, so the header's bottom edge runs through
     * its center rather than sitting above it. Derived so it stays true if the bar height moves.
     */
    val cardCollapsedOverlap = cardCollapsedHeight / 2

    /**
     * Clear air between the app bar's title row and the top of the card.
     */
    val cardHeaderGap = 24.dp

    // Slot offsets inside the expanded card, measured from its top.
    val topInfoTop = 20.dp
    val avatarTop = 20.dp
    // Derived from the design rather than nudged: inside the card the body starts 14px below the
    // 15px top padding, the four field rows occupy 91.8px (16.8+16.8+15.6+15.6 plus three 9px
    // gaps), and the strip adds margin-top:14px before its border-top. That puts the rule at
    // 134.8px in design space; scaled() divides by 0.92, hence 146.5.
    val footerRuleTop = 146.5.dp
    // The strip's padding-top:12px below the rule -> 146.8px in design space, /0.92.
    val footerTop = 159.5.dp

    // The holder's photo.
    val avatarWidth = 72.dp
    val avatarHeight = 80.dp
    val avatarCorner = 14.dp
    val avatarCollapsedHeight = 36.dp

    /** Air between the photo and the name beside it. */
    // Design has gap:14px between the field column and the photo; /0.92 = 15.2.
    val avatarNameGap = 15.2.dp

    val bannerIconSize = 18.dp

    /** The card's soft blue drop shadow. */
    val cardShadow = 20.dp

    // The sheen rings: thin arcs of light.
    val ringNearInset = 35.dp
    val ringNearTop = 21.dp
    val ringFarInset = 39.dp
    val ringOuterRadius = 105.dp
    val ringInnerRadius = 75.dp
    val ringFootRadius = 85.dp

    // The card's furniture, at the export's alphas.
    const val ringOuterAlpha = 0.09f
    const val ringInnerAlpha = 0.07f
    const val ringFootAlpha = 0.06f
    const val avatarBorderAlpha = 0.28f
    const val AVATAR_GLYPH_ALPHA = 0.85f
    const val footerRuleAlpha = 0.18f
    const val topEdgeAlpha = 0.16f

    const val ssnCollapsedScale = 0.82f
    const val nameCollapsedScale = 0.95f

    /**
     * The expanded-only pieces fade this much faster than the fold, so they have cleared well
     * before the bar forms rather than ghosting over it.
     */
    const val VANISH_RATE = 3f

    // ── Detail sections ──────────────────────────────────────────────────────────
    // Outside the card, so these are 1:1 with the design's pixels — no scaled().

    /** Design: section caption `margin:20px 0 10px`. */
    val sectionLabelGap = 10.dp

    /** Design: each row is `padding:14px 16px`; the horizontal half is Spacing.lg. */
    val rowVerticalPadding = 14.dp

    // ── Card interior ────────────────────────────────────────────────────────────

    /** Gap between the stacked name/father/birth rows. */
    val cardFieldGap = 4.dp

    /** Hairlines: the footer rule, the avatar border and the card's top edge. */
    val hairline = 1.dp
}
