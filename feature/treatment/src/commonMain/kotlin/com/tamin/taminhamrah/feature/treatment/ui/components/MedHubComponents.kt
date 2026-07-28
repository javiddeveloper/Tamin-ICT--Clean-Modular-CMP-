package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.ui.components.AutoResizeText
import com.tamin.taminhamrah.ui.components.IconTile
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.startToEndGradient
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
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
import com.tamin.taminhamrah.ui.theme.TaminCoverageBadgeBg
import com.tamin.taminhamrah.ui.theme.TaminCoverageBadgeFg
import com.tamin.taminhamrah.ui.theme.TaminTeal500
import com.tamin.taminhamrah.ui.theme.TaminTeal900
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_ejtemaei_logo

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
 * Dependants cycle through these, so a person with more of them than the design has colors still
 * gets a card: the palette repeats every three, which is enough to keep neighboring cards in the
 * carousel apart. Ordered as the design lists them: spouse, then children.
 */
private val DependantCardStops = listOf(
    listOf(TaminCardPurpleStart, TaminCardPurpleMid, TaminCardPurpleEnd),
    listOf(TaminCardBlueStart, TaminCardBlueMid, TaminCardBlueEnd),
    listOf(TaminCardAmberStart, TaminCardAmberMid, TaminCardAmberEnd),
)

/**
 * The card identity for one insured person. [dependantOrdinal] is the person's position
 * among the dependants only — the main insured person ignores it and always reads teal.
 *
 * Any ordinal is valid: it wraps, so the count of dependants is never a constraint.
 */
fun insuranceCardGradient(isDependent: Boolean, dependantOrdinal: Int = 0): Brush {
    val stops = if (isDependent) {
        DependantCardStops[dependantOrdinal.mod(DependantCardStops.size)]
    } else {
        MainInsuredCardStops
    }
    return Brush.linearGradient(stops)
}

/** The prominent teal gradient behind the "سوابق درمانی من" quick-access card. */
@Composable
fun quickAccessGradient(): Brush = startToEndGradient(listOf(TaminTeal900, TaminTeal500))

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
    // 0 shows the full card; as it runs to 1 the card cross-fades into a compact
    // tick + name + national-code bar and shrinks to that height. Read only inside
    // layout/draw lambdas, so the morph never recomposes the card.
    collapseProgress: () -> Float = { 0f },
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.cardCompact))
            .background(background)
            .cardDecoration(),
    ) {
        Layout(
            content = {
                InsuranceCardBrandRow(
                    modifier = Modifier
                        .layoutId(CardSlot.Brand)
                        .vanishOnCollapse(collapseProgress),
                )
                Text(
                    text = holderName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .layoutId(CardSlot.Name)
                        .shrinkOnCollapse(collapseProgress, rtl),
                )
                Text(
                    text = "کد ملی",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier
                        .layoutId(CardSlot.CodeLabel)
                        .vanishOnCollapse(collapseProgress),
                )
                NumericText(
                    text = nationalId.toPersianDigits(),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.layoutId(CardSlot.Code),
                )
                // Wrapped so the slot exists even when there is no badge to show.
                Box(Modifier.layoutId(CardSlot.Badge)) { coverageBadge?.invoke() }
                InsuranceCardFooter(
                    coverageLabel = coverageLabel,
                    badge = coverageBadge,
                    action = footerAction,
                    modifier = Modifier
                        .layoutId(CardSlot.Footer)
                        .vanishOnCollapse(collapseProgress),
                )
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val pad = Spacing.lg.roundToPx()
            val md = Spacing.md.roundToPx()
            val xs = Spacing.xs.roundToPx()
            val sm = Spacing.sm.roundToPx()
            val innerC = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))

            val brand = measurables.slot(CardSlot.Brand).measure(innerC)
            val name = measurables.slot(CardSlot.Name).measure(innerC)
            val label = measurables.slot(CardSlot.CodeLabel).measure(innerC)
            val number = measurables.slot(CardSlot.Code).measure(innerC)
            val badge = measurables.slot(CardSlot.Badge).measure(Constraints())
            val footer = measurables.slot(CardSlot.Footer).measure(Constraints.fixedWidth(width))

            // Expanded slots (start-offset from the start edge, top from the card top).
            val nameExpTop = pad + brand.height + md
            val labelExpTop = nameExpTop + name.height + xs
            val numberExpTop = labelExpTop + label.height
            val footerTop = numberExpTop + number.height + pad
            val badgeExpTop = footerTop + (footer.height - badge.height) / 2
            val expandedH = footerTop + footer.height

            // Collapsed slots — a compact bar of tick + name + code (slightly larger).
            val barH = maxOf(badge.height, name.height, number.height) +
                TreatmentDimens.cardBarPadding.roundToPx()
            val badgeColTop = (barH - badge.height) / 2
            val nameColStart = pad + badge.width + sm
            val nameColTop = (barH - name.height) / 2
            // The name shrinks in the bar, so the code sits just past its scaled width.
            val numberColStart = nameColStart + (name.width * NameCollapsedScale).toInt() + sm
            val numberColTop = (barH - number.height) / 2

            val t = CardCollapseEasing.transform(collapseProgress())

            layout(width, lerp(expandedH, barH, t)) {
                // Fading pieces stay at their expanded spots (and clip as the card shrinks).
                brand.placeRelative(pad, pad)
                label.placeRelative(pad, labelExpTop)
                footer.placeRelative(0, footerTop)
                // Traveling pieces glide from their expanded slot to their bar slot.
                name.placeRelative(lerp(pad, nameColStart, t), lerp(nameExpTop, nameColTop, t))
                number.placeRelative(lerp(pad, numberColStart, t), lerp(numberExpTop, numberColTop, t))
                badge.placeRelative(pad, lerp(badgeExpTop, badgeColTop, t))
            }
        }
    }
}

