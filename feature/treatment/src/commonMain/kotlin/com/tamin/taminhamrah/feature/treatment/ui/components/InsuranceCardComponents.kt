package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.feature.treatment.ui.model.CoverageStatus
import com.tamin.taminhamrah.feature.treatment.ui.model.PatientItemPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminPageIndicator
import com.tamin.taminhamrah.ui.components.cssAngleGradient
import com.tamin.taminhamrah.ui.components.shrinkOnCollapse
import com.tamin.taminhamrah.ui.components.vanishOnCollapse
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
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
import com.tamin.taminhamrah.ui.theme.TaminCoverageRejectedBadgeBg
import com.tamin.taminhamrah.ui.theme.TaminCoverageRejectedBadgeFg
import com.tamin.taminhamrah.ui.theme.TaminInsuranceCardChipBg
import com.tamin.taminhamrah.ui.theme.TaminInsuranceCardDivider
import com.tamin.taminhamrah.ui.theme.TaminInsuranceCardInk
import com.tamin.taminhamrah.ui.theme.TaminInsuranceCardInkMuted
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.theme.insuranceCardTextStyles
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.card_national_code
import taminx.core.core_ui.card_org_name
import taminx.core.core_ui.card_subtitle
import taminx.core.core_ui.coverage_covered
import taminx.core.core_ui.coverage_pending
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_verified

/**
 * The electronic health-insurance card and everything that dresses one: its gradient identity,
 * the expanded → collapsed morph, the coverage badge and footer, and the snapping carousel that
 * pages between insured people.
 *
 * Kept apart from the hub's sections so that the card's layout maths lives beside the card rather
 * than among the screen's stacked blocks.
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
 * `linear-gradient(120deg, … 0%, … 55%, … 100%)`. The middle stop is at 55%, not halfway: it holds
 * the teal across most of the card and turns to blue only near the trailing corner. Spreading the
 * three evenly washes the whole card blue-green instead.
 */
