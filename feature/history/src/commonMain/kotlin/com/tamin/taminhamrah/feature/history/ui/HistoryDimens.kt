package com.tamin.taminhamrah.feature.history.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Every measurement «کلیه سوابق» is drawn to, in one table.
 *
 * These are the design's own numbers, not theme spacing — the hero's 34dp grid, the 104dp orb, the
 * −18dp the chart card rides up over the header. Scattered as private vals per file they could not
 * be compared, and the same name meant different things in different files: `cardPaddingH` was 12dp
 * beside a workshop row and 15dp inside the chart card. Naming them by what they belong to is what
 * makes that visible.
 *
 * Anything that is ordinary spacing still comes from [com.tamin.taminhamrah.ui.theme.Spacing]; this
 * holds only what the design fixes.
 */
object HistoryDimens {

    /** A hairline border, everywhere one is drawn. */
    val hairline = 1.dp

    // ── Hero ──────────────────────────────────────────────────────────────────
    val heroCorner = 34.dp
    val heroPaddingH = 18.dp
    val heroPaddingTop = 8.dp
    val heroPaddingBottom = 28.dp

    /** The gap between the app-bar row, the chip strip and the orb row. */
    val heroRowGap = 14.dp

    /**
     * What the hero's lower padding closes to once the orb deck has folded away.
     *
     * Not zero: the year chips still need to clear the head's rounded edge, and the chart card
     * rides up over that edge by [chartOverlap] — at zero the two meet and the strip's last row
     * disappears under the card.
     */
    val heroPaddingBottomCollapsed = 12.dp

    /**
     * How far the body drags before the hero is fully folded.
     *
     * The orb deck's own height plus the row gap above it and the lower padding it takes with it,
     * so the fold finishes exactly as the deck runs out rather than part-way through it.
     */
    val heroCollapseDistance = 104.dp + 14.dp + 16.dp

    /** The faint rule ruled over the hero. */
    val gridStep = 34.dp
    val gridStroke = 1.dp

    val chipGap = 5.dp
    val chipCorner = 13.dp
    val chipPaddingH = 11.dp
    val chipPaddingV = 8.dp

    val orbSize = 104.dp
    val haloSize = 150.dp
    val haloBlur = 5.dp

    /** The orb's number shrinks as it grows, so a long career still fits the sphere. */
    val orbTextLarge = 27.sp
    val orbTextMedium = 24.sp
    val orbTextSmall = 22.sp

    val durationChipPaddingV = 6.dp
    val durationChipGap = 5.dp

    // ── Chart card ────────────────────────────────────────────────────────────
    val cardCorner = 24.dp
    val cardPaddingH = 15.dp
    val cardPaddingV = 14.dp

    val sourceChipPaddingV = 6.dp

    val legendSwatchWidth = 10.dp
    val legendSwatchHeight = 7.dp
    val legendSwatchCorner = 3.dp

    /** The «جمع دستمزد این ماه» panel. */
    val totalCorner = 12.dp

    /** Between an amount and its «ریال». */
    val wageGap = 3.dp

    // ── Rows and tiles ────────────────────────────────────────────────────────
    val rowPaddingH = 12.dp
    val rowPaddingV = 11.dp
    val tileSize = 34.dp
    val tileCorner = 12.dp
    val tileIconSize = 18.dp
    val chevronSize = 16.dp

    val pillCorner = 100.dp
    val pillPaddingV = 4.dp

    // ── Year sheet ────────────────────────────────────────────────────────────
    /** The design caps the sheet at 90% of the screen instead of letting it size to its content. */
    const val sheetMaxHeightFraction = 0.9f

    val sheetCorner = 30.dp
    val sheetPaddingH = 18.dp
    val sheetPaddingBottom = 22.dp

    val seasonCardPaddingH = 12.dp
    val seasonCardPaddingV = 10.dp
    val seasonDot = 6.dp

    val factTilePaddingH = 10.dp
    val factTilePaddingV = 8.dp
    val copyIcon = 12.dp

    /** Keeps the month names in the wage table aligned in their own column. */
    val monthColumnWidth = 52.dp

    val buttonCorner = 15.dp
    val buttonHeight = 46.dp

    /** The dashed rule: dash, then gap. */
    val dashWidth = 4.dp
    val dashGap = 3.dp

    // ── Year + month picker ───────────────────────────────────────────────────
    /** The two columns, and the gap the design sets between and inside them. */
    val pickerColumnGap = 9.dp
    val pickerCardCorner = 18.dp
    val pickerCardPaddingH = 8.dp
    val pickerCardPaddingTop = 9.dp
    val pickerCardPaddingBottom = 8.dp
    val pickerHeaderPaddingH = 4.dp
    val pickerHeaderPaddingBottom = 7.dp
    val pickerRowGap = 5.dp
    val pickerRowCorner = 13.dp
    val pickerRowPaddingH = 10.dp
    val pickerRowPaddingV = 8.dp

    /**
     * The design caps each column rather than letting it grow.
     *
     * The year column is the taller of the two because a career can be forty years and the month
     * one is always twelve, so they are given different ceilings rather than one shared number.
     */
    val pickerYearListHeight = 212.dp
    val pickerMonthListHeight = 172.dp

    val pickerApplyCorner = 17.dp
    val pickerApplyPaddingV = 13.dp
    val pickerAllYearsCorner = 16.dp
    val pickerAllYearsPaddingH = 13.dp
    val pickerAllYearsPaddingV = 11.dp
    val pickerEmptyPaddingV = 18.dp

    // ── Page ──────────────────────────────────────────────────────────────────
    val sidePadding = 18.dp
    val chartSidePadding = 14.dp

    /** The chart card rides up over the hero's rounded edge. */
    val chartOverlap = (-18).dp

    /** What a card measures, so a skeleton stands in for one without the page jumping. */
    val cardHeight = 76.dp

    /**
     * How tall each plot is drawn.
     *
     * Paired, the two series split one chart's worth of height unevenly: earnings carry the shape
     * worth reading and days become a strip beneath it, which is the design's own proportion
     * (`104px` over `48px`). Alone, a series takes the whole `138px`.
     */
    val plotHeightSingle = 138.dp
    val plotHeightPaired = 104.dp
    val plotHeightSecondary = 48.dp
}
