package com.tamin.taminhamrah.feature.girlSurvivor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.close_content_description
import taminx.core.core_ui.girl_survivor_subtitle
import taminx.core.core_ui.girl_survivor_title
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_cross

/**
 * Collapsing gradient header for تعهدنامه بازمانده اناث — same TopArea fold as
 * [com.tamin.taminhamrah.feature.contractaffair.ui.components.ContractAffairsHeader]:
 * the title row stays put; ring icon + subtitle fold away via [topAreaHide].
 */
@Composable
internal fun GirlSurvivorHeader(
    onBackClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(gradient),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.girl_survivor_title),
            background = gradient,
            bottomPadding = Spacing.none,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(Res.string.back_content_description),
                    onClick = onBackClicked,
                    bordered = true,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_cross),
                    contentDescription = stringResource(Res.string.close_content_description),
                    onClick = onCloseClicked,
                    bordered = true,
                )
            },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaHide(topAreaState)
                .padding(bottom = Spacing.xl),
        ) {
            DecorativeBackgroundCircle(
                size = HeaderDecoration.circleSize,
                xOffset = HeaderDecoration.circleXOffset,
                yOffset = HeaderDecoration.circleYOffset,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedRingHeaderIcon(
                    icon = vectorResource(Res.drawable.ic_request),
                    animated = !topAreaState.isMeasureProbe,
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = stringResource(Res.string.girl_survivor_subtitle),
                    style = MaterialTheme.typography.labelLarge,
                    color = taminColors.textHeaderSubtitle,
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaReveal(topAreaState, Spacing.lg),
        )
    }
}

/** Grows a child from zero up to [height] as [state] folds — the inverse of [topAreaHide]. */
private fun Modifier.topAreaReveal(state: TopAreaState, height: Dp): Modifier =
    layout { measurable, constraints ->
        val targetPx = height.roundToPx()
        val revealedPx = (targetPx * state.progress).roundToInt().coerceAtLeast(0)
        val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = revealedPx))
        layout(placeable.width, revealedPx) { placeable.place(0, 0) }
    }
