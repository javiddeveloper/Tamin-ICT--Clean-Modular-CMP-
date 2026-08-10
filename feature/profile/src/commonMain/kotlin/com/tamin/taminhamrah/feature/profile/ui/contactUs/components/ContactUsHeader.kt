package com.tamin.taminhamrah.feature.profile.ui.contactUs.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.collapseAway
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contact_us_subtitle
import taminx.core.core_ui.contact_us_title
import taminx.core.core_ui.ic_support
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun ContactUsHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    collapseProgress: () -> Float = { 0f },
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 40.dp,
                    bottomEnd = 40.dp
                )
            )
            .background(profileGradientBrush)
    ) {
        // Title fades out and the subtitle fades in over the same title-row spot, so
        // scrolling reads as the subtitle taking over the title's place rather than two
        // unrelated labels swapping. Sequential (not overlapping) so the RTL glyphs
        // never sit half-opaque on top of each other mid-fade.
        val title = stringResource(Res.string.contact_us_title)
        val subtitle = stringResource(Res.string.contact_us_subtitle)
        val titleAlpha = { (1f - collapseProgress() * 2f).coerceIn(0f, 1f) }
        val collapsedAlpha = { ((collapseProgress() - 0.5f) * 2f).coerceIn(0f, 1f) }

        TaminTopAppBar(
            title = title,
            background = profileGradientBrush,
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
                        text = subtitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
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
                    bordered = true
                )
            },
            // The header icon fades out below as this fades in here, on the same schedule
            // as the title/subtitle handoff, so it reads as the icon moving up into the bar.
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_support),
                    contentDescription = null,
                    onClick = {},
                    modifier = Modifier.graphicsLayer { alpha = collapsedAlpha() },
                )
            }
        )

        // Only the expanded-state icon below folds away; the title row itself stays put
        // so the bar reads the same as the rest of the app once collapsed.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .collapseAway(collapseProgress)
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
                AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_support))
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = taminColors.textHeaderSubtitle
                )
            }
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewContactUsHeaderLight() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContactUsHeader(onBackClicked = {})
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewContactUsHeaderDark() {
    com.tamin.taminhamrah.ui.theme.TaminHamrahTheme(darkTheme = true) {
        ContactUsHeader(onBackClicked = {})
    }
}
