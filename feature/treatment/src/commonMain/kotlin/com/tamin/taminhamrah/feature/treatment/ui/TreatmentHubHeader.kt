package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.components.taminHeroGradient
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.tab_treatment

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
            background = taminHeroGradient(LocalTaminColors.current.treatmentHubStops),
            bottomPadding = TreatmentDimens.cardOverlap + TreatmentDimens.hubHeaderUnderTitle,
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
