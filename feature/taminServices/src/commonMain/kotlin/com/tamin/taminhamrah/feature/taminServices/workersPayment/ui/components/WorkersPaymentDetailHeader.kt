package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.rememberTopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.workers_payment_detail_title
import taminx.core.core_ui.workers_payment_info_sheet_title
import taminx.core.core_ui.workers_payment_subtitle

/** How far the recap card rides up into the gradient — mirrors screen 1's `HEADER_OVERLAP`. */
private val HEADER_OVERLAP = 24.dp

/**
 * Screen 2's header — copied from screen 1's [WorkersPaymentHeader]: a gradient [TaminTopAppBar]
 * (ripple-ring icon + subtitle) with the [WorkersPaymentMonthRecapCard] as a sibling directly below
 * it, riding [HEADER_OVERLAP] up into the bar's bottom padding so the white card overlaps the
 * rounded gradient edge. Folds on scroll via [topAreaHide].
 */
@Composable
internal fun WorkersPaymentDetailHeader(
    item: WorkersPaymentInfoPR,
    onBack: () -> Unit,
    topAreaState: TopAreaState,
    onInfoClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val gradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = stringResource(Res.string.workers_payment_detail_title),
            background = gradient,
            bottomPadding = Spacing.page + HEADER_OVERLAP,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(Res.string.action_back),
                    onClick = onBack,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_info),
                    contentDescription = stringResource(Res.string.workers_payment_info_sheet_title),
                    onClick = onInfoClicked,
                )
            },
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .topAreaHide(topAreaState)
                        .padding(horizontal = Spacing.page, vertical = Spacing.smPlus),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp,
                    )
                    AnimatedRingHeaderIcon(icon = Icons.Outlined.Engineering)
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = stringResource(Res.string.workers_payment_subtitle),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textHeaderSubtitle,
                    )
                }
            }
        }

        WorkersPaymentMonthRecapCard(
            item = item,
            modifier = Modifier
                .fillMaxWidth()
                .rideUpIntoHeader(
                    progress = topAreaState.progressProvider,
                    expandedOverlap = HEADER_OVERLAP,
                    collapsedOverlap = HEADER_OVERLAP,
                )
                .padding(horizontal = Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentDetailHeaderPreview() {
    PreviewRtlThemeContent {
        WorkersPaymentDetailHeader(
            item = workersPaymentPreviewItem(),
            onBack = {},
            topAreaState = rememberTopAreaState(expandedHeight = 300.dp, collapsedHeight = 96.dp),
            onInfoClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentDetailHeaderPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        WorkersPaymentDetailHeader(
            item = workersPaymentPreviewItem(),
            onBack = {},
            topAreaState = rememberTopAreaState(expandedHeight = 300.dp, collapsedHeight = 96.dp),
            onInfoClicked = {},
        )
    }
}
