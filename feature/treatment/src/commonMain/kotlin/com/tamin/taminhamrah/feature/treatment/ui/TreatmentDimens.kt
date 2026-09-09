package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.ui.unit.dp

/**
 * Treatment-specific UI constants, kept in one place instead of scattered as private vals across
 * the hub components, the hub screen and the previews. Shared design tokens (spacing, radii, icon
 * sizes) live in core-ui's theme; these are layout numbers that only the treatment screens need.
 */
internal object TreatmentDimens {
    /** Timeline action buttons, and the placeholders the record detail reserves for a PDF. */
    val timelineActionSize = 38.dp
    val recordsFooterSpacer = 100.dp
    val detailPdfPlaceholderTall = 120.dp
    val detailPdfPlaceholderShort = 80.dp

    /** The advanced-search sheet: inputs sized to the design rather than Material's 56dp floor. */
    val searchFieldHeight = 40.dp
    val searchFieldCorner = 13.dp
    val searchActionHeight = 52.dp
    val searchActionCorner = 14.dp
    val searchChipPaddingHorizontal = 18.dp
    val searchChipPaddingVertical = 10.dp
    val searchHandleIconSize = 32.dp
    val searchHandleGlyphSize = 18.dp

    /**
     * How far every card in the feature sits off the page.
     *
     * One number for all of them on purpose: a list where cards lift by different amounts reads as
     * a mistake rather than a hierarchy. Deep enough to cast a real shadow, not so deep that a
     * scrolling list looks like it is peeling away.
     */
    val cardElevation = 12.dp

    /** Header collapse scroll distance. */
    val headerCollapseDistance = 96.dp

    /** How far the insured-person carousel rides up into the hub header. `margin: -40px 0 0`. */
    val cardOverlap = 40.dp

    /**
     * What is left of the hub header below its title once the carousel has ridden up into it:
     * the design's `padding: 16px 20px 66px` less the card's own 40px of overlap.
     */
    val hubHeaderUnderTitle = 26.dp

    /** How far the collapsed card rides up into the hub header so the header line bisects it. */
    val collapsedCardOverlap = 26.dp

    /*
     * Insured-person card carousel.
     *
     * Every length below is the design's own, in CSS pixels at its 412px frame, which map 1:1 to
     * dp. The card is `padding: 14px 16px 12px` inside a 20px radius, its coverage line sits under
     * a 1px rule at `padding: 9px 16px`, and the strip of dots is `padding: 7px 11px` in a pill.
     */
    val cardPaddingTop = 14.dp
    val cardPaddingHorizontal = 16.dp
    val cardPaddingBottom = 12.dp

    /** `margin-top: 13px` — the gap between the brand row and the holder's name. */
    val cardNameTopGap = 13.dp

    /** `margin-top: 5px` above «کد ملی», `margin-top: 2px` above the number itself. */
    val cardCodeLabelTopGap = 5.dp
    val cardCodeTopGap = 2.dp

    val cardFooterPaddingHorizontal = 16.dp
    val cardFooterPaddingVertical = 9.dp
    val cardFooterGap = 7.dp

    val coverageBadgeSize = 16.dp
    val coverageBadgeIconSize = 10.dp
    val brandTickSize = 24.dp
    val brandTickIconSize = 13.dp
    val cardLoadingHeight = 160.dp

    val pageIndicatorDotSize = 7.dp
    val pageIndicatorSelectedWidth = 22.dp
    val pageIndicatorGap = 7.dp
    val pageIndicatorPaddingHorizontal = 11.dp
    val pageIndicatorPaddingVertical = 7.dp

    /** `margin-top: 12px` between the cards and the strip of dots. */
    val pageIndicatorTopGap = 12.dp

    /** Past this the strip scrolls rather than growing the pill off the card. */
    val pageIndicatorMaxWidth = 140.dp

    /*
     * The three translucent shapes over the card's gradient, each at its own strength:
     * `#ffffff12` for the swoosh down the trailing edge, `#ffffff0d` and `#ffffff14` for the two
     * ellipses. They are not one alpha — the swoosh has to read against both ends of the wash.
     */
    const val cardDecorSwooshAlpha = 0.07f
    const val cardDecorLowerAlpha = 0.05f
    const val cardDecorUpperAlpha = 0.08f

    /**
     * `width: 87%` of the carousel track's *content* box, not of the viewport — the track itself
     * is inset by [cardTrackPadding] on both edges first. Missing that inset is what makes the
     * neighbors peek too little.
     */
    const val cardPeekFraction = 0.87f
    val cardTrackPadding = 18.dp

    /** `gap: 12px` between two cards in the track. */
    val cardTrackGap = 12.dp

    // Brand tile on the insurance card — the holder's initial, top of the card.
    val brandTileSize = 30.dp
    val brandTileRadius = 10.dp

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
