package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.ui.unit.dp

/**
 * What is left of the refund card's own numbers.
 *
 * The card's shell, bands, chip, stamp, tracking line and footer all moved into core-ui's
 * `RecordCard` when the personal inbox started sharing it — only the detail rows below the rule
 * are still drawn here, so only their measurements remain.
 */
internal object TreatmentCostsDimens {

    /** Drag it takes to fold the hero bar down to its title row. */
    val headerCollapseDistance = 120.dp

    /** A soft shadow right under the card, as if lit from directly above. */
    val cardShadowBlur = 10.dp
    val cardShadowOffsetY = 2.dp

    /** Detail rows carry their own rhythm so a rule sits flush between two of them. */
    val detailRowPadding = 12.dp

    /** The dashed rule under every row but the first and the last. */
    val dashedStrokeWidth = 1.dp
    val dashedDashLength = 4.dp
    val dashedGapLength = 4.dp
}
