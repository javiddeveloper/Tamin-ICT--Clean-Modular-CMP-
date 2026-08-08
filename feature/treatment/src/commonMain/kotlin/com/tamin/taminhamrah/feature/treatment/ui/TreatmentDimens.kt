package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.ui.unit.dp

/**
 * Treatment-specific UI constants, kept in one place instead of scattered as private vals across
 * the hub components, the hub screen and the previews. Shared design tokens (spacing, radii, icon
 * sizes) live in core-ui's theme; these are layout numbers that only the treatment screens need.
 */
internal object TreatmentDimens {
    /** Header collapse scroll distance. */
    val headerCollapseDistance = 96.dp

    /** How far the insured-person carousel rides up into the hub header. */
    val cardOverlap = 40.dp

    /** How far the collapsed card rides up into the hub header so the header line bisects it. */
    val collapsedCardOverlap = 26.dp

    // Insured-person card carousel.
    val coverageBadgeSize = 16.dp
    val coverageBadgeIconSize = 10.dp
    val brandTickSize = 24.dp
    val brandTickIconSize = 13.dp
    val cardLoadingHeight = 160.dp
    val pageIndicatorDotSize = 6.dp
    val pageIndicatorSelectedWidth = 16.dp
    const val cardDecorAlpha = 0.07f

    /** A card's share of the carousel viewport; the rest is the neighbors peeking. */
    const val cardPeekFraction = 0.87f

    // Brand tile on the insurance card (the organization's mark, top of the card).
    val brandTileSize = 34.dp
    val brandTileIconSize = 28.dp

    /** How far the holder name shrinks by the time the card is a compact bar. */
    const val cardNameCollapsedScale = 0.88f

    /** Breathing room above and below the collapsed bar's contents. */
    val cardBarPadding = 28.dp

    /** The share figure's placeholder on a record card, sized to the digits it stands in for. */
    val recordShareShimmerWidth = 48.dp
    val recordShareShimmerHeight = 12.dp

    // Miscellaneous-claim certificates ("خسارت متفرقه")
    val certificateSkeletonHeight = 150.dp
    const val certificateSkeletonRows = 4

    /** A quarter turn: the disclosure chevron points back when closed, down when open. */
    /**
     * The disclosure chevron. The asset points along the reading direction, so it has to be turned
     * a quarter-turn down when closed and up when open -- leaving the closed state at 0 degrees
     * shows a forward arrow, which reads as "navigates away" rather than "expands".
     */
    const val chevronOpenDegrees = -90f
    const val chevronClosedDegrees = 90f

    // Category tiles & badges
    val categoryTileIconSize = 40.dp
    val centerBadgeSize = 44.dp
    val accentBarWidth = 4.dp

    // Bottom sheet & clearance
    val bottomBarClearance = 100.dp
    val sheetHandleWidth = 36.dp
    val sheetHandleHeight = 4.dp
    val sheetCornerRadius = 28.dp

    // The full-page phone frame used only by @Preview.
    val pageWidth = 412.dp
    val pageHeight = 892.dp
}
