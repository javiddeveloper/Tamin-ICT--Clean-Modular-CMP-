package com.tamin.taminhamrah.feature.profile.ui.identity

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
     *
     * Separate from [designCardWidth], which records the width the design was drawn at and should
     * keep saying so: this is the deliberate "render it a little smaller than drawn" decision.
     */
    const val cardScale = 0.9f

    // The card, expanded and folded.
    val cardExpandedHeight = 276.dp
    val cardCollapsedHeight = 64.dp
    val cardCorner = 22.dp

    /** The card's own inset. Everything on its face lines up to this. */
    val cardPadding = 20.dp

    /** How far the card rides up into the header when open. */
    val cardOverlap = 32.dp

    /**
     * Folded, the bar rides up by half its own height, so the header's bottom edge runs through
     * its centre rather than sitting above it. Derived so it stays true if the bar height moves.
     */
    val cardCollapsedOverlap = cardCollapsedHeight / 2

    /**
     * Clear air between the app bar's title row and the top of the card.
     *
     * The app bar reserves [cardOverlap] + this below its content and the card then rides
     * [cardOverlap] of it back, so what is left is the gap. Reserving only the overlap would
     * cancel out and leave the card sitting on the title.
     */
    val cardHeaderGap = 24.dp

    // Slot offsets inside the expanded card, measured from its top.
    val brandTop = 19.dp
    val chipTop = 20.dp
    val avatarTop = 66.dp
    val ssnCaptionTop = 145.dp
    val ssnNumberTop = 167.dp
    val footerRuleTop = 207.dp
    val footerCaptionTop = 223.dp
    val footerValueTop = 243.dp

    // The holder's photo — taller than it is wide, as a portrait should be.
    val avatarWidth = 48.dp
    val avatarHeight = 58.dp
    val avatarCorner = 12.dp
    val avatarCollapsedHeight = 34.dp

    /** Air between the photo and the name beside it. */
    val avatarNameGap = 8.dp

    // The card's chrome.
    val chipWidth = 35.dp
    val chipHeight = 26.dp
    val chipCorner = 6.dp
    val brandTileSize = 29.dp
    val brandTileCorner = 9.dp
    val brandIconSize = 18.dp
    val verifiedIconSize = 12.dp
    val bannerIconSize = 18.dp

    /** The card's soft blue drop shadow. */
    val cardShadow = 20.dp

    // The sheen rings: thin arcs of light, not filled blobs. Centers are insets from the
    // card's edges and radii are absolute, both straight from the design export.
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
    const val avatarBorderAlpha = 0.18f
    const val avatarGlyphAlpha = 0.7f
    const val brandTileFillAlpha = 0.08f
    const val brandTileBorderAlpha = 0.17f
    const val footerRuleAlpha = 0.11f
    const val topEdgeAlpha = 0.16f

    /** The social-security number stays legible in the folded bar, just a little smaller. */
    const val ssnCollapsedScale = 0.62f

    /** How far the holder's name shrinks by the time the card is a bar. */
    const val nameCollapsedScale = 0.8f

    /**
     * The expanded-only pieces fade this much faster than the fold, so they have cleared well
     * before the bar forms rather than ghosting over it.
     */
    const val vanishRate = 3f
}