/** The pieces of the card's expanded → collapsed morph, addressed by name rather than by index. */
private enum class CardSlot { Brand, Name, CodeLabel, Code, Badge, Footer }

private fun List<Measurable>.slot(id: CardSlot): Measurable = first { it.layoutId == id }

private val CardCollapseEasing = FastOutSlowInEasing

/** How far the holder name shrinks by the time the card is a compact bar. */
private const val NameCollapsedScale = 0.88f

/**
 * Fades a piece out over the first half of the fold, so the vanishing pieces have cleared before
 * the compact bar forms. [progress] is read inside the draw lambda — no recomposition per frame.
 */
private fun Modifier.vanishOnCollapse(progress: () -> Float): Modifier = graphicsLayer {
    alpha = (1f - progress() * 2f).coerceIn(0f, 1f)
}

/** Shrinks the holder name as it travels, anchored to its start edge so it stays put in the bar. */
private fun Modifier.shrinkOnCollapse(progress: () -> Float, rtl: Boolean): Modifier = graphicsLayer {
    val scale = lerp(1f, NameCollapsedScale, CardCollapseEasing.transform(progress()))
    scaleX = scale
    scaleY = scale
    transformOrigin = TransformOrigin(if (rtl) 1f else 0f, 0.5f)
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
private fun InsuranceCardBrandRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(TreatmentDimens.brandTileSize)
                .background(
                    Color.White.copy(alpha = 0.13f),
                    RoundedCornerShape(CornerRadius.avatarTile),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_ejtemaei_logo),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(TreatmentDimens.brandTileIconSize),
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
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
                pagerState = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
            )
        }
    }
}

/**
 * Takes the [pagerState] rather than the current page so that swiping recomposes the dots only —
 * reading `currentPage` in the carousel above would recompose the pager and every card with it.
 *
 * Scrolls rather than wraps or clips: a person can have more dependants than a row of dots fits,
 * and the strip keeps the active one in view instead of running off the edge. It centres itself
 * while the dots do fit, so the common two- or three-card case looks exactly as before.
 */
@Composable
private fun PageIndicator(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val selectedPage = pagerState.currentPage
    val listState = rememberLazyListState()

    LaunchedEffect(selectedPage) {
        listState.animateScrollToItem(selectedPage)
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        // The pager owns the gesture; these are a read-out, not a second way to page.
        userScrollEnabled = false,
    ) {
        items(pageCount) { page ->
            val isSelected = page == selectedPage
            Box(
                modifier = Modifier
                    .size(
                        width = if (isSelected) SelectedDotWidth else DotSize,
                        height = DotSize,
                    )
                    .background(
                        color = if (isSelected) colors.teal else colors.chevron,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

private val DotSize = 6.dp
private val SelectedDotWidth = 16.dp

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
        AutoResizeText(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xxs),
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
