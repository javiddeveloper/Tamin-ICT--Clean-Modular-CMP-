package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.history.HistoryMonthStatusPR
import com.tamin.taminhamrah.model.history.HistorySummaryPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminColors
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.history_summary_details
import taminx.core.core_ui.history_summary_follow_up
import taminx.core.core_ui.history_summary_last_registered
import taminx.core.core_ui.history_summary_legend_registered
import taminx.core.core_ui.history_summary_legend_unpaid
import taminx.core.core_ui.history_summary_registered_of_elapsed
import taminx.core.core_ui.history_summary_subtitle
import taminx.core.core_ui.history_summary_title
import taminx.core.core_ui.history_summary_unpaid_warning
import taminx.core.core_ui.history_summary_year
import taminx.core.core_ui.ic_tamin_calendar_check
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_warning

/**
 * خلاصهٔ سابقه — one year of premium payments at a glance: how many of the elapsed months are
 * registered, which ones are not, and a way into the month-by-month detail.
 *
 * Everything it prints is derived from [summary], so the headline figure, the bar and the twelve
 * cells can never disagree with one another the way three separately-passed numbers could.
 *
 * @param onFollowUpClick shown as a «پیگیری» action on the unpaid warning when given; the row is
 * icon-and-text only when it is null, which is the variant a year with nothing outstanding needs
 * anyway.
 */
@Composable
fun HistorySummaryCard(
    summary: HistorySummaryPR,
    onCardClick: () -> Unit,
    onYearClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onFollowUpClick: (() -> Unit)? = null,
) {
    val colors = LocalTaminColors.current

    HistoryCardShell(colors = colors, modifier = modifier.clickable(onClick = onCardClick)) {
        HistoryCardHeader(colors = colors, yearLabel = summary.yearLabel, onYearClick = onYearClick)

        HistoryHeadlineRow(
            colors = colors,
            registeredCount = summary.registeredCount,
            elapsedCount = summary.elapsedCount,
            lastRegisteredMonth = summary.lastRegisteredMonth,
        )

        HistoryProgressBar(
            colors = colors,
            registeredCount = summary.registeredCount,
            unpaidCount = summary.unpaidCount,
            modifier = Modifier
                .padding(horizontal = CardPadding)
                .padding(top = ProgressTopGap)
                .fillMaxWidth()
                .height(ProgressHeight),
        )

        HistoryMonthStrip(
            colors = colors,
            months = summary.months,
            currentMonthIndex = summary.currentMonthIndex,
        )

        if (summary.unpaidCount > 0) {
            HistoryUnpaidWarning(
                colors = colors,
                unpaidCount = summary.unpaidCount,
                onFollowUpClick = onFollowUpClick,
            )
        }

        HistoryCardFooter(colors = colors, onDetailsClick = onDetailsClick)
    }
}

/** The card while the year is still being fetched — same silhouette, nothing claimed. */
@Composable
fun HistorySummaryCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current

    HistoryCardShell(colors = colors, modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = CardPadding)
                .padding(top = CardTopPadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HeaderGap),
        ) {
            ShimmerBlock(
                modifier = Modifier.size(HeaderIconSize),
                cornerRadius = HeaderIconRadius,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                ShimmerBlock(modifier = Modifier.size(width = 92.dp, height = 12.dp))
                ShimmerBlock(modifier = Modifier.size(width = 120.dp, height = 9.dp))
            }
            ShimmerBlock(
                modifier = Modifier.size(width = 62.dp, height = 22.dp),
                cornerRadius = CornerRadius.max,
            )
        }

        ShimmerBlock(
            modifier = Modifier
                .padding(horizontal = CardPadding)
                .padding(top = HeadlineTopGap)
                .size(width = 150.dp, height = 26.dp),
        )

        ShimmerBlock(
            modifier = Modifier
                .padding(horizontal = CardPadding)
                .padding(top = ProgressTopGap)
                .fillMaxWidth()
                .height(ProgressHeight),
            cornerRadius = CornerRadius.max,
        )

        // The twelve cells, at the rhythm the real strip has, so nothing shifts when it arrives.
        Row(
            modifier = Modifier
                .padding(horizontal = CardPadding)
                .padding(top = MonthStripTopGap)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MonthCellGap),
        ) {
            repeat(HistorySummaryPR.MONTHS_IN_YEAR) {
                ShimmerBlock(
                    modifier = Modifier.weight(1f).height(MonthCellHeight),
                    cornerRadius = MonthCellRadius,
                )
            }
        }

        // The month names are left blank but their lane is not: rotated names are a tall band, and
        // a skeleton that skipped it would hand the page a 62dp jolt the moment the year arrived.
        Spacer(modifier = Modifier.height(MonthLabelTopGap + RotatedLabelLane))

        HistoryCardFooterDivider(colors = colors)
        Spacer(modifier = Modifier.height(FooterPaddingVertical))
    }
}

