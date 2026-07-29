package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlin.math.roundToInt
import taminx.core.core_ui.Res
import taminx.core.core_ui.tab_treatment
import org.jetbrains.compose.resources.stringResource

/**
 * The hub header: the gradient bar keeps its colors and title in place while the insured-person
 * [card] riding up into it morphs as [progress] runs 0 → 1. It sits anchored over the scrollable
 * body so content slides under it when collapsed.
 *
 * [progress] is a lambda, not a value, so it is read in the layout phase only — see
 * [com.tamin.taminhamrah.ui.components.CollapsingHeaderState].
 */
@Composable
internal fun TreatmentHubHeader(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    card: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = stringResource(Res.string.tab_treatment),
            centerTitle = false,
            bottomPadding = TreatmentDimens.cardOverlap + Spacing.xl,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .rideUpIntoHeader(
                    progress = progress,
                    expandedOverlap = TreatmentDimens.cardOverlap,
                    collapsedOverlap = TreatmentDimens.collapsedCardOverlap,
                ),
        ) {
            card()
        }
    }
}

/**
 * Lifts the content into the band above it by [expandedOverlap], narrowing to [collapsedOverlap]
 * as [progress] runs 0 → 1, and gives back the overlapped strip so the body below closes up
 * instead of leaving a gap.
 */
private fun Modifier.rideUpIntoHeader(
    progress: () -> Float,
    expandedOverlap: Dp,
    collapsedOverlap: Dp,
): Modifier = layout { measurable, constraints ->
    val overlapPx = lerp(expandedOverlap.toPx(), collapsedOverlap.toPx(), progress())
    val placeable = measurable.measure(constraints)
    val reserved = (placeable.height - overlapPx).coerceAtLeast(0f).roundToInt()
    layout(placeable.width, reserved) {
        placeable.place(0, -overlapPx.roundToInt())
    }
}
