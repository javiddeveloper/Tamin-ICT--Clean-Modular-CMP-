package com.tamin.taminhamrah.feature.treatment.ui.components
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import com.tamin.taminhamrah.ui.components.IconTile
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.startToEndGradient
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminCoverageBadgeBg
import com.tamin.taminhamrah.ui.theme.TaminCoverageBadgeFg
import com.tamin.taminhamrah.ui.theme.TaminTeal500
import com.tamin.taminhamrah.ui.theme.TaminTeal900
import com.tamin.taminhamrah.ui.theme.TaminCardAmberEnd
import com.tamin.taminhamrah.ui.theme.TaminCardAmberMid
import com.tamin.taminhamrah.ui.theme.TaminCardAmberStart
import com.tamin.taminhamrah.ui.theme.TaminCardBlueEnd
import com.tamin.taminhamrah.ui.theme.TaminCardBlueMid
import com.tamin.taminhamrah.ui.theme.TaminCardBlueStart
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleEnd
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleMid
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleStart
import com.tamin.taminhamrah.ui.theme.TaminCardTealEnd
import com.tamin.taminhamrah.ui.theme.TaminCardTealMid
import com.tamin.taminhamrah.ui.theme.TaminCardTealStart
import com.tamin.taminhamrah.util.toPersianDigits

/**
 * Components for the treatment hub landing screen: the insured-person card carousel,
 * the quick-access and navigation cards, the service category grid and the yearly
 * cost summary.
 *
 * None of these depend on a domain model — they take display strings and lambdas, so
 * they stay decoupled from whatever shape the treatment entities settle on.
 */

/** The main insured person's card: teal fading into brand blue. */
private val MainInsuredCardStops = listOf(TaminCardTealStart, TaminCardTealMid, TaminCardTealEnd)

/**
 * Dependants cycle through these so neighboring cards in the carousel never share a
 * color. Ordered as the design lists them: spouse, then children.
 */
private val DependantCardStops = listOf(
    listOf(TaminCardPurpleStart, TaminCardPurpleMid, TaminCardPurpleEnd),
    listOf(TaminCardBlueStart, TaminCardBlueMid, TaminCardBlueEnd),
    listOf(TaminCardAmberStart, TaminCardAmberMid, TaminCardAmberEnd),
)

/**
 * The card identity for one insured person. [dependantOrdinal] is the person's position
 * among the dependants only — the main insured person ignores it and always reads teal.
 */
fun insuranceCardGradient(isDependent: Boolean, dependantOrdinal: Int = 0): Brush {
    val stops = if (isDependent) {
        DependantCardStops[dependantOrdinal.mod(DependantCardStops.size)]
    } else {
        MainInsuredCardStops
    }
    return Brush.linearGradient(stops)
}

/**
 * The electronic health-insurance card for one insured person: organization branding,
 * the holder's name and national ID, and a coverage-status footer.
 */
@Composable
fun InsuranceCard(
    holderName: String,
    nationalId: String,
    coverageLabel: String,
    modifier: Modifier = Modifier,
    background: Brush = insuranceCardGradient(isDependent = false),
    coverageBadge: @Composable (() -> Unit)? = null,
    footerAction: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.cardCompact))
            .background(background)
            .cardDecoration(),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            InsuranceCardBrandRow(initial = holderName.take(1))
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = holderName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "کد ملی",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.75f),
            )
            NumericText(
                text = nationalId.toPersianDigits(),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
            )
        }
        InsuranceCardFooter(
            coverageLabel = coverageLabel,
            badge = coverageBadge,
            action = footerAction,
        )
    }
}

/**
 * The small round glyph beside the coverage line — a tick when treatment support is
 * active, a cross when it is not. Defaults to the design's mint-on-deep-green tick.
 */
@Composable
fun CoverageBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = TaminCoverageBadgeBg,
    contentColor: Color = TaminCoverageBadgeFg,
) {
    Box(
        modifier = modifier
            .size(TreatmentDimens.coverageBadgeSize)
            .background(containerColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(TreatmentDimens.coverageBadgeIconSize),
        )
    }
}


