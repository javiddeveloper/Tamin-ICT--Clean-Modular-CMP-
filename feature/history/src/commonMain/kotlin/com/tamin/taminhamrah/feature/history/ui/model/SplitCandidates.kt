package com.tamin.taminhamrah.feature.history.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList

/**
 * One employer the chart can be broken out by — «تفکیک کارگاه».
 *
 * Identified by workshop number *and* history type together, which is the design's own key. Neither
 * alone is enough: one workshop can report a person under two schemes, and the schemes that carry no
 * workshop number at all (بیمهٔ اختیاری, کارگران ساختمانی) would otherwise collapse into a single
 * nameless entry.
 */
@Immutable
data class SplitCandidatePR(
    val key: String,
    val label: String,
    val totalDays: Int,
    /** Days per month, index 0 = فروردین, summed over every row this employer reported. */
    val monthDays: ImmutableList<Int>,
    val monthWages: ImmutableList<Long>,
)

/**
 * The employers worth offering a split by, largest first, capped at [MAX_SPLIT_SOURCES].
 *
 * In «همه» the whole career is grouped, so a person who has only ever had one employer is not
 * offered a split that would draw one bar; in a single year only that year's rows count. Both are
 * the design's rule, and the cap is its number — beyond four the bars are too thin to compare,
 * which is the whole point of splitting them.
 *
 * Ordered by days rather than by year, so the split shows the employers that actually account for
 * the career instead of whichever the service happened to list first.
 */
fun splitCandidates(
    wageByYear: ImmutableMap<String, ImmutableList<DastmozdInfoItemPR>>,
    year: String?,
    nameOf: (DastmozdInfoItemPR) -> String,
): ImmutableList<SplitCandidatePR> {
    val rows = if (year == null) {
        wageByYear.values.flatten()
    } else {
        wageByYear[year].orEmpty()
    }
    if (rows.isEmpty()) return emptyCandidates

    // Grouped in first-seen order, then ranked — so the ranking decides what shows, not the map.
    val grouped = LinkedHashMap<String, MutableList<DastmozdInfoItemPR>>()
    rows.forEach { row ->
        grouped.getOrPut(row.splitKey()) { mutableListOf() }.add(row)
    }

    return grouped.entries
        .map { (key, employerRows) ->
            val days = IntArray(HistoryConstants.MONTHS_IN_YEAR)
            val wages = LongArray(HistoryConstants.MONTHS_IN_YEAR)
            employerRows.forEach { row ->
                row.wageDetails.forEachIndexed { month, detail ->
                    if (month < HistoryConstants.MONTHS_IN_YEAR) {
                        days[month] += detail.month.toIntOrNull() ?: 0
                        wages[month] += detail.wage.toLongOrNull() ?: 0L
                    }
                }
            }
            SplitCandidatePR(
                key = key,
                label = nameOf(employerRows.first()),
                totalDays = days.sum(),
                monthDays = days.toList().toImmutableList(),
                monthWages = wages.toList().toImmutableList(),
            )
        }
        .sortedByDescending { it.totalDays }
        .take(MAX_SPLIT_SOURCES)
        .toImmutableList()
}

/** Workshop number and history type together — see [SplitCandidatePR]. */
private fun DastmozdInfoItemPR.splitKey(): String = "$rwshid|$historytypedesc"

private val emptyCandidates: ImmutableList<SplitCandidatePR> =
    kotlinx.collections.immutable.persistentListOf()

/** Past four, the bars are too thin to compare — which is the only reason to split them. */
const val MAX_SPLIT_SOURCES = 4
