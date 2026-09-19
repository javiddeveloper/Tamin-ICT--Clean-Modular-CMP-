package com.tamin.taminhamrah.feature.history.ui.model

import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.ui.components.BarChartItem
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialMonthBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialMonthTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialYearBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialYearTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryZeroText
import com.tamin.taminhamrah.ui.theme.TaminLightTextSecondary
import com.tamin.taminhamrah.ui.theme.TaminNavy700
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * The دستمزد series, and how it is scaled.
 *
 * Days and wages are plotted the same way but scaled differently, and the difference is the whole
 * point of showing both:
 *
 * - **days** are scaled against a *fixed* full year, so a short year looks short — the yardstick is
 *   the calendar, and it does not move.
 * - **wages** have no such yardstick. There is no "full" wage, and wages climb every year with
 *   inflation, so the only honest scale is this person's own highest figure. That is why the wage
 *   chart carries its «بیشینه …» label and the days chart's is a constant: without it the bars are
 *   proportions of an unstated number.
 *
 * Color still encodes *cover*, not earnings, in both charts — a year is green when it is a full
 * year of insurance. A tall amber bar therefore reads correctly as "earned a lot, insured briefly",
 * which is exactly the case a person checking their record needs to notice.
 */

/** Total reported wage for one year, across every employer that reported it. */
fun List<DastmozdInfoItemPR>.yearWageTotal(): Long =
    sumOf { row -> row.wageDetails.sumOf { it.wage.toLongOrNull() ?: 0L } }

/**
 * Wage per month for one year, index 0 = فروردین.
 *
 * [source] narrows to a single employer by its position in the year's rows, matching how the day
 * chart and the source chips address one; null adds them together.
 */
fun List<DastmozdInfoItemPR>.monthWages(source: Int?): LongArray {
    val months = LongArray(HistoryConstants.MONTHS_IN_YEAR)
    val rows = if (source == null) this else listOfNotNull(getOrNull(source))
    rows.forEach { row ->
        row.wageDetails.forEachIndexed { index, detail ->
            if (index < HistoryConstants.MONTHS_IN_YEAR) {
                months[index] += detail.wage.toLongOrNull() ?: 0L
            }
        }
    }
    return months
}

/**
 * The wage series across every year, newest first, in the same order [yearBars] uses.
 *
 * [years] rather than the wage map decides which bars exist, so the two charts always line up
 * column for column — a year the wage service did not report still gets a bar, at zero, instead of
 * silently shifting every column beside it.
 */
fun List<YearHistoryPR>.wageYearBars(
    wageByYear: ImmutableMap<String, ImmutableList<DastmozdInfoItemPR>>,
    toPersian: (String) -> String,
): ImmutableList<BarChartItem> {
    if (isEmpty()) return persistentListOf()
    val totals = associate { it.year to (wageByYear[it.year]?.yearWageTotal() ?: 0L) }
    val max = totals.values.maxOrNull() ?: 0L

    return asReversed().map { year ->
        val wage = totals[year.year] ?: 0L
        val full = year.isComplete
        BarChartItem(
            id = year.year,
            label = toPersian(year.year),
            fraction = if (max == 0L) 0f else wage.toFloat() / max.toFloat(),
            fillTop = if (full) TaminHistoryBarFullTop else TaminHistoryBarPartialYearTop,
            fillBottom = if (full) TaminHistoryBarFullBottom else TaminHistoryBarPartialYearBottom,
            labelColor = if (wage > 0L) TaminLightTextSecondary else TaminHistoryZeroText,
            // A year with no wage reported has nothing to open, the same rule the day bars use.
            enabled = wage > 0L,
        )
    }.toImmutableList()
}

/** The tallest year's wage, which is what the «بیشینه» label states. */
fun List<YearHistoryPR>.maxYearWage(
    wageByYear: ImmutableMap<String, ImmutableList<DastmozdInfoItemPR>>,
): Long = maxOfOrNull { wageByYear[it.year]?.yearWageTotal() ?: 0L } ?: 0L

/**
 * One year's wages as twelve months, optionally narrowed to a single employer.
 *
 * Scaled against the tallest month of the year on screen, for the reason above: a month's wage has
 * no fixed ceiling to be a fraction of.
 */
fun List<DastmozdInfoItemPR>?.wageMonthBars(
    source: Int?,
    selectedMonth: Int?,
    wageLabel: (wage: Long) -> String,
    isMonthFull: (month: Int) -> Boolean = { false },
): ImmutableList<BarChartItem> {
    val rows = this ?: return persistentListOf()
    val months = rows.monthWages(source)
    val max = months.maxOrNull() ?: 0L

    return List(HistoryConstants.MONTHS_IN_YEAR) { month ->
        val wage = months[month]
        val selected = month == selectedMonth
        val full = isMonthFull(month)
        BarChartItem(
            id = month.toString(),
            label = PersianDateFormatter.monthNames[month],
            fraction = if (max == 0L) 0f else wage.toFloat() / max.toFloat(),
            fillTop = when {
                selected -> if (full) com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullSelectedTop else com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialSelectedTop
                full -> TaminHistoryBarFullTop
                else -> TaminHistoryBarPartialMonthTop
            },
            fillBottom = when {
                selected -> if (full) com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullSelectedBottom else com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialSelectedBottom
                full -> TaminHistoryBarFullBottom
                else -> TaminHistoryBarPartialMonthBottom
            },
            labelColor = when {
                selected -> TaminNavy700
                wage > 0L -> TaminLightTextSecondary
                else -> TaminHistoryZeroText
            },
            pill = wageLabel(wage).takeIf { selected && wage > 0L },
            labelBold = selected,
            enabled = wage > 0L,
        )
    }.toImmutableList()
}

/** The tallest month's wage in one year, for the «بیشینه» label in year scope. */
fun List<DastmozdInfoItemPR>?.maxMonthWage(source: Int?): Long =
    this?.monthWages(source)?.maxOrNull() ?: 0L
