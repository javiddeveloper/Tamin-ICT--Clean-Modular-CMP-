package com.tamin.taminhamrah.feature.profile.ui.contactUs.components

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
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contact_us_subtitle
import taminx.core.core_ui.contact_us_title
import taminx.core.core_ui.ic_support
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
internal fun ContactUsHeader(
    onBackClicked: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    val topBarGradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    val title = stringResource(Res.string.contact_us_title)
    val subtitle = stringResource(Res.string.contact_us_subtitle)

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
                    icon = vectorResource(Res.drawable.ic_support),
                    animated = !topAreaState.isMeasureProbe
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = taminColors.textHeaderSubtitle
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
private fun Modifier.topAreaReveal(state: TopAreaState, height: Dp): Modifier =
    layout { measurable, constraints ->
        val targetPx = height.roundToPx()
        val revealedPx = (targetPx * state.progress).roundToInt()
        val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = revealedPx.coerceAtLeast(0)))
        layout(placeable.width, revealedPx) { placeable.place(0, 0) }
    }

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewContactUsHeaderLight() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContactUsHeader(
            onBackClicked = {},
            topAreaState = com.tamin.taminhamrah.ui.toparea.rememberTopAreaState(224.dp, 64.dp)
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewContactUsHeaderDark() {
    com.tamin.taminhamrah.ui.theme.TaminHamrahTheme(darkTheme = true) {
        ContactUsHeader(
            onBackClicked = {},
            topAreaState = com.tamin.taminhamrah.ui.toparea.rememberTopAreaState(224.dp, 64.dp)
        )
    }
}
