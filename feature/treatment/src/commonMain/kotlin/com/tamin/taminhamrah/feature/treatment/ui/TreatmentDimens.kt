package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.ui.unit.dp

/**
 * Treatment-specific UI constants, kept in one place instead of scattered as private vals across
 * the hub components, the hub screen and the previews. Shared design tokens (spacing, radii, icon
 * sizes) live in core-ui's theme; these are layout numbers that only the treatment screens need.
 */
internal object TreatmentDimens {
    /** How far the insured-person carousel rides up into the hub header. */
    val cardOverlap = 40.dp

    // Insured-person card carousel.
    val coverageBadgeSize = 16.dp
    val coverageBadgeIconSize = 10.dp
    val brandTickSize = 24.dp
    val brandTickIconSize = 13.dp
    const val cardDecorAlpha = 0.07f
    const val cardPeekFraction = 0.87f

    // The full-page phone frame used only by @Preview.
    val pageWidth = 412.dp
    val pageHeight = 892.dp
}
