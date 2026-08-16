package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Confirmations-specific UI dimens and constants to minimize hardcoded dp/sp values,
 * ensure consistent spacing, and optimize Compose layout performance.
 */
internal object TreatmentConfirmationsDimens {
    val headerCollapseDistance = 120.dp
    val cardPaddingHorizontal = 20.dp
    val cardPaddingTop = 15.dp
    val cardPaddingBottom = 15.dp

    /**
     * The design gives each block of the card its own top gap rather than one uniform rhythm:
     * 11dp under the header row, 13dp around the rest panel.
     */
    val cardSectionSpacing = 13.dp
    val detailRowPadding = 10.dp
    val detailCardPaddingHorizontal = 18.dp
    val detailCardPaddingTop = 15.dp
    val detailCardPaddingBottom = 16.dp
    val statusDotSize = 6.dp
    val iconSizeSmall = 14.dp
    val iconSizeMedium = 16.dp
    val verdictBarWidth = 3.dp
    val verdictBarHeightExpanded = 64.dp
    val medicalCenterSpacing = 2.dp
    val detailsButtonHeight = 38.dp

    // Rest panel -- the framed block holding the rest period and its timeline.
    val restPanelPaddingHorizontal = 14.dp
    val restPanelPaddingTop = 12.dp
    val restPanelPaddingBottom = 13.dp

    /**
     * Each end of the timeline is a fixed-width column with the date centred over its marker.
     * [restBarInset] is exactly half of it, which is what lets the bar between the columns run
     * *under* both and stop dead on each marker's center -- the design writes it as a negative
     * 41px margin on a bar that would otherwise start at the columns' inner edges.
     */
    val restEndpointWidth = 82.dp
    val restBarInset = 41.dp
    val restEndpointSpacing = 7.dp
    val restBarHeight = 6.dp
    val restBarBottomInset = 5.dp
    val restStartMarkerSize = 16.dp
    val restStartMarkerRing = 4.dp
    val restEndPinWidth = 18.dp
    val restEndPinHeight = 22.dp

    /**
     * The end marker is traced from the design's SVG, so its path coordinates stay in that
     * drawing's 24x30 viewport and are scaled to [restEndPinWidth] x [restEndPinHeight] at paint
     * time. These are viewport units, not dp.
     */
    const val PIN_VIEWPORT_WIDTH = 24f
    const val PIN_VIEWPORT_HEIGHT = 30f
    const val PIN_CORE_RADIUS = 3.4f

    /** The halo around the start marker, and the tint under the modal's button. */
    const val MARKER_HALO_ALPHA = 0.18f
    const val MODAL_BUTTON_SHADOW_ALPHA = 0.28f

    /** The detail view draws the same timeline in a slightly roomier frame. */
    val detailRestPanelPaddingHorizontal = 16.dp
    val detailRestPanelPaddingTop = 14.dp
    val detailRestPanelPaddingBottom = 15.dp
    val detailRestMarkerSpacing = 8.dp

    // Saved-to-inbox modal.
    val modalMaxWidth = 320.dp
    val modalPaddingHorizontal = 22.dp
    val modalPaddingTop = 26.dp
    val modalPaddingBottom = 22.dp
    val modalIconSize = 64.dp
    val modalIconGlyphSize = 26.dp
    val modalIconBottomSpacing = 15.dp
    val modalTextSpacing = 10.dp
    val modalButtonSpacing = 18.dp
    val modalButtonHeight = 46.dp
    val modalButtonCornerRadius = 15.dp

    val pillCornerRadiusPercent = 50
    val dashedStrokeWidth = 1.dp
    val dashedDashLength = 4.dp
    val dashedGapLength = 4.dp

    val verdictLineHeight = 22.sp

    // Shimmer Skeleton dimensions
    val shimmerFilterPillWidth = 100.dp
    val shimmerStatusPillWidth = 80.dp
    val shimmerLabelWidth = 60.dp
    val shimmerTimelineHeight = 64.dp
    val shimmerButtonHeight = 36.dp
    val shimmerBranchWidth = 100.dp
    val shimmerDetailBtnWidth = 70.dp

    /** The shimmer placeholder's bars, sized to the rows they stand in for. */
    val skeletonChipHeight = 24.dp
    val skeletonLineTall = 20.dp
    val skeletonLineMedium = 18.dp
    val skeletonLineShort = 12.dp
    val skeletonLineCorner = 4.dp
}