private const val CARD_GRADIENT_ANGLE_DEG = 120f
private const val CARD_GRADIENT_MID_STOP = 0.55f

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
    return cssAngleGradient(
        angleDeg = CARD_GRADIENT_ANGLE_DEG,
        colorStops = listOf(
            0f to stops[0],
            CARD_GRADIENT_MID_STOP to stops[1],
            1f to stops[2],
        ),
    )
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
    // 0 shows the full card; as it runs to 1 the card cross-fades into a compact
    // tick + name + national-code bar and shrinks to that height. Read only inside
    // layout/draw lambdas, so the morph never recomposes the card.
    collapseProgress: () -> Float = { 0f },
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val text = insuranceCardTextStyles()
    // The tile beside the branding shows who the card belongs to. Derived here rather than carried
    // on the model: it is a rendering of the name, not a second field that could disagree with it.
    val initial = remember(holderName) { holderName.trim().take(1) }
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
                    initial = initial,
                    modifier = Modifier
                        .layoutId(CardSlot.Brand)
                        .vanishOnCollapse(collapseProgress),
                )
                Text(
                    text = holderName,
                    style = text.holderName,
                    color = TaminInsuranceCardInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .layoutId(CardSlot.Name)
                        .shrinkOnCollapse(
                            progress = collapseProgress,
                            minScale = TreatmentDimens.cardNameCollapsedScale,
                            rtl = rtl,
                        ),
                )
                Text(
                    text = stringResource(Res.string.card_national_code),
                    style = text.codeLabel,
                    color = TaminInsuranceCardInkMuted,
                    modifier = Modifier
                        .layoutId(CardSlot.CodeLabel)
                        .vanishOnCollapse(collapseProgress),
                )
                NumericText(
                    text = nationalId.toPersianDigits(),
                    style = text.code,
                    color = TaminInsuranceCardInk,
                    modifier = Modifier.layoutId(CardSlot.Code),
                )
                // Wrapped so the slot exists even when there is no badge to show.
                Box(Modifier.layoutId(CardSlot.Badge)) { coverageBadge?.invoke() }
                InsuranceCardFooter(
                    coverageLabel = coverageLabel,
                    // Only the traveling slot above ever draws the tick. The footer just reserves
                    // its width so the label keeps its place — drawing it here as well would show
                    // two ticks the moment the fold starts, one gliding up and one fading out.
                    badge = coverageBadge?.let { { Spacer(Modifier.size(TreatmentDimens.coverageBadgeSize)) } },
                    action = footerAction,
                    modifier = Modifier
                        .layoutId(CardSlot.Footer)
                        .vanishOnCollapse(collapseProgress),
                )
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            // `padding: 14px 16px 12px` — the card is not padded evenly, so the top, the sides
            // and the bottom are three numbers rather than one.
            val pad = TreatmentDimens.cardPaddingHorizontal.roundToPx()
            val padTop = TreatmentDimens.cardPaddingTop.roundToPx()
            val padBottom = TreatmentDimens.cardPaddingBottom.roundToPx()
            val nameGap = TreatmentDimens.cardNameTopGap.roundToPx()
            val codeLabelGap = TreatmentDimens.cardCodeLabelTopGap.roundToPx()
            val codeGap = TreatmentDimens.cardCodeTopGap.roundToPx()
            val sm = Spacing.sm.roundToPx()
            val innerC = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))

            val brand = measurables.slot(CardSlot.Brand).measure(innerC)
            val name = measurables.slot(CardSlot.Name).measure(innerC)
            val label = measurables.slot(CardSlot.CodeLabel).measure(innerC)
            val number = measurables.slot(CardSlot.Code).measure(innerC)
            val badge = measurables.slot(CardSlot.Badge).measure(Constraints())
            val footer = measurables.slot(CardSlot.Footer).measure(Constraints.fixedWidth(width))

            // Expanded slots (start-offset from the start edge, top from the card top).
            val nameExpTop = padTop + brand.height + nameGap
            val labelExpTop = nameExpTop + name.height + codeLabelGap
            val numberExpTop = labelExpTop + label.height + codeGap
            val footerTop = numberExpTop + number.height + padBottom
            val badgeExpTop = footerTop + (footer.height - badge.height) / 2
            val expandedH = footerTop + footer.height

            // Collapsed slots — a compact bar of tick + name + code (slightly larger).
            val barH = maxOf(badge.height, name.height, number.height) +
                TreatmentDimens.cardBarPadding.roundToPx()
            val badgeColTop = (barH - badge.height) / 2
            val nameColStart = pad + badge.width + sm
            val nameColTop = (barH - name.height) / 2
            // The name shrinks in the bar, so the code sits just past its scaled width.
            val numberColStart =
                nameColStart + (name.width * TreatmentDimens.cardNameCollapsedScale).toInt() + sm
            val numberColTop = (barH - number.height) / 2

            val t = Easing.standard.transform(collapseProgress())

            layout(width, lerp(expandedH, barH, t)) {
                // Fading pieces stay at their expanded spots (and clip as the card shrinks).
                brand.placeRelative(pad, padTop)
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

/*
 * The card's decoration is an SVG overlay in the design, drawn on a `viewBox="0 0 340 130"` with
 * `preserveAspectRatio="none"` and stretched over the whole card — so every coordinate below is
 * divided by 340 on x and 130 on y and multiplied back up by the card's real size.
 *
 * It is three shapes, not two, and they carry three different alphas. The swoosh down the
 * trailing edge is the one that reads as "card" rather than "rectangle"; the ellipses only soften
 * the corners behind it.
 */
private const val DECOR_VIEWPORT_WIDTH = 340f
private const val DECOR_VIEWPORT_HEIGHT = 130f

/** Soft translucent swooshes that stop the gradient card reading as a flat rectangle. */
private fun Modifier.cardDecoration(): Modifier = drawWithCache {
    val sx = size.width / DECOR_VIEWPORT_WIDTH
    val sy = size.height / DECOR_VIEWPORT_HEIGHT

    // M340 0 C 300 25, 330 65, 300 100 C 275 130, 340 130, 340 130 L 340 0 Z
    // Cached rather than rebuilt per frame: it only changes when the card is measured again.
    val swoosh = Path().apply {
        moveTo(340f * sx, 0f)
        cubicTo(300f * sx, 25f * sy, 330f * sx, 65f * sy, 300f * sx, 100f * sy)
        cubicTo(275f * sx, 130f * sy, 340f * sx, 130f * sy, 340f * sx, 130f * sy)
        close()
    }

    onDrawBehind {
        drawPath(
            path = swoosh,
            color = TaminInsuranceCardInk.copy(alpha = TreatmentDimens.cardDecorSwooshAlpha),
        )
        // <ellipse cx="60" cy="120" rx="110" ry="45" fill="#ffffff0d" />
        drawOval(
            color = TaminInsuranceCardInk.copy(alpha = TreatmentDimens.cardDecorLowerAlpha),
            topLeft = Offset(-50f * sx, 75f * sy),
            size = Size(220f * sx, 90f * sy),
        )
        // <ellipse cx="300" cy="8" rx="80" ry="42" fill="#ffffff14" />
        drawOval(
            color = TaminInsuranceCardInk.copy(alpha = TreatmentDimens.cardDecorUpperAlpha),
            topLeft = Offset(220f * sx, -34f * sy),
            size = Size(160f * sx, 84f * sy),
        )
    }
}

/**
 * The tile carries the holder's own initial rather than the organization's mark — the design
 * leaves the branding to the two lines of text beside it, so the card reads as *this person's*
 * card at a glance in a carousel where every page shares the same wording.
 */
@Composable
private fun InsuranceCardBrandRow(initial: String, modifier: Modifier = Modifier) {
    val text = insuranceCardTextStyles()
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(TreatmentDimens.brandTileSize)
                .background(
                    TaminInsuranceCardChipBg,
                    RoundedCornerShape(TreatmentDimens.brandTileRadius),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                style = text.initial,
                color = TaminInsuranceCardInk,
                maxLines = 1,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.card_org_name),
                style = text.orgName,
                color = TaminInsuranceCardInk,
            )
            Text(
                text = stringResource(Res.string.card_subtitle),
                style = text.orgSubtitle,
                color = TaminInsuranceCardInkMuted,
            )
        }
        Box(
            modifier = Modifier
                .size(TreatmentDimens.brandTickSize)
                .background(TaminInsuranceCardChipBg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = TaminInsuranceCardInk,
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
            // `border-top: 1px solid #ffffff26` and nothing else: the design separates the
            // coverage line with a rule, not with a second wash over the gradient.
            .drawBehind {
                drawRect(
                    color = TaminInsuranceCardDivider,
                    size = Size(size.width, Thickness.border.toPx()),
                )
            }
            .padding(
                horizontal = TreatmentDimens.cardFooterPaddingHorizontal,
                vertical = TreatmentDimens.cardFooterPaddingVertical,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TreatmentDimens.cardFooterGap),
    ) {
        badge?.invoke()
        Text(
            text = coverageLabel,
            style = insuranceCardTextStyles().coverage,
            color = TaminInsuranceCardInk,
            // A refusal reason is a sentence, not a status word, so it gets a second line before
            // being cut — the chip beside it still opens the full text.
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        action?.invoke()
    }
}

/**
 * One insured person's card, dressed for their entitlement.
 *
 * Takes the resolved pieces rather than the hub's state, so a total loading elsewhere on the
 * screen leaves the card untouched.
 */
@Composable
internal fun PatientCard(
    patient: PatientItemPR,
    status: CoverageStatus,
    dependantOrdinal: Int,
    collapseProgress: () -> Float = { 0f },
) {
    val style = status.cardStyle(
        isDependent = patient.isDependent,
        dependantOrdinal = dependantOrdinal,
    )
    InsuranceCard(
        holderName = patient.fullName,
        nationalId = patient.nationalId,
        coverageLabel = style.label,
        background = style.background,
        coverageBadge = style.badge,
        collapseProgress = collapseProgress,
    )
}

/** How a [CoverageStatus] is dressed on the card: wording, gradient and badge. */
private data class CoverageCardStyle(
    val label: String,
    val background: Brush,
    val badge: (@Composable () -> Unit)?,
)

/**
 * Covered is the only state that shows the person's own card identity — pending and
 * rejected deliberately override it so status stays readable at a glance.
 */
@Composable
private fun CoverageStatus.cardStyle(
    isDependent: Boolean,
    dependantOrdinal: Int,
): CoverageCardStyle {
    val colors = LocalTaminColors.current
    return when (this) {
        CoverageStatus.Pending -> CoverageCardStyle(
            label = stringResource(Res.string.coverage_pending),
            background = Brush.verticalGradient(listOf(colors.textMuted, colors.chevron)),
            badge = null,
        )

        is CoverageStatus.Rejected -> CoverageCardStyle(
            label = reason,
            background = insuranceCardGradient(isDependent, dependantOrdinal),
            badge = {
                CoverageBadge(
                    icon = vectorResource(Res.drawable.ic_tamin_cross),
                    containerColor = TaminCoverageRejectedBadgeBg,
                    contentColor = TaminCoverageRejectedBadgeFg,
                )
            },
        )

        CoverageStatus.Covered -> CoverageCardStyle(
            label = stringResource(Res.string.coverage_covered),
            background = insuranceCardGradient(isDependent, dependantOrdinal),
            badge = {
                CoverageBadge(
                    icon = vectorResource(Res.drawable.ic_tamin_verified),
                    containerColor = TaminCoverageBadgeBg,
                    contentColor = TaminCoverageBadgeFg,
                )
            },
        )
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
    /**
     * False when the caller draws the dots itself. The hub does: its carousel sits in a header
     * floating over the page, and dots left inside it are floating furniture the content scrolls
     * *behind* instead of below.
     */
    showIndicator: Boolean = true,
    card: @Composable (page: Int) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            // The design's track is inset by cardTrackPadding on both edges and each card is
            // cardPeekFraction of what is left — so the peek is the leftover *after* that inset,
            // not a share of the whole viewport. Measuring the fraction against the raw width
            // instead makes the card too wide and the neighbors too thin.
            val cardWidth =
                (maxWidth - TreatmentDimens.cardTrackPadding * 2) * TreatmentDimens.cardPeekFraction

            // Pinned to the start edge, not centred. The design's track is a `scroll-snap` row
            // resting at scroll 0, which under RTL holds the first card against the right inset
            // and lets the next one peek on the left. Padding both sides equally centres every
            // card instead and opens a gutter beside the first one.
            //
            // A lone card is the exception: with no neighbor to peek, pinning it leaves an empty
            // strip on one side. It keeps the carousel's card width and sits centred instead.
            val trackPadding = if (pageCount > 1) {
                PaddingValues(
                    start = TreatmentDimens.cardTrackPadding,
                    end = (maxWidth - TreatmentDimens.cardTrackPadding - cardWidth)
                        .coerceAtLeast(TreatmentDimens.cardTrackPadding),
                )
            } else {
                PaddingValues(horizontal = (maxWidth - cardWidth) / 2)
            }
            HorizontalPager(
                state = pagerState,
                key = { page -> page },
                contentPadding = trackPadding,
                pageSpacing = TreatmentDimens.cardTrackGap,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                card(page)
            }
        }
        if (pageCount > 1 && showIndicator) {
            TaminPageIndicator(
                pageCount = pageCount,
                pagerState = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = TreatmentDimens.pageIndicatorTopGap),
            )
        }
    }
}