/** Soft translucent swooshes that stop the gradient card reading as a flat rectangle. */
private fun Modifier.cardDecoration(): Modifier = drawBehind {
    val decor = Color.White.copy(alpha = TreatmentDimens.cardDecorAlpha)
    drawOval(
        color = decor,
        topLeft = Offset(-size.width * 0.15f, size.height * 0.55f),
        size = Size(size.width * 0.7f, size.height * 0.8f),
    )
    drawOval(
        color = decor,
        topLeft = Offset(size.width * 0.6f, -size.height * 0.5f),
        size = Size(size.width * 0.6f, size.height * 0.7f),
    )
}

@Composable
private fun InsuranceCardBrandRow(initial: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    Color.White.copy(alpha = 0.13f),
                    RoundedCornerShape(CornerRadius.avatarTile),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "سازمان تامین اجتماعی",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
            Text(
                text = "کارت الکترونیک بیمهٔ درمان",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f),
            )
        }
        Box(
            modifier = Modifier
                .size(TreatmentDimens.brandTickSize)
                .background(Color.White.copy(alpha = 0.13f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(TreatmentDimens.brandTickIconSize),
            )
        }
    }
}


@Composable
private fun InsuranceCardFooter(
    coverageLabel: String,
    badge: @Composable (() -> Unit)?,
    action: @Composable (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        badge?.invoke()
        Text(
            text = coverageLabel,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )
        action?.invoke()
    }
}

/**
 * Snapping carousel of insured-person cards with a page-indicator strip. The
 * neighboring cards peek at the edges, matching the design's scroll-snap row.
 */
@Composable
fun InsuranceCardCarousel(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    card: @Composable (page: Int) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            // Each card occupies TreatmentDimens.cardPeekFraction of the viewport and stays centred,
            // so the neighboring cards peek evenly on both edges.
            val sidePadding = maxWidth * (1 - TreatmentDimens.cardPeekFraction) / 2
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = sidePadding),
                pageSpacing = Spacing.cardGap,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                card(page)
            }
        }
        if (pageCount > 1) {
            PageIndicator(
                pageCount = pageCount,
                selectedPage = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
            )
        }
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { page ->
            val isSelected = page == selectedPage
            Box(
                modifier = Modifier
                    .size(width = if (isSelected) 16.dp else 6.dp, height = 6.dp)
                    .background(
                        color = if (isSelected) colors.teal else colors.chevron,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

/**
 * The quick-access card's wash, sweeping right to left: deep teal under the title on the
 * start edge, brightening toward the end edge.
 *
 * The design writes this as `135deg, #2FB9BC → #0E7C82`, but a CSS angle is absolute and
 * ignores direction, so reading its stop order literally mirrors the card under RTL.
 */
@Composable
fun quickAccessGradient(): Brush = startToEndGradient(listOf(TaminTeal900, TaminTeal500))

/**
 * The prominent gradient entry point at the top of the quick-access group —
 * "سوابق درمانی من".
 */
@Composable
fun QuickAccessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Brush = quickAccessGradient(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(background)
            .clickable(onClick = onClick)
            .padding(Spacing.page),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        IconTile(
            icon = icon,
            tint = Color.White,
            background = Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.1f)),
            ),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
            )
        }
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(IconSize.medium),
        )
    }
}

/**
 * Surface-level row card used for the secondary hub destinations — health profile,
 * contracted centers — with an optional status pill before the chevron.
 */
@Composable
fun TreatmentNavigationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Brush,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    status: @Composable (() -> Unit)? = null,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        IconTile(icon = icon, tint = iconTint, background = iconBackground)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }
        status?.invoke()
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = colors.chevron,
            modifier = Modifier.size(IconSize.medium),
        )
    }
}

/**
 * One square tile in the hub's three-up service grid — electronic prescriptions,
 * medical confirmations, miscellaneous claims.
 */
@Composable
fun CategoryTile(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .taminSurface(CornerRadius.cardCompact)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.sm, vertical = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        IconTile(
            icon = icon,
            tint = iconTint,
            background = iconBackground,
            size = 40.dp,
            cornerRadius = CornerRadius.lg,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Yearly spend summary: the insured person's share beside the organization's share.
 */
@Composable
fun CostSummaryCard(
    title: String,
    insuredShareLabel: String,
    insuredShareAmount: String,
    organizationShareLabel: String,
    organizationShareAmount: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = colors.textPrimary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StatTile(
                label = insuredShareLabel,
                amount = insuredShareAmount,
                containerColor = colors.greenBg,
                contentColor = colors.greenText,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = organizationShareLabel,
                amount = organizationShareAmount,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
