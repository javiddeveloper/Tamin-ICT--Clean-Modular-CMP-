package com.tamin.taminhamrah.feature.history.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.TalfighInfoItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/** Months in a Jalali year — the length every row's day-count list is normalized to. */
private const val MONTHS_IN_YEAR = 12

/** The service counts a month as 30 days when it reports a career length. */
private const val DAYS_IN_MONTH = 30

/** Below this many insured days a year is short, which the design marks. */
private const val FULL_YEAR_DAYS = 365

/** One insurance year, after the employers reporting it have been added together. */
@Immutable
data class YearHistoryPR(
    /** Jalali year, e.g. "1400". The identity of this row — never its position in a list. */
    val year: String,
    /** Insured days per month: always 12 entries, index 0 = فروردین. */
    val monthDays: ImmutableList<Int>,
    val totalDays: Int,
) {
    /** A full year of cover, as opposed to one the person was only insured for part of. */
    val isComplete: Boolean get() = totalDays >= FULL_YEAR_DAYS
}

/** How long the person has been insured altogether. */
@Immutable
data class CareerTotalPR(
    val years: Int = 0,
    val months: Int = 0,
    val days: Int = 0,
    /** The same span in days, as the service reports it. */
    val totalDays: Int = 0,
)

/**
 * One row per year, with every employer's months added together.
 *
 * The endpoint returns a row per employer *and* history type, so the same year arrives more than
 * once and has to be folded — the previous app did the same.
 *
 * What it got wrong was opening the detail sheet by list *position*: the bars were built from the
 * merged list and indexed into the raw one, so every year after the first duplicate showed another
 * year's months. Carrying [YearHistoryPR] itself to the sheet removes that whole class of bug
 * rather than fixing one instance of it.
 *
 * Server order is preserved: nothing in the spec says which way the years should run, and inventing
 * an order here would be a design decision made in a mapper.
 */
fun List<TalfighInfoItemPR>.mergeByYear(): ImmutableList<YearHistoryPR> {
    if (isEmpty()) return persistentListOf()

    val daysByYear = LinkedHashMap<String, IntArray>(size)
    forEach { row ->
        val months = daysByYear.getOrPut(row.hisYear) { IntArray(MONTHS_IN_YEAR) }
        for (month in 0 until MONTHS_IN_YEAR) {
            months[month] += row.months.getOrNull(month)?.toIntOrNull() ?: 0
        }
    }

    return daysByYear.map { (year, months) ->
        YearHistoryPR(
            year = year,
            monthDays = months.asList().toImmutableList(),
            // Summed from the months rather than read off `sumYear`: for a year reported by two
            // employers the server's own total covers only the first of them.
            totalDays = months.sum(),
        )
    }.toImmutableList()
}

/**
 * Career length, normalized the way the previous app normalized it.
 *
 * Only the first row carries these totals. The 30-day month is the service's convention rather than
 * a calendar fact, and it is kept deliberately: the number has to agree with what the rest of
 * تأمین prints for the same person.
 */
fun List<TalfighInfoItemPR>.careerTotal(): CareerTotalPR {
    val row = firstOrNull() ?: return CareerTotalPR()

    val days = row.historyDays % DAYS_IN_MONTH
    val carriedMonths = row.historyMonths + row.historyDays / DAYS_IN_MONTH
    return CareerTotalPR(
        years = row.historyYears + carriedMonths / MONTHS_IN_YEAR,
        months = carriedMonths % MONTHS_IN_YEAR,
        days = days,
        totalDays = row.sumHistoryYears,
    )
}

/**
 * The years, rebuilt from the wage rows.
 *
 * `talfighinfos` is the service that merges a career into one row per year, and it is what this
 * page reads — but it answers with an empty list for some insured people whose history the wage
 * service reports in full. Seen in production: `talfighinfos` → `{"total":0,"list":[]}` while
 * `dastmozdinfos` returned five years of days and wages for the same person.
 *
 * `dastmozdinfos` carries the same per-month day counts, one row per employer per year, so the
 * years can be folded from it exactly the way [mergeByYear] folds the merged rows. Used only when
 * the merged service gives nothing: when it answers, it stays the source of truth.
 */
fun List<DastmozdInfoItemPR>.yearsFromWages(): ImmutableList<YearHistoryPR> {
    if (isEmpty()) return persistentListOf()

    val daysByYear = LinkedHashMap<String, IntArray>(size)
    forEach { row ->
        val months = daysByYear.getOrPut(row.hisyear) { IntArray(MONTHS_IN_YEAR) }
        for (month in 0 until MONTHS_IN_YEAR) {
            months[month] += row.wageDetails.getOrNull(month)?.month?.toIntOrNull() ?: 0
        }
    }

    return daysByYear.map { (year, months) ->
        YearHistoryPR(
            year = year,
            monthDays = months.asList().toImmutableList(),
            totalDays = months.sum(),
        )
    }.toImmutableList()
}

/**
 * The career total when only the wage rows are available.
 *
 * [careerTotal] reads figures the merged service puts on its first row; with no merged rows there
 * are none, so the same 30-day-month convention is applied to the days actually counted.
 */
fun List<YearHistoryPR>.careerTotalFromDays(): CareerTotalPR {
    val totalDays = sumOf { it.totalDays }
    val months = totalDays / DAYS_IN_MONTH
    return CareerTotalPR(
        years = months / MONTHS_IN_YEAR,
        months = months % MONTHS_IN_YEAR,
        days = totalDays % DAYS_IN_MONTH,
        totalDays = totalDays,
    )
}