/**
 * Skeleton placeholder for the brand row inside [InsuranceCardSkeleton].
 */
@Composable
private fun InsuranceCardBrandRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        ShimmerBlock(
            modifier = Modifier.size(TreatmentDimens.brandTileSize),
            cornerRadius = TreatmentDimens.brandTileRadius,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            ShimmerBlock(
                modifier = Modifier.width(110.dp).height(12.dp),
                cornerRadius = CornerRadius.xs,
            )
            ShimmerBlock(
                modifier = Modifier.width(140.dp).height(10.dp),
                cornerRadius = CornerRadius.xs,
            )
        }
        ShimmerBlock(
            modifier = Modifier.size(TreatmentDimens.brandTickSize),
            cornerRadius = CornerRadius.full,
        )
    }
}

/**
 * Skeleton placeholder for the footer row inside [InsuranceCardSkeleton].
 */
@Composable
private fun InsuranceCardFooterSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(
                    color = colors.border,
                    size = Size(size.width, Thickness.border.toPx()),
                )
            }
            .padding(
                horizontal = TreatmentDimens.cardFooterPaddingHorizontal,
                vertical = TreatmentDimens.cardFooterPaddingVertical,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TreatmentDimens.cardFooterGap),
    ) {
        ShimmerBlock(
            modifier = Modifier.size(TreatmentDimens.coverageBadgeSize),
            cornerRadius = CornerRadius.full,
        )
        ShimmerBlock(
            modifier = Modifier.width(180.dp).height(13.dp),
            cornerRadius = CornerRadius.xs,
        )
    }
}