// ─── Shell ────────────────────────────────────────────────────────────────────────────────────

/**
 * The card's paper: its fill, border, radius and the one decorative wash in the top corner.
 *
 * The glow is drawn rather than laid out — it is decoration with no size of its own, and a child
 * `Box` for it would cost a layout node and, under RTL, would need mirroring back to the physical
 * corner the design pins it to. Draw coordinates are physical, so this needs neither.
 */
@Composable
private fun HistoryCardShell(
    colors: TaminColors,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val glow = colors.historyGlow
    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.historySummaryShadow,
                borderRadius = CornerRadius.card,
                blurRadius = ShadowBlur,
                offsetY = ShadowOffsetY,
            )
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(colors.bgSurface)
            .border(BorderThickness, colors.historySummaryBorder, RoundedCornerShape(CornerRadius.card))
            .drawWithCache {
                val radius = GlowSize.toPx() / 2f
                val brush = Brush.radialGradient(
                    colors = listOf(glow, Color.Transparent),
                    center = Offset(GlowCenterX.toPx(), GlowCenterY.toPx()),
                    radius = radius,
                )
                onDrawBehind {
                    drawCircle(
                        brush = brush,
                        radius = radius,
                        center = Offset(GlowCenterX.toPx(), GlowCenterY.toPx()),
                    )
                }
            },
    ) {
        content()
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────────────────────

@Composable
private fun HistoryCardHeader(
    colors: TaminColors,
    yearLabel: String,
    onYearClick: () -> Unit,
) {
    val iconStops = colors.historyIconStops
    val tileBrush = remember(iconStops) { Brush.linearGradient(iconStops) }

    Row(
        modifier = Modifier
            .padding(horizontal = CardPadding)
            .padding(top = CardTopPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HeaderGap),
    ) {
        Box(
            modifier = Modifier
                .size(HeaderIconSize)
                .background(tileBrush, RoundedCornerShape(HeaderIconRadius)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_calendar_check),
                contentDescription = null,
                tint = colors.bgSurface,
                modifier = Modifier.size(HeaderIconGlyphSize),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            TaminText(
                text = stringResource(Res.string.history_summary_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
            )
            TaminText(
                text = stringResource(Res.string.history_summary_subtitle),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textMuted,
            )
        }

        YearPill(colors = colors, yearLabel = yearLabel, onClick = onYearClick)
    }
}

@Composable
private fun YearPill(colors: TaminColors, yearLabel: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.blueBg)
            .border(BorderThickness, colors.historyYearPillBorder, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = PillPaddingHorizontal, vertical = PillPaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PillGap),
    ) {
        TaminText(
            text = stringResource(Res.string.history_summary_year, yearLabel),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = colors.blueText,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(PillChevronSize),
        )
    }
}

// ─── Headline ─────────────────────────────────────────────────────────────────────────────────

@Composable
private fun HistoryHeadlineRow(
    colors: TaminColors,
    registeredCount: Int,
    elapsedCount: Int,
    lastRegisteredMonth: String?,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = CardPadding)
            .padding(top = HeadlineTopGap)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            NumericText(
                text = registeredCount.toString().toPersianDigits(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = FigureSize,
                ),
                color = colors.historyFigure,
            )
            TaminText(
                text = stringResource(
                    Res.string.history_summary_registered_of_elapsed,
                    elapsedCount.toString().toPersianDigits(),
                ),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = FigureBaselineNudge),
            )
        }

        if (lastRegisteredMonth != null) {
            TaminText(
                text = stringResource(Res.string.history_summary_last_registered, lastRegisteredMonth),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = FigureBaselineNudge),
            )
        }
    }
}

// ─── Progress ─────────────────────────────────────────────────────────────────────────────────

/**
 * Registered months as a solid stretch, unpaid ones hatched, both measured against the whole year
 * rather than against the months elapsed — the bar is how much of the year is settled.
 *
 * Drawn in one pass with no children: the two stretches are geometry, not layout.
 */
