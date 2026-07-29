package com.tamin.taminhamrah.feature.profile.ui.identity

import androidx.compose.ui.unit.dp

/**
 * Identity-screen layout numbers, kept in one place instead of scattered through the card's
 * measure block. Shared design tokens (spacing, radii, colors, type) live in core-ui's theme;
 * these are the ones only the insured-person card needs.
 */
internal object IdentityDimens {

    /** How much drag folds the header from open to collapsed. */
    val headerCollapseDistance = 220.dp

    // The card, expanded and folded.
    val cardExpandedHeight = 216.dp
    val cardCollapsedHeight = 64.dp
    val cardCorner = 20.dp

    /** How far the card rides up into the header, open and folded. */
    val cardOverlap = 32.dp
    val cardCollapsedOverlap = 8.dp

    /**
     * Clear air between the app bar's title row and the top of the card.
     *
     * The app bar reserves [cardOverlap] + this below its content and the card then rides
     * [cardOverlap] of it back, so what is left is the gap. Reserving only the overlap would
     * cancel out and leave the card sitting on the title.
     */
    val cardHeaderGap = 24.dp

    // Slot offsets inside the expanded card, measured from its top.
    val brandTop = 18.dp
    val identityTop = 58.dp
    val ssnTop = 112.dp

    // Avatar, expanded and folded into the top bar.
    val avatarSize = 52.dp
    val avatarCollapsedSize = 34.dp
    val avatarCorner = 16.dp
    val avatarInnerCorner = 12.dp
    val avatarRim = 4.dp

    // The card's chrome.
    val chipWidth = 38.dp
    val chipHeight = 28.dp
    val chipCorner = 6.dp
    val brandTileSize = 32.dp
    val brandIconSize = 20.dp
    val verifiedIconSize = 14.dp
    val bannerIconSize = 18.dp

    /** The band across the card's foot that carries the national code and date of birth. */
    val footerBandHeight = 54.dp

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