/**
 * Skeleton placeholder for an electronic health-insurance card during initial load.
 * Matches the expanded → collapsed geometry of [InsuranceCard] to eliminate layout shift,
 * styled as a surface card with standard shimmer blocks like other features' cards.
 */
@Composable
fun InsuranceCardSkeleton(
    modifier: Modifier = Modifier,
    collapseProgress: () -> Float = { 0f },
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier = modifier
            .fillMaxWidth()
            .raisedCard(CornerRadius.cardCompact),
    ) {
        Layout(
            content = {
                InsuranceCardBrandRowSkeleton(
                    modifier = Modifier
                        .layoutId(CardSlot.Brand)
                        .vanishOnCollapse(collapseProgress),
                )
                ShimmerBlock(
                    modifier = Modifier
                        .layoutId(CardSlot.Name)
                        .shrinkOnCollapse(
                            progress = collapseProgress,
                            minScale = TreatmentDimens.cardNameCollapsedScale,
                            rtl = rtl,
                        )
                        .size(width = 120.dp, height = 20.dp),
                    cornerRadius = CornerRadius.xs,
                )
                ShimmerBlock(
                    modifier = Modifier
                        .layoutId(CardSlot.CodeLabel)
                        .vanishOnCollapse(collapseProgress)
                        .size(width = 44.dp, height = 11.dp),
                    cornerRadius = CornerRadius.xs,
                )
                ShimmerBlock(
                    modifier = Modifier
                        .layoutId(CardSlot.Code)
                        .size(width = 100.dp, height = 15.dp),
                    cornerRadius = CornerRadius.xs,
                )
                ShimmerBlock(
                    modifier = Modifier
                        .layoutId(CardSlot.Badge)
                        .size(TreatmentDimens.coverageBadgeSize),
                    cornerRadius = CornerRadius.full,
                )
                InsuranceCardFooterSkeleton(
                    modifier = Modifier
                        .layoutId(CardSlot.Footer)
                        .vanishOnCollapse(collapseProgress),
                )
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val pad = TreatmentDimens.cardPaddingHorizontal.roundToPx()
            val padTop = TreatmentDimens.cardPaddingTop.roundToPx()
            val padBottom = TreatmentDimens.cardPaddingBottom.roundToPx()
            val nameGap = TreatmentDimens.cardNameTopGap.roundToPx()
            val codeLabelGap = TreatmentDimens.cardCodeLabelTopGap.roundToPx()
            val codeGap = TreatmentDimens.cardCodeTopGap.roundToPx()
            val sm = Spacing.sm.roundToPx()
            val innerC = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))

            val brand = measurables.slot(CardSlot.Brand).measure(innerC)
            val name = measurables.slot(CardSlot.Name).measure(innerC)
            val label = measurables.slot(CardSlot.CodeLabel).measure(innerC)
            val number = measurables.slot(CardSlot.Code).measure(innerC)
            val badge = measurables.slot(CardSlot.Badge).measure(Constraints())
            val footer = measurables.slot(CardSlot.Footer).measure(Constraints.fixedWidth(width))

            val nameExpTop = padTop + brand.height + nameGap
            val labelExpTop = nameExpTop + name.height + codeLabelGap
            val numberExpTop = labelExpTop + label.height + codeGap
            val footerTop = numberExpTop + number.height + padBottom
            val badgeExpTop = footerTop + (footer.height - badge.height) / 2
            val expandedH = footerTop + footer.height

            val barH = maxOf(badge.height, name.height, number.height) +
                TreatmentDimens.cardBarPadding.roundToPx()
            val badgeColTop = (barH - badge.height) / 2
            val nameColStart = pad + badge.width + sm
            val nameColTop = (barH - name.height) / 2
            val numberColStart =
                nameColStart + (name.width * TreatmentDimens.cardNameCollapsedScale).toInt() + sm
            val numberColTop = (barH - number.height) / 2

            val t = Easing.standard.transform(collapseProgress())

            layout(width, lerp(expandedH, barH, t)) {
                brand.placeRelative(pad, padTop)
                label.placeRelative(pad, labelExpTop)
                footer.placeRelative(0, footerTop)
                name.placeRelative(lerp(pad, nameColStart, t), lerp(nameExpTop, nameColTop, t))
                number.placeRelative(lerp(pad, numberColStart, t), lerp(numberExpTop, numberColTop, t))
                badge.placeRelative(pad, lerp(badgeExpTop, badgeColTop, t))
            }
        }
    }
}