@Composable
private fun HistoryProgressBar(
    colors: TaminColors,
    registeredCount: Int,
    unpaidCount: Int,
    modifier: Modifier = Modifier,
) {
    val registeredStops = colors.historyMonthStops
    val track = colors.historyProgressTrack
    val stripe = colors.historyUnpaidStripe
    val stripeSoft = colors.historyUnpaidStripeSoft

    Box(
        modifier = modifier
            .clip(CircleShape)
            .drawWithCache {
                val months = HistorySummaryPR.MONTHS_IN_YEAR.toFloat()
                val registeredWidth = size.width * (registeredCount / months)
                val unpaidWidth = size.width * (unpaidCount / months)
                val rtl = layoutDirection == LayoutDirection.Rtl
                val registeredStart = if (rtl) size.width - registeredWidth else 0f
                val unpaidStart =
                    if (rtl) size.width - registeredWidth - unpaidWidth else registeredWidth
                val fill = Brush.horizontalGradient(
                    colors = registeredStops,
                    startX = registeredStart,
                    endX = registeredStart + registeredWidth,
                )
                val stripeStep = StripeWidth.toPx() * 2f
                val stripeStroke = StripeWidth.toPx()

                onDrawBehind {
                    drawRect(color = track)
                    drawRect(
                        brush = fill,
                        topLeft = Offset(registeredStart, 0f),
                        size = Size(registeredWidth, size.height),
                    )
                    if (unpaidWidth <= 0f) return@onDrawBehind

                    clipRect(
                        left = unpaidStart,
                        top = 0f,
                        right = unpaidStart + unpaidWidth,
                        bottom = size.height,
                    ) {
                        drawRect(
                            color = stripeSoft,
                            topLeft = Offset(unpaidStart, 0f),
                            size = Size(unpaidWidth, size.height),
                        )
                        // The design's 135° hatch: lines drawn upright, then the whole set turned.
                        rotate(degrees = StripeAngle) {
                            var x = -size.height
                            while (x < size.width + size.height) {
                                drawLine(
                                    color = stripe,
                                    start = Offset(x, -size.height),
                                    end = Offset(x, size.height * 2f),
                                    strokeWidth = stripeStroke,
                                )
                                x += stripeStep
                            }
                        }
                    }
                }
            },
    )
}

// ─── Month strip ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun HistoryMonthStrip(
    colors: TaminColors,
    months: List<HistoryMonthStatusPR>,
    currentMonthIndex: Int,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = CardPadding)
            .padding(top = MonthStripTopGap)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MonthCellGap),
    ) {
        months.forEachIndexed { index, status ->
            MonthCell(
                colors = colors,
                status = status,
                isCurrent = index == currentMonthIndex,
                modifier = Modifier.weight(1f),
            )
        }
    }

    // Whole month names, turned on their side exactly as «کلیه سوابق» draws its own month axis —
    // same helper, same lane height, so the two pages spell the year the same way. Twelve names do
    // not fit a card's width lying down, and the three-letter abbreviation the design used cannot
    // tell شهریور from شهر… at a glance.
    Row(
        modifier = Modifier
            .padding(horizontal = CardPadding)
            .padding(top = MonthLabelTopGap)
            .fillMaxWidth()
            .height(RotatedLabelLane),
        horizontalArrangement = Arrangement.spacedBy(MonthCellGap),
        verticalAlignment = Alignment.Top,
    ) {
        PersianDateFormatter.monthNames.forEachIndexed { index, name ->
            val isCurrent = index == currentMonthIndex
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentAlignment = Alignment.TopCenter,
            ) {
                TaminText(
                    text = name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isCurrent) colors.blueText else colors.textSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.rotateVertically(),
                )
            }
        }
    }
}

@Composable
private fun MonthCell(
    colors: TaminColors,
    status: HistoryMonthStatusPR,
    isCurrent: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(MonthCellRadius)
    val filled = status == HistoryMonthStatusPR.Registered
    val stops = if (isCurrent && filled) colors.historyMonthCurrentStops else colors.historyMonthStops
    val fill = remember(stops, filled) { if (filled) Brush.verticalGradient(stops) else null }

    Box(
        modifier = modifier
            .height(MonthCellHeight)
            .clip(shape)
            .then(
                when {
                    fill != null -> Modifier.background(fill)
                    status == HistoryMonthStatusPR.Unpaid -> Modifier
                        .background(colors.historyUnpaidBg)
                        .dashedOutline(colors.historyUnpaidBorder, MonthCellRadius, UnpaidBorderThickness)

                    else -> Modifier
                        .background(colors.historyUpcomingBg)
                        .dashedOutline(colors.historyUpcomingBorder, MonthCellRadius, BorderThickness)
                },
            )
            .then(
                // The ring the design puts on today's cell, inside the fill rather than around it.
                if (isCurrent && filled) {
                    Modifier.border(CurrentRingThickness, colors.bgSurface.copy(alpha = CurrentRingAlpha), shape)
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isCurrent && filled) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = colors.bgSurface,
                modifier = Modifier.size(MonthCheckSize),
            )
        }
    }
}

