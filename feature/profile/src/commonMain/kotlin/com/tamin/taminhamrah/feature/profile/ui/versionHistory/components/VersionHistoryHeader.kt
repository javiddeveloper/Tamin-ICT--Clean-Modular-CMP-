package com.tamin.taminhamrah.feature.profile.ui.versionHistory.components

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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import com.tamin.taminhamrah.ui.toparea.topAreaReveal
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_history
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.profile_version_history
import taminx.core.core_ui.profile_version_history_last_updated

@Composable
internal fun VersionHistoryHeader(
    lastUpdatedDate: String,
    onBackClicked: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    val topBarGradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomEnd = 40.dp,
                    bottomStart = 40.dp
                )
            )
            .background(taminTopAppBarGradient(taminColors.profileGradientStops)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.profile_version_history),
            centerTitle = true,
            background = topBarGradient,
            bottomPadding = Spacing.none,
            navigationIcon = {
                TaminTopAppBarButton(
                    bordered = true,
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                    modifier = Modifier
                )
            }
        )

        // Only the expanded-state furniture below the title row folds away; it must span the
        // full 0..1 progress range so the header's height tracks the drag exactly.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaHide(topAreaState)
                .padding(bottom = Spacing.xl)
        ) {
            DecorativeBackgroundCircle(
                size = 190.dp,
                xOffset = 450.dp,
                yOffset = (-150).dp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedRingHeaderIcon(
                    icon = vectorResource(Res.drawable.ic_history),
                    animated = !topAreaState.isMeasureProbe
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = stringResource(Res.string.profile_version_history_last_updated, lastUpdatedDate),
                    style = MaterialTheme.typography.labelLarge,
                    color = taminColors.textHeaderSubtitle
                )
            }
        }

        // Breathing room below the collapsed bar's title row, zero while expanded.
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaReveal(topAreaState, CollapsedBottomSpace)
        )
    }
}

private val CollapsedBottomSpace = 20.dp

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewVersionHistoryHeader() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        VersionHistoryHeader(
            lastUpdatedDate = "۳۰ فروردین ۱۴۰۵",
            onBackClicked = {},
            topAreaState = com.tamin.taminhamrah.ui.toparea.rememberTopAreaState(224.dp, 64.dp)
        )
    }
}
