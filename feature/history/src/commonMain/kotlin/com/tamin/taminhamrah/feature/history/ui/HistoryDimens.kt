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

    // ── Page ──────────────────────────────────────────────────────────────────
    val sidePadding = 18.dp
    val chartSidePadding = 14.dp

    /** The chart card rides up over the hero's rounded edge. */
    val chartOverlap = (-18).dp

    /** What a card measures, so a skeleton stands in for one without the page jumping. */
    val cardHeight = 76.dp
}