// ─── Warning ──────────────────────────────────────────────────────────────────────────────────

@Composable
private fun HistoryUnpaidWarning(
    colors: TaminColors,
    unpaidCount: Int,
    onFollowUpClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = CardPadding)
            .padding(top = WarningTopGap)
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.listRow))
            .background(colors.historyUnpaidBg)
            .border(BorderThickness, colors.historyWarningBorder, RoundedCornerShape(CornerRadius.listRow))
            .padding(horizontal = WarningPaddingHorizontal, vertical = WarningPaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_warning),
            contentDescription = null,
            tint = colors.orangeText,
            modifier = Modifier.size(WarningIconSize),
        )
        TaminText(
            text = stringResource(
                Res.string.history_summary_unpaid_warning,
                unpaidCount.toString().toPersianDigits(),
            ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = colors.historyWarningText,
            modifier = Modifier.weight(1f),
        )
        if (onFollowUpClick != null) {
            FollowUpPill(colors = colors, onClick = onFollowUpClick)
        }
    }
}

@Composable
private fun FollowUpPill(colors: TaminColors, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.bgSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = PillPaddingHorizontal, vertical = PillPaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PillGap),
    ) {
        TaminText(
            text = stringResource(Res.string.history_summary_follow_up),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = colors.orangeText,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.orangeText,
            modifier = Modifier.size(PillChevronSize),
        )
    }
}

// ─── Footer ───────────────────────────────────────────────────────────────────────────────────

@Composable
private fun HistoryCardFooter(colors: TaminColors, onDetailsClick: () -> Unit) {
    HistoryCardFooterDivider(colors = colors)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDetailsClick)
            .padding(horizontal = CardPadding, vertical = FooterPaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        LegendEntry(
            label = stringResource(Res.string.history_summary_legend_registered),
            labelColor = colors.textSecondary,
            swatch = { LegendSwatch(brush = Brush.verticalGradient(colors.historyMonthStops)) },
        )
        LegendEntry(
            label = stringResource(Res.string.history_summary_legend_unpaid),
            labelColor = colors.orangeText,
            swatch = {
                LegendSwatch(
                    color = colors.historyUnpaidBg,
                    borderColor = colors.historyUnpaidBorder,
                )
            },
        )
        TaminText(
            text = stringResource(Res.string.history_summary_details),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = colors.blueText,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(FooterChevronSize),
        )
    }
}

@Composable
private fun HistoryCardFooterDivider(colors: TaminColors) {
    Spacer(
        modifier = Modifier
            .padding(top = FooterTopGap)
            .fillMaxWidth()
            .height(BorderThickness)
            .background(colors.historyCardDivider),
    )
}

@Composable
private fun LegendEntry(
    label: String,
    labelColor: Color,
    swatch: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        swatch()
        TaminText(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = labelColor,
        )
    }
}

@Composable
private fun LegendSwatch(
    brush: Brush? = null,
    color: Color = Color.Unspecified,
    borderColor: Color? = null,
) {
    val shape = RoundedCornerShape(LegendSwatchRadius)
    Box(
        modifier = Modifier
            .size(LegendSwatchSize)
            .clip(shape)
            .then(if (brush != null) Modifier.background(brush) else Modifier.background(color))
            .then(
                if (borderColor != null) {
                    Modifier.dashedOutline(borderColor, LegendSwatchRadius, UnpaidBorderThickness)
                } else {
                    Modifier
                },
            ),
    )
}

// ─── Geometry, straight from the design ───────────────────────────────────────────────────────

private val CardPadding = 15.dp
private val CardTopPadding = 14.dp
private val BorderThickness = 1.dp
private val ShadowBlur = 30.dp
private val ShadowOffsetY = 12.dp

private val GlowSize = 170.dp
private val GlowCenterX = 39.dp    // left:-46px + half of 170
private val GlowCenterY = 25.dp    // top:-60px + half of 170

private val HeaderGap = 9.dp
private val HeaderIconSize = 34.dp
private val HeaderIconRadius = 12.dp
private val HeaderIconGlyphSize = 18.dp

private val PillPaddingHorizontal = 9.dp
private val PillPaddingVertical = 5.dp
private val PillGap = 5.dp
private val PillChevronSize = 11.dp

private val HeadlineTopGap = 12.dp
private val FigureSize = 34.sp
private val FigureBaselineNudge = 4.dp

private val ProgressTopGap = 9.dp
private val ProgressHeight = 7.dp
private val StripeWidth = 4.dp
private const val StripeAngle = -45f

