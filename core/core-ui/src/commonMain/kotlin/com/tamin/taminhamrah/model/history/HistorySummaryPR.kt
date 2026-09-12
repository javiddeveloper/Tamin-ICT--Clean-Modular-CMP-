package com.tamin.taminhamrah.model.history

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

/** How one month of a year stands, as the خلاصهٔ سابقه card paints it. */
enum class HistoryMonthStatusPR {
    /** The premium reached the organisation: a filled cell, and a filled stretch of the bar. */
    Registered,

    /** Elapsed, and nothing registered against it — the hatched amber the warning row counts. */
    Unpaid,

    /** Not here yet. Drawn as an empty outline so the year reads as a whole from فروردین. */
    Upcoming,
}

/**
 * A year of premium payments, as the home page summarises it.
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
