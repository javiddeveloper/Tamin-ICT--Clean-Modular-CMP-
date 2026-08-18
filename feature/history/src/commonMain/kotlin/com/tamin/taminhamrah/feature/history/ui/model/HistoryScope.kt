package com.tamin.taminhamrah.feature.history.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/** Months in a Jalali year. */
private const val MONTHS = 12

/** Days the service counts to a month when it reports a length. */
private const val DAYS_IN_MONTH = 30

/**
 * What the page is showing: everything, or one year.
 *
 * The whole page reads from this — the orb's number, its caption, the chart's title and bars, the
 * chips beside the orb and what sits under the chart. One switch rather than four flags is what
 * keeps them from disagreeing.
 */
@Immutable
sealed interface HistoryScope {
    data object All : HistoryScope

    data class Year(val year: String) : HistoryScope
}

/** One year on the hero's chip strip, including the years with nothing in them. */
@Immutable
data class YearChipPR(
    val year: String,
    val label: String,
    /** A year inside the span that the service reported nothing for: shown, but not selectable. */
    val hasHistory: Boolean,
)

/** A figure beside the orb — «۱۲ سال», «۴ ماه», «۲ منبع». */
@Immutable
data class DurationChipPR(val value: String, val label: String)

/** One employer's year: who they were, and what they reported month by month. */
@Immutable
data class WorkshopPR(
    val id: String,
    val name: String,
    val type: String,
    val branch: String,
    /** The workshop's own number, or null when the row carries none. */
    val code: String?,
    val totalDays: Int,
    /** Only the months this employer actually reported. */
    val months: ImmutableList<WorkedMonthPR>,
)

/** A month one employer reported: which one, how many days, and what it paid. */
@Immutable
data class WorkedMonthPR(
    val monthIndex: Int,
    val days: Int,
    /** Raw from the wire; formatted at the edge that draws it. */
    val wage: String,
)

/**
 * One year, ready to draw: its months, the employers that reported it, and where they overlap.
 *
 * [concurrentMonths] is the design's «اشتغال همزمان» — a month more than one employer sent a list
 * for. It is derived rather than stored, because it is a fact about the rows, not a field on them.
 */
@Immutable
data class YearDetailPR(
    val year: String,
    val monthDays: ImmutableList<Int>,
    val totalDays: Int,
    val workshops: ImmutableList<WorkshopPR>,
    val concurrentMonths: ImmutableList<Boolean>,
) {
    val hasConcurrency: Boolean get() = concurrentMonths.any { it }
}

/**
 * The full span of years, gaps included.
 *
 * The chip strip shows every year between the first and the last, so a person can see the years
 * they were *not* insured as well as the ones they were — which is the point of the note under the
 * chart. Years the service reported are selectable; the gaps are not.
 */
fun List<YearHistoryPR>.yearChips(toPersian: (String) -> String): ImmutableList<YearChipPR> {
    val present = mapTo(HashSet()) { it.year }
    val numbers = mapNotNull { it.year.toIntOrNull() }
    if (numbers.isEmpty()) return persistentListOf()

    // Newest first, which is the order the design lists them in.
    return (numbers.max() downTo numbers.min()).map { year ->
        val asText = year.toString()
        YearChipPR(
            year = asText,
            label = toPersian(asText),
            hasHistory = asText in present,
        )
    }.toImmutableList()
}

/** The years inside the span that have nothing recorded — what the note under the chart counts. */
fun List<YearHistoryPR>.gapYearCount(): Int {
    val numbers = mapNotNull { it.year.toIntOrNull() }
    if (numbers.isEmpty()) return 0
    return (numbers.max() - numbers.min() + 1) - numbers.distinct().size
}

/**
 * Everything one year needs, folded from the merged year and the employers who reported it.
 *
 * The month totals come from `talfighinfos` — the service's own merge — while the employers and
 * their wages come from `dastmozdinfos`. Neither is derived from the other: a year can have months
 * with no wage row behind them (before ۱۳۸۶ there are none at all), and the design shows that
 * honestly rather than making the two agree.
 */
fun YearHistoryPR.detailWith(rows: List<DastmozdInfoItemPR>): YearDetailPR {
    val workshops = rows.map { row ->
        val months = row.wageDetails.mapIndexedNotNull { index, detail ->
            val days = detail.month.toIntOrNull() ?: 0
            val wage = detail.wage.toLongOrNull() ?: 0L
            if (days == 0 && wage == 0L) null else WorkedMonthPR(index, days, detail.wage)
        }
        WorkshopPR(
            id = row.rwshid.ifBlank { row.id.toString() },
            name = row.rwshname,
            type = row.historytypedesc,
            branch = row.brhname,
            code = row.rwshid.takeIf { it.isNotBlank() },
            totalDays = months.sumOf { it.days },
            months = months.toImmutableList(),
        )
    }

    val concurrent = BooleanArray(MONTHS) { month ->
        rows.count { row ->
            (row.wageDetails.getOrNull(month)?.month?.toIntOrNull() ?: 0) > 0
        } > 1
    }

    return YearDetailPR(
        year = year,
        monthDays = monthDays,
        totalDays = totalDays,
        workshops = workshops.toImmutableList(),
        concurrentMonths = concurrent.toList().toImmutableList(),
    )
}

/**
 * The three figures beside the orb.
 *
 * They say different things in the two scopes, which is the design's own choice: a career is read
 * in years, while a single year is read in the months and the number of employers behind it.
 */
fun careerDurationChips(
    total: CareerTotalPR,
    labels: DurationLabels,
    toPersian: (Int) -> String,
): ImmutableList<DurationChipPR> = persistentListOf(
    DurationChipPR(toPersian(total.years), labels.years),
    DurationChipPR(toPersian(total.months), labels.months),
    DurationChipPR(toPersian(total.days), labels.days),
)

fun yearDurationChips(
    detail: YearDetailPR?,
    labels: DurationLabels,
    toPersian: (Int) -> String,
): ImmutableList<DurationChipPR> {
    val days = detail?.totalDays ?: 0
    return persistentListOf(
        DurationChipPR(toPersian(days / DAYS_IN_MONTH), labels.months),
        DurationChipPR(toPersian(days % DAYS_IN_MONTH), labels.days),
        DurationChipPR(toPersian(detail?.workshops?.size ?: 0), labels.sources),
    )
}

/** The four words the chips are labelled with, resolved once by the screen. */
@Immutable
data class DurationLabels(
    val years: String,
    val months: String,
    val days: String,
    val sources: String,
)