private val MonthStripTopGap = 12.dp
private val MonthLabelTopGap = 5.dp

/** The design labels each month with its first three letters — دی simply has two. */
private val MonthCellGap = 3.dp
private val MonthCellHeight = 30.dp
private val MonthCellRadius = 9.dp
private val MonthCheckSize = 13.dp
private val UnpaidBorderThickness = 1.4.dp
private val CurrentRingThickness = 1.8.dp
private const val CurrentRingAlpha = 0.9f

private val WarningTopGap = 11.dp
private val WarningPaddingHorizontal = 11.dp
private val WarningPaddingVertical = 9.dp
private val WarningIconSize = 15.dp

private val FooterTopGap = 11.dp
private val FooterPaddingVertical = 10.dp
private val FooterChevronSize = 13.dp
private val LegendSwatchSize = 8.dp
private val LegendSwatchRadius = 3.dp

// ─── Previews ─────────────────────────────────────────────────────────────────────────────────

/** The year the design draws: six months elapsed, three of them registered, شهریور current. */
private fun previewSummary(
    months: List<HistoryMonthStatusPR> = listOf(
        HistoryMonthStatusPR.Unpaid,
        HistoryMonthStatusPR.Unpaid,
        HistoryMonthStatusPR.Registered,
        HistoryMonthStatusPR.Registered,
        HistoryMonthStatusPR.Unpaid,
        HistoryMonthStatusPR.Registered,
        HistoryMonthStatusPR.Upcoming,
        HistoryMonthStatusPR.Upcoming,
        HistoryMonthStatusPR.Upcoming,
        HistoryMonthStatusPR.Upcoming,
        HistoryMonthStatusPR.Upcoming,
        HistoryMonthStatusPR.Upcoming,
    ),
    currentMonthIndex: Int = 5,
    lastRegisteredMonth: String? = PersianDateFormatter.monthNames[5],
) = HistorySummaryPR(
    yearLabel = "1405".toPersianDigits(),
    months = months.toImmutableList(),
    currentMonthIndex = currentMonthIndex,
    lastRegisteredMonth = lastRegisteredMonth,
)

@Composable
private fun PreviewCard(summary: HistorySummaryPR, withFollowUp: Boolean = true) {
    Box(modifier = Modifier.background(LocalTaminColors.current.bgPage).padding(Spacing.page)) {
        HistorySummaryCard(
            summary = summary,
            onCardClick = {},
            onYearClick = {},
            onDetailsClick = {},
            onFollowUpClick = if (withFollowUp) ({}) else null,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistorySummaryCardPreview() {
    PreviewRtlThemeContent { PreviewCard(previewSummary()) }
}

@PreviewRtlTheme
@Composable
private fun HistorySummaryCardDarkPreview() {
    TaminHamrahTheme(darkTheme = true) { PreviewCard(previewSummary()) }
}

/** Nothing outstanding: no warning row, and the bar is one unbroken stretch. */
@PreviewRtlTheme
@Composable
private fun HistorySummaryCardAllRegisteredPreview() {
    PreviewRtlThemeContent {
        PreviewCard(
            previewSummary(
                months = List(HistorySummaryPR.MONTHS_IN_YEAR) { index ->
                    if (index <= 5) HistoryMonthStatusPR.Registered else HistoryMonthStatusPR.Upcoming
                },
            ),
        )
    }
}

/** A year with nothing registered yet — the figure reads ۰ and no month carries the ring. */
@PreviewRtlTheme
@Composable
private fun HistorySummaryCardNothingRegisteredPreview() {
    PreviewRtlThemeContent {
        PreviewCard(
            previewSummary(
                months = List(HistorySummaryPR.MONTHS_IN_YEAR) { index ->
                    if (index <= 5) HistoryMonthStatusPR.Unpaid else HistoryMonthStatusPR.Upcoming
                },
                lastRegisteredMonth = null,
            ),
        )
    }
}

/** A year already past: twelve registered months and no current one to ring. */
@PreviewRtlTheme
@Composable
private fun HistorySummaryCardPastYearPreview() {
    PreviewRtlThemeContent {
        PreviewCard(
            previewSummary(
                months = List(HistorySummaryPR.MONTHS_IN_YEAR) { HistoryMonthStatusPR.Registered },
                currentMonthIndex = -1,
                lastRegisteredMonth = PersianDateFormatter.monthNames.last(),
            ),
            withFollowUp = false,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistorySummaryCardSkeletonPreview() {
    PreviewRtlThemeContent {
        Box(modifier = Modifier.background(LocalTaminColors.current.bgPage).padding(Spacing.page)) {
            HistorySummaryCardSkeleton()
        }
    }
}
