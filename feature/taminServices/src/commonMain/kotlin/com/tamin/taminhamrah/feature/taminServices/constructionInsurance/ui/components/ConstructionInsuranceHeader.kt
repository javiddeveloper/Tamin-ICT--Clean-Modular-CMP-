package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.rememberTopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.btn_search
import taminx.core.core_ui.construction_insurance_subtitle
import taminx.core.core_ui.construction_insurance_title
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.notice_title

/**
 * The navy gradient header of the بیمه ساختمانی list screen. Folds on the list's own drag through
 * the TopArea system (`docs/vault/TopArea-System.md`) — same shape as
 * `EmployerOnlineServicesHeader`: [topAreaState] drives the ring icon + subtitle away (and out of
 * the layout) as the list scrolls up, snapping to fully-open / fully-closed on release.
 * [heroCardOverlap] leaves that much extra gradient below the content for
 * [ConstructionUserInfoCard] to ride up into, straddling the seam.
 */
@Composable
internal fun ConstructionInsuranceHeader(
    onBackClicked: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
    heroCardOverlap: Dp = Spacing.none,
    onSearchClicked: () -> Unit = {},
    onInfoClicked: () -> Unit = {},
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(gradient)
            .padding(bottom = heroCardOverlap),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.construction_insurance_title),
            background = gradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(Res.string.action_back),
                    onClick = onBackClicked,
                    bordered = true,
                )
            },
            action = {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_info),
                        contentDescription = stringResource(Res.string.notice_title),
                        onClick = onInfoClicked,
                        bordered = true,
                    )
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_search),
                        contentDescription = stringResource(Res.string.btn_search),
                        onClick = onSearchClicked,
                        bordered = true,
                    )
                }
            },
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                DecorativeBackgroundCircle(
                    size = 190.dp,
                    xOffset = 450.dp,
                    yOffset = (-150).dp,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .topAreaHide(topAreaState)
                        .padding(horizontal = Spacing.page, vertical = Spacing.smPlus),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Rendered statically while this header is one of rememberMeasuredTopAreaState's
                    // off-screen measure probes — its size doesn't depend on the ring animation.
                    AnimatedRingHeaderIcon(
                        icon = Icons.Outlined.Home,
                        animated = !topAreaState.isMeasureProbe,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.construction_insurance_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceHeaderPreviewLight() {
    PreviewRtlThemeContent {
        ConstructionInsuranceHeader(
            onBackClicked = {},
            topAreaState = rememberTopAreaState(expandedHeight = 260.dp, collapsedHeight = 96.dp),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceHeaderPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ConstructionInsuranceHeader(
            onBackClicked = {},
            topAreaState = rememberTopAreaState(expandedHeight = 260.dp, collapsedHeight = 96.dp),
        )
    }
}
