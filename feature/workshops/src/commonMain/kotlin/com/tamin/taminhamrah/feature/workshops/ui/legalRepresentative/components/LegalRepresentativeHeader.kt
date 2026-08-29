package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.legal_representative_full_name_label
import taminx.core.core_ui.legal_representative_title
import taminx.core.core_ui.legal_representative_workshop_stat_label

/**
 * The gradient hero bar shared by every screen in this flow — same structure as
 * `OrotezProtezHeader` (`feature:orotez-protez`): an outer rounded-bottom column painted with
 * `profileGradientStops` (navy), holding the [TaminTopAppBar] plus centered [content] below it,
 * rather than `TaminTopAppBar`'s own default teal `content` slot.
 */
@Composable
internal fun LegalRepresentativeHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    /** Extra blue space left below [content] for a [LegalRepresentativeIdentitySummaryCard] to ride up into. */
    heroCardOverlap: Dp = Spacing.none,
    content: @Composable () -> Unit = {},
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
            .padding(bottom = Spacing.smPlus + heroCardOverlap),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.legal_representative_title),
            background = gradient,
            bottomPadding = Spacing.smPlus,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                    bordered = true,
                )
            },
        )
        DecorativeBackgroundCircle(
            size = 190.dp,
            xOffset = 250.dp,
            yOffset = (-150).dp
        )
        content()
    }
}

/** The hub screen's own header content: an icon tile over a muted caption. */
@Composable
internal fun LegalRepresentativeHeroSubtitle(text: String) {
    val taminColors = LocalTaminColors.current
    AnimatedRingHeaderIcon(icon = Icons.Filled.Person)
    Spacer(Modifier.height(Spacing.sm))
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = taminColors.textHeaderSubtitle,
    )
}

/** A small card summarizing the workshop, embedded in the header on every later screen. */
@Composable
internal fun LegalRepresentativeWorkshopSummaryCard(
    workshopName: String,
    subtitle: String,
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg)
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(taminColors.glassIconTileBg)
            .padding(Spacing.md),
    ) {
        Text(
            text = workshopName,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = taminColors.textHeaderSubtitle,
        )
    }
}

/**
 * The hub screen's identity card — the user's own name and their legal-representative workshop
 * count, side by side with a divider. Composed as the first item of a block the caller offsets up
 * by [LegalRepresentativeHeader]'s `heroCardOverlap`, so it rides up to straddle the header seam.
 */
@Composable
internal fun LegalRepresentativeIdentitySummaryCard(
    fullName: String?,
    workshopCount: Int,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (fullName == null) {
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.titleWidth)
                        .height(ShimmerSize.titleHeight),
                )
            } else {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textPrimary,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                text = stringResource(Res.string.legal_representative_full_name_label),
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted,
                textAlign = TextAlign.Center,
            )
        }
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(Spacing.xxl)
                .background(taminColors.border),
        )
        Column(
            modifier = Modifier.padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NumericText(
                text = workshopCount.toString(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                text = stringResource(Res.string.legal_representative_workshop_stat_label),
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted,
            )
        }
    }
}
