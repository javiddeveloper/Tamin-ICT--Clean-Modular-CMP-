package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.collapseAway
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.active_relation_header_active_count
import taminx.core.core_ui.active_relation_header_check_time
import taminx.core.core_ui.active_relation_header_inactive_count
import taminx.core.core_ui.active_relation_header_status_error
import taminx.core.core_ui.active_relation_header_status_ok
import taminx.core.core_ui.ic_communication
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.profile_active_relation

@Composable
internal fun ActiveRelationHeader(
    activeCount: Int,
    inactiveCount: Int,
    lastCheckTime: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    collapseProgress: () -> Float = { 0f },
) {
    val taminColors = LocalTaminColors.current
    val isDark = taminColors == DarkTaminColors

    val topBarGradient =
        remember(isDark) { Brush.horizontalGradient(taminColors.profileGradientStops) }

    // Title fades out and the status text fades in over the same title-row spot, so
    // scrolling reads as the status taking over the title's place rather than two
    // unrelated labels swapping. Sequential (not overlapping) so the RTL glyphs never
    // sit half-opaque on top of each other mid-fade.
    val title = stringResource(Res.string.profile_active_relation)
    val statusText = if (activeCount > 0) stringResource(Res.string.active_relation_header_status_ok) else stringResource(Res.string.active_relation_header_status_error)
    val statusColor =
        if (activeCount > 0) taminColors.springGreenText else taminColors.dangerText
    val titleAlpha = { (1f - collapseProgress() * 2f).coerceIn(0f, 1f) }
    val collapsedAlpha = { ((collapseProgress() - 0.5f) * 2f).coerceIn(0f, 1f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomEnd = CornerRadius.chip,
                    bottomStart = CornerRadius.chip
                )
            )
            .background(taminTopAppBarGradient(taminColors.profileGradientStops)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TaminTopAppBar(
            title = title,
            centerTitle = true,
            background = topBarGradient,
            titleContent = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = titleAlpha() },
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = collapsedAlpha() },
                    )
                }
            },
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                    modifier = Modifier
                )
            },
            // The header icon fades out below as this fades in here, on the same schedule
            // as the title/status handoff, so it reads as the icon moving up into the bar.
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_communication),
                    contentDescription = null,
                    onClick = {},
                    modifier = Modifier.graphicsLayer { alpha = collapsedAlpha() },
                )
            },
        )

        // Only the expanded-state furniture below the title row folds away; the title
        // row itself stays put so the bar reads the same as the rest of the app once collapsed.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .collapseAway(collapseProgress)
                .padding(bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.md))

            AnimatedRingHeaderIcon(
                icon = vectorResource(Res.drawable.ic_communication)
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.padding(bottom = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (activeCount > 0) taminColors.springGreenText else taminColors.textMuted,
                            RoundedCornerShape(50)
                        )
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            val activeText = stringResource(Res.string.active_relation_header_active_count, activeCount.toString().toPersianDigits())
            val inactiveText = stringResource(Res.string.active_relation_header_inactive_count, inactiveCount.toString().toPersianDigits())
            val checkTimeText = stringResource(Res.string.active_relation_header_check_time, lastCheckTime)

            Text(
                text = "$activeText · $inactiveText · $checkTimeText",
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.txtNatProfile
            )
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewActiveRelationHeader() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ActiveRelationHeader(
            activeCount = 1,
            inactiveCount = 0,
            lastCheckTime = "۱۰:۲۴",
            onBackClicked = {}
        )
    }
}
