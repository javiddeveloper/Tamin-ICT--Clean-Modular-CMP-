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
    val totalWage: Long,
    /**
     * One value per column of the chart this row sits under: a year in «همه», a month inside a
     * single year. The cells are a timeline of the same columns, so they must be built in the same
     * order the bars are — see [splitCandidates]'s `yearOrder`.
     */
    val cellDays: ImmutableList<Int>,
    val cellWages: ImmutableList<Long>,
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
 *
 * [yearOrder] is the chart's own column order in «همه» — the years exactly as the bars above are
 * laid out, so each employer's timeline lines up with them column for column. Taken from the caller
 * rather than sorted here, because a timeline that orders its own years is a timeline that silently
 * stops agreeing with the chart the first time the chart's order changes. Ignored inside one year,
 * where the columns are always the twelve months.
 */
fun splitCandidates(
    wageByYear: ImmutableMap<String, ImmutableList<DastmozdInfoItemPR>>,
    year: String?,
    yearOrder: List<String>,
    nameOf: (DastmozdInfoItemPR) -> String,
): ImmutableList<SplitCandidatePR> {
    val rows = if (year == null) {
        wageByYear.values.flatten()
    } else {
        wageByYear[year].orEmpty()
    }
    if (rows.isEmpty()) return emptyCandidates

    // A column per year across the career, a column per month inside one year.
    val columnOfYear = if (year == null) {
        yearOrder.withIndex().associate { (index, it) -> it to index }
    } else {
        null
    }
    val columns = columnOfYear?.size ?: HistoryConstants.MONTHS_IN_YEAR
    if (columns == 0) return emptyCandidates

    // Grouped in first-seen order, then ranked — so the ranking decides what shows, not the map.
    val grouped = LinkedHashMap<String, MutableList<DastmozdInfoItemPR>>()
    rows.forEach { row ->
        grouped.getOrPut(row.splitKey()) { mutableListOf() }.add(row)
    }

    return grouped.entries
        .map { (key, employerRows) ->
            val days = IntArray(columns)
            val wages = LongArray(columns)
            employerRows.forEach { row ->
                if (columnOfYear != null) {
                    // A whole year folds into its one column; a year the chart does not draw — the
                    // wage service reporting one the merged service did not — is left out rather
                    // than shifting every column beside it.
                    val column = columnOfYear[row.hisyear] ?: return@forEach
                    row.wageDetails.forEach { detail ->
                        days[column] += detail.month.toIntOrNull() ?: 0
                        wages[column] += detail.wage.toLongOrNull() ?: 0L
                    }
                } else {
                    row.wageDetails.forEachIndexed { month, detail ->
                        if (month < columns) {
                            days[month] += detail.month.toIntOrNull() ?: 0
                            wages[month] += detail.wage.toLongOrNull() ?: 0L
                        }
                    }
                }
            }
            SplitCandidatePR(
                key = key,
                label = nameOf(employerRows.first()),
                totalDays = days.sum(),
                totalWage = wages.sum(),
                cellDays = days.toList().toImmutableList(),
                cellWages = wages.toList().toImmutableList(),
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
