package com.tamin.taminhamrah.model.history

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/** How one month of a year stands, as the خلاصهٔ سابقه card paints it. */
enum class HistoryMonthStatusPR {
    /** The premium reached the organization: a filled cell, and a filled stretch of the bar. */
    Registered,

    /** Elapsed, and nothing registered against it — the hatched amber the warning row counts. */
    Unpaid,

    /** Not here yet. Drawn as an empty outline so the year reads as a whole from فروردین. */
    Upcoming,
}

/**
 * A year of premium payments, as the home page summarizes it.
 *
 * Counts are held as numbers rather than as the strings the card prints, so the caller cannot hand
 * in a figure that disagrees with the strip beside it — the card derives every label from
 * [months], and only converts to Persian digits at the moment of drawing.
 *
 * @param months exactly twelve entries, فروردین first.
 * @param currentMonthIndex the month the user is living in, or `-1` for a year already past —
 * the card rings that cell rather than adding a thirteenth kind of state to [months].
 * @param lastRegisteredMonth the name of the newest registered month, or `null` when none is.
 */
@Immutable
data class HistorySummaryPR(
    val yearLabel: String,
    val months: ImmutableList<HistoryMonthStatusPR>,
    val currentMonthIndex: Int,
    val lastRegisteredMonth: String?,
) {
    val registeredCount: Int get() = months.count { it == HistoryMonthStatusPR.Registered }

    val unpaidCount: Int get() = months.count { it == HistoryMonthStatusPR.Unpaid }

    /** Months the year has actually reached — what "۳ از ۶" is out of. */
    val elapsedCount: Int get() = months.count { it != HistoryMonthStatusPR.Upcoming }

    companion object {
        const val MONTHS_IN_YEAR = 12
    }
}

/**
 * The newest year on record, as خلاصهٔ سابقه reads it.
 *
 * `talfighinfos` answers with a row per employer per year, so the twelve day-counts are summed
 * across the rows sharing that year — the same fold «کلیه سوابق» does over a whole career, narrowed
 * to the one year this card shows.
 *
 * The newest year rather than the current one, which is what the design's own handler picks: a
 * record that stops in ۱۴۰۲ is summarized at ۱۴۰۲, where every month has elapsed, rather than at a
 * current year that would read as twelve unpaid months for someone who simply is not working.
 *
 * Null when there is no year at all. A person with no history has nothing to summarize, and the
 * home page leaves the card out rather than drawing an empty one.
 */
fun List<TalfighInfoItemPR>.toHistorySummary(
    currentYear: Int = PersianDateFormatter.currentJalaliYear(),
    currentMonth: Int = PersianDateFormatter.today().second,
): HistorySummaryPR? {
    val newest = mapNotNull { it.hisYear.toIntOrNull() }.maxOrNull() ?: return null

    val days = IntArray(HistorySummaryPR.MONTHS_IN_YEAR)
    forEach { row ->
        if (row.hisYear.toIntOrNull() != newest) return@forEach
        for (month in days.indices) {
            days[month] += row.months.getOrNull(month)?.toIntOrNull() ?: 0
        }
    }

    // -1 for a year already behind us: all twelve of its months have elapsed, so none of them is
    // upcoming and no cell is ringed as the month being lived in.
    val currentMonthIndex = if (newest == currentYear) currentMonth - 1 else -1
    val months = List(HistorySummaryPR.MONTHS_IN_YEAR) { index ->
        when {
            days[index] > 0 -> HistoryMonthStatusPR.Registered
            currentMonthIndex in 0..<index -> HistoryMonthStatusPR.Upcoming
            else -> HistoryMonthStatusPR.Unpaid
        }
    }

    return HistorySummaryPR(
        yearLabel = newest.toString().toPersianDigits(),
        months = months.toImmutableList(),
        currentMonthIndex = currentMonthIndex,
        lastRegisteredMonth = months
            .indexOfLast { it == HistoryMonthStatusPR.Registered }
            .takeIf { it >= 0 }
            ?.let { PersianDateFormatter.monthNames[it] },
    )
}