/**
 * Shimmering placeholder for [TaminPageIndicator] during initial load.
 */
@Composable
fun TaminPageIndicatorSkeleton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBlock(
            modifier = Modifier.size(width = 24.dp, height = 7.dp),
            cornerRadius = CornerRadius.full,
        )
        repeat(2) {
            ShimmerBlock(
                modifier = Modifier.size(7.dp),
                cornerRadius = CornerRadius.full,
            )
        }
    }
}

/**
 * Skeleton carousel matching [InsuranceCardCarousel] during initial load:
 * displays the main insured person's card, peeking dependant card, and page indicator.
 */
@Composable
fun InsuranceCardCarouselSkeleton(
    modifier: Modifier = Modifier,
    collapseProgress: () -> Float = { 0f },
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds(),
        ) {
            val cardWidth =
                (maxWidth - TreatmentDimens.cardTrackPadding * 2) * TreatmentDimens.cardPeekFraction

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = TreatmentDimens.cardTrackPadding),
                horizontalArrangement = Arrangement.spacedBy(TreatmentDimens.cardTrackGap),
            ) {
                InsuranceCardSkeleton(
                    modifier = Modifier.width(cardWidth),
                    collapseProgress = collapseProgress,
                )
                InsuranceCardSkeleton(
                    modifier = Modifier
                        .width(cardWidth)
                        .vanishOnCollapse(collapseProgress),
                    collapseProgress = collapseProgress,
                )
            }
        }
        TaminPageIndicatorSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TreatmentDimens.pageIndicatorTopGap)
                .vanishOnCollapse(collapseProgress),
        )
    }
}


