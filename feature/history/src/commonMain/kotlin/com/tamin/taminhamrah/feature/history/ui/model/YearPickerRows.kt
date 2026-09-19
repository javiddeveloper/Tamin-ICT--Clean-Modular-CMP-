package com.tamin.taminhamrah.feature.history.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * One row of «انتخاب سال و ماه» — a year in the right column, a month in the left.
 *
 * Both columns are the same row: a name, a count under it, and the three states the design draws
 * (picked, pickable, and nothing recorded). One model rather than two keeps them from drifting the
 * first time one of their paddings is adjusted.
 */
@Immutable
data class YearPickerRowPR(
    val id: String,
    val label: String,
    val caption: String,
    val selected: Boolean,
    /** A year or month the service reported nothing for is shown, dimmed, and does not answer. */
    val enabled: Boolean,
)

/**
 * The years the search matches, newest first.
 *
 * Built from [YearChipPR] rather than from the year list, because the chips already answer the
 * question this column asks: *which years are in the span, and which of them have history*. The
 * strip walks the whole span so the gaps in a career are visible rather than skipped, and the
 * picker must list exactly the same years in exactly the same order — deriving the span twice is
 * how the two would disagree the first time either was edited.
 *
 * Matched as a substring, which is what the design does — typing ۹۵ finds ۱۳۹۵ — and against ASCII,
 * because the query was folded to ASCII on its way into the state.
 */
fun List<YearChipPR>.pickerYearRows(
    years: List<YearHistoryPR>,
    query: String,
    picked: String?,
    dayLabel: (String) -> String,
    noHistoryLabel: String,
    toPersian: (String) -> String,
): ImmutableList<YearPickerRowPR> {
    val daysByYear = years.associate { it.year to it.totalDays }
    return filter { query.isEmpty() || it.year.contains(query) }
        .map { chip ->
            YearPickerRowPR(
                id = chip.year,
                label = chip.label,
                caption = if (chip.hasHistory) {
                    dayLabel(toPersian((daysByYear[chip.year] ?: 0).toString()))
                } else {
                    noHistoryLabel
                },
                selected = chip.year == picked,
                enabled = chip.hasHistory,
            )
        }
        .toImmutableList()
}

/**
 * The twelve months of the staged year, or nothing while no year is staged.
 *
 * Empty rather than twelve dead rows: the column then reads as waiting for a year, and nobody is
 * offered a month that cannot belong to anything.
 */
fun YearHistoryPR?.pickerMonthRows(
    picked: Int?,
    monthNames: List<String>,
    dayLabel: (String) -> String,
    toPersian: (String) -> String,
): ImmutableList<YearPickerRowPR> {
    val year = this ?: return persistentListOf()
    return List(HistoryConstants.MONTHS_IN_YEAR) { month ->
        val days = year.monthDays.getOrElse(month) { 0 }
        YearPickerRowPR(
            id = month.toString(),
            label = monthNames[month],
            caption = if (days > 0) dayLabel(toPersian(days.toString())) else EMPTY_MONTH,
            selected = month == picked,
            enabled = days > 0,
        )
    }.toImmutableList()
}

/** What the design prints where a month has no days: an em dash, not «۰ روز». */
private const val EMPTY_MONTH = "—"
