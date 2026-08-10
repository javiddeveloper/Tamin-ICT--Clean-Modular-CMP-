package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Layout numbers for the refund card, read from the design's own markup rather than measured off a
 * render — see `docs/superpowers/specs/2026-08-05-treatment-costs-card.md`.
 *
 * Separate from [TreatmentDimens] because that object holds the hub's numbers; these belong to one
 * screen and would otherwise be private vals scattered through the card, its stamp and its menu.
 * Anything a second screen could want — spacing, radii, icon sizes — stays in core-ui's theme.
 */
internal object TreatmentCostDimens {

    // The card shell.
    val cardCorner = 18.dp
    val cardPaddingHorizontal = 20.dp
    val cardShadowBlur = 26.dp
    val cardShadowOffsetY = 10.dp

    /** The teal rail down the trailing edge, and the wash under the top edge. */
    val railWidth = 3.dp
    val topWashHeight = 26.dp
    const val TOP_WASH_ALPHA = 0.05f

    // Band paddings, top to bottom.
    val claimRowTop = 15.dp
    val patientRowTop = 11.dp
    val patientRowBottom = 14.dp
    val receiptRowTop = 2.dp
    val receiptRowBottom = 4.dp
    val ruleTop = 12.dp
    val ruleBottom = 4.dp
    val footerTop = 6.dp
    val footerBottom = 16.dp

    // The claim chip.
    val chipPaddingHorizontal = 12.dp
    val chipPaddingVertical = 5.dp
    val chipIconSize = 14.dp

    /** The payment stamp: a check over its label, ringed by a dashed circle. */
    val stampRingSize = 54.dp
    val stampCheckSize = 21.dp
    val stampDashOn = 3.dp
    val stampDashOff = 4.dp
    const val STAMP_RING_ALPHA = 0.35f

    // The receipt line and its copyable chip.
    val receiptIconSize = 15.dp
    val receiptLetterSpacing = 1.sp
    val copyChipCorner = 9.dp
    val copyChipBorderWidth = 1.4.dp
    val copyChipDashOn = 3.dp
    val copyChipDashOff = 3.dp
    val copyChipPaddingStart = 6.dp
    val copyChipPaddingEnd = 8.dp
    val copyChipPaddingVertical = 5.dp
    val copyChipGap = 7.dp

    /** Detail rows carry their own rhythm so a rule sits flush between two of them. */
    val detailRowPadding = 12.dp

    // The footer and the actions popover.
    val footerButtonCorner = 14.dp
    val footerButtonGap = 9.dp
    val footerButtonPaddingVertical = 12.dp
    val operationsPaddingHorizontal = 18.dp
    val chevronSize = 15.dp
    val menuWidth = 206.dp
    val menuCorner = 16.dp
    val menuIconSize = 16.dp
    val menuItemPaddingHorizontal = 14.dp
    val menuItemPaddingVertical = 12.dp
}
