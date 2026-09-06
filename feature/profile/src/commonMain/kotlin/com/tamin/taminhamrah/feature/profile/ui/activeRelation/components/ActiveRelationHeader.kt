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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaAlpha
import com.tamin.taminhamrah.ui.toparea.topAreaHide
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
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val isDark = taminColors == DarkTaminColors

    val topBarGradient =
        remember(isDark) { Brush.horizontalGradient(taminColors.profileGradientStops) }

    // Trims the font's built-in leading above/below each line so the status badge and the
    // stats line beneath it sit close together instead of the extra line-height padding.
    val trimmedLineHeight = remember {
        LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.Both)
    }

    val title = stringResource(Res.string.profile_active_relation)
    val statusText = if (activeCount > 0) stringResource(Res.string.active_relation_header_status_ok) else stringResource(Res.string.active_relation_header_status_error)
    val statusColor =
        if (activeCount > 0) taminColors.springGreenText else taminColors.dangerText

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
            title = title,
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

        // Only the expanded-state furniture below the title row folds away; the title
        // row itself stays put so the bar reads the same as the rest of the app once collapsed.
        // This shrinks the column's own measured height, which is what makes the header's total
        // rendered height track the drag -- so it must span the full 0..1 progress range (the
        // same range maxOffsetPx models), never a narrower one, or the header visibly collapses
        // faster than the finger.
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
                icon = vectorResource(Res.drawable.ic_communication),
                animated = !topAreaState.isMeasureProbe
            )
            Row(
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
                    style = MaterialTheme.typography.titleMedium.copy(lineHeightStyle = trimmedLineHeight),
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            val activeText = stringResource(Res.string.active_relation_header_active_count, activeCount.toString().toPersianDigits())
            val inactiveText = stringResource(Res.string.active_relation_header_inactive_count, inactiveCount.toString().toPersianDigits())
            val checkTimeText = stringResource(Res.string.active_relation_header_check_time, lastCheckTime)
            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = "$activeText · $inactiveText · $checkTimeText",
                style = MaterialTheme.typography.bodySmall.copy(lineHeightStyle = trimmedLineHeight),
                color = taminColors.txtNatProfile
            )
        }
        }

        // Zero height while expanded, growing to CollapsedBottomSpace as the header folds --
        // the inverse of topAreaHide above. Gives the collapsed bar breathing room below its
        // title row without adding to the expanded gap between the title and the ring icon.
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaReveal(topAreaState, CollapsedBottomSpace)
        )
    }
}

private val CollapsedBottomSpace = 20.dp

/** Grows a child from zero up to [height] as [state] folds -- the inverse of [topAreaHide]. */
private fun Modifier.topAreaReveal(state: TopAreaState, height: androidx.compose.ui.unit.Dp): Modifier =
    layout { measurable, constraints ->
        val targetPx = height.roundToPx()
        val revealedPx = (targetPx * state.progress).roundToInt()
        val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = revealedPx.coerceAtLeast(0)))
        layout(placeable.width, revealedPx) { placeable.place(0, 0) }
    }

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewActiveRelationHeader() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ActiveRelationHeader(
            activeCount = 1,
            inactiveCount = 0,
            lastCheckTime = "۱۰:۲۴",
            onBackClicked = {},
            topAreaState = com.tamin.taminhamrah.ui.toparea.rememberTopAreaState(224.dp, 64.dp)
        )
    }
}
