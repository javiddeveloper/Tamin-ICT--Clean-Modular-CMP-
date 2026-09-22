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
    val heroPaddingBottom = 54.dp

    /**
     * What the hero's lower band closes to as the card folds.
     *
     * Open, the band is deep enough for the card to ride up into. Folded, the head tightens around
     * the summary bar and carries it up with it, instead of leaving the bar parked on the hero's
     * bottom edge under a band of empty blue as deep as the open state needed.
     *
     * Not [heroPaddingBottomCollapsed]: that one is the year-workshops head's own fixed padding,
     * which has no card to make room for. Sharing the number would tie two unrelated heads together.
     */
    val heroPaddingBottomFolded = 34.dp

    val heroActionSize = 36.dp
    val heroActionCorner = 14.dp
    val heroActionIconSize = 18.dp

    // ── Duration Card ─────────────────────────────────────────────────────────
    /** How far the card rides up into the hero when open. */
    val durationCardOverlap = 34.dp

    /** What the card measures with the scope pill and the step buttons still on it. */
    val durationCardExpandedHeight = 84.dp

    /** What is left once it is a summary bar: it's padding around the folded figures. */
    val durationCardCollapsedHeight = 38.dp

    /**
     * Folded, the bar rides up by half its own height, so the hero's bottom edge runs through its
     * center rather than sitting above it — the rule the identity and treatment cards fold by.
     *
     * The card folds *in place*: it keeps its slot under the chip strip and only gives up its own
     * height. Riding further up would carry it over the strip and onto the app-bar row.
     */
    val durationCardCollapsedOverlap = durationCardCollapsedHeight / 2

    val durationCardCorner = 24.dp
    val durationNavSize = 31.dp

    val durationCardPaddingH = 14.dp
    val durationCardPaddingV = 13.dp

    /** What the card's lower padding closes to once it is just a summary bar. */
    val durationCardPaddingVCollapsed = 7.dp

    /** Between the scope pill and the figures under it. */
    val durationFiguresGap = 5.dp

    /**
     * How far the figures shrink in the collapsed bar.
     *
     * Small enough to read as a summary rather than a heading, large enough that the ۴۶sp figure is
     * still the thing the eye lands on — the same role the name plays in the treatment card's bar.
     */
    const val durationFiguresCollapsedScale = 0.52f
    val durationNavIconSize = 15.dp

    /**
     * The three figures, and the units beside them.
     *
     * A much narrower spread than the mock's 46/27/17: at that range the years dwarf the months and
     * days into footnotes and the line stops reading as one figure. These keep the hierarchy — the
     * years are still first — while letting all three be read together.
     */
    val durationTextLarge = 34.sp
    val durationTextMedium = 27.sp
    val durationTextSmall = 23.sp
    val durationUnitLarge = 13.sp
    val durationUnitMedium = 12.sp
    val durationUnitSmall = 11.sp
    val durationSeparator = 14.sp
    val durationUnitGap = 4.dp
    val durationSeparatorGap = 5.dp

    /** The gap between the app-bar row, the chip strip and the duration card. */
    val heroRowGap = 16.dp

    /**
     * What the hero's lower padding closes to once folded away.
     */
    val heroPaddingBottomCollapsed = 12.dp

    /**
     * How far «کارگاه‌های سال» drags before its head is folded.
     *
     * What that head gives up: the caption-and-pills row, plus the gap it carries above it. The
     * title row and the back button stay, so the page keeps a bar to leave by however far it scrolls.
     */
    val workshopsHeroCollapseDistance = heroRowGap + 24.dp

    /**
     * How far the body drags before the hero is fully folded.
     *
     * Exactly what the head gives up — the card's own shrink, less the ride it hands back as it
     * settles onto the hero's edge — so the body tracks the finger 1:1. A budget larger than the
     * height actually lost is the fold finishing early and the drag carrying on against a head
     * that cannot move any further.
     */
    val heroCollapseDistance = (durationCardExpandedHeight - durationCardCollapsedHeight) -
        (durationCardOverlap - durationCardCollapsedOverlap) +
        (heroPaddingBottom - heroPaddingBottomFolded)

    /** The app-bar button, and the spacer that balances it so a centred title really is centred. */
    val heroButtonSize = 36.dp

    /** The bloom washed over the head's top corner. */
    val heroGlowSize = 95.dp

    /** The faint rule ruled over the hero. */
    val gridStep = 34.dp
    val gridStroke = 1.dp

    val chipGap = 5.dp
    val chipCorner = 13.dp
    val chipPaddingH = 11.dp
    val chipPaddingV = 8.dp

    /**
     * The hero's year strip — «همه» plus the years beside it.
     *
     * One table for three call sites: the chip, the «more» chip and the skeleton that stands in for
     * the whole strip while the career loads. A skeleton measured separately is a skeleton that
     * stops matching the thing it stands in for.
     */
    val yearChipHeight = 34.dp
    val yearChipCorner = 16.dp
    val yearChipGap = 6.dp

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
    val cardCorner = 22.dp
    val cardPaddingH = 12.dp
    val cardPaddingV = 14.dp

    val indicatorWidth = 3.dp
    val indicatorHeight = 15.dp
    val indicatorCorner = 2.dp

    val subChartCorner = 16.dp
    val subChartPaddingH = 11.dp
    val subChartPaddingV = 9.dp

    val legendCorner = 12.dp
    val legendPaddingH = 10.dp
    val legendPaddingV = 8.dp
    val legendDotSize = 9.dp
    val legendDotCorner = 3.dp

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
    /** A bar's own width, and the air between two of them, before the chart starts scrolling. */
    val chartBarWidth = 34.dp
    val chartBarGap = 6.dp

    val plotHeightSingle = 138.dp
    val plotHeightPaired = 104.dp
    val plotHeightSecondary = 48.dp
}
