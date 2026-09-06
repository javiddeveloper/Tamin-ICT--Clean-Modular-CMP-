package com.tamin.taminhamrah.feature.contracts.ui.affairs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.rememberTopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_search_title
import taminx.core.core_ui.contract_affairs_subtitle
import taminx.core.core_ui.contract_affairs_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search

/**
 * Collapsing gradient header for امور قراردادها و پرداخت, driven by the shared TopArea system —
 * same fold behaviour as `ActiveRelationHeader`:
 *  - the top-app-bar row (title, back, search) stays put and the title is never swapped for
 *    anything else,
 *  - only the ring icon + subtitle block folds away, via [topAreaHide] — it fades and loses
 *    height in place, with no sideways slide.
 */
@Composable
internal fun ContractAffairsHeader(
    onBackClicked: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
    onSearchClicked: () -> Unit = {},
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(taminTopAppBarGradient(taminColors.profileGradientStops)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.contract_affairs_title),
            background = gradient,
            bottomPadding = Spacing.none,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    contentDescription = stringResource(Res.string.contract_affairs_search_title),
                    onClick = onSearchClicked,
                )
            },
        )

        // Only this block folds — the bar above keeps its expanded-state layout. It must span the
        // full 0f..1f progress range (the range the drag budget was measured against), so the
        // header's rendered height tracks the drag exactly.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaHide(topAreaState)
                .padding(horizontal = Spacing.page)
                .padding(top = Spacing.smPlus, bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp,
                    )
                    AnimatedRingHeaderIcon(
                        icon = Icons.Outlined.Description,
                        animated = !topAreaState.isMeasureProbe,
                    )
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = stringResource(Res.string.contract_affairs_subtitle),
                        style = MaterialTheme.typography.labelLarge,
                        color = taminColors.textHeaderSubtitle,
                    )
                }
            }
        }

        // Zero height while expanded, growing to [CollapsedBottomSpace] as the header folds — the
        // inverse of [topAreaHide], so the collapsed bar keeps a little room below its title row
        // without widening the expanded gap above the ring icon.
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaReveal(topAreaState, CollapsedBottomSpace),
        )
    }
}

private val CollapsedBottomSpace = 16.dp

/** Grows a child from zero up to [height] as [state] folds — the inverse of [topAreaHide]. */
private fun Modifier.topAreaReveal(state: TopAreaState, height: Dp): Modifier =
    layout { measurable, constraints ->
        val targetPx = height.roundToPx()
        val revealedPx = (targetPx * state.progress).roundToInt().coerceAtLeast(0)
        val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = revealedPx))
        layout(placeable.width, revealedPx) { placeable.place(0, 0) }
    }

@PreviewRtlTheme
@Composable
private fun ContractAffairsHeaderPreviewLight() {
    PreviewRtlThemeContent {
        ContractAffairsHeader(
            onBackClicked = {},
            topAreaState = rememberTopAreaState(expandedHeight = 240.dp, collapsedHeight = 64.dp),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsHeaderPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractAffairsHeader(
            onBackClicked = {},
            topAreaState = rememberTopAreaState(expandedHeight = 240.dp, collapsedHeight = 64.dp),
        )
    }
}
