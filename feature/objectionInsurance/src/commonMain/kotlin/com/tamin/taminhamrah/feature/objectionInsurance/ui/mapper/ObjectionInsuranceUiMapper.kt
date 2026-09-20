package com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper

import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionDelta
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionMonthBarPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionMonthRowPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionSeasonGroupPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionWorkshopPickerRowPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionYearCardPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionYearSubtitle
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.StagedYearChipPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.YearCompletionStatus
import com.tamin.taminhamrah.mapper.objectionInsurance.toDomain
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryPR
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toEnglishDigits
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val DAYS_IN_YEAR = 365
private const val MIN_RING_FRACTION = 0.02f

/** A year with at least this many registered/declared days is shown as complete, not deficient. */
private const val COMPLETE_DAYS_THRESHOLD = 350

/**
 * One card per year, grouping every record ([ObjectionInsuranceHistoryPR]) that shares it.
 *
 * Mirrors the ring/color/subtitle rules captured from the product's own design mockup: the ring
 * shows edited (blue) when this session staged a change for the year, otherwise complete (green,
 * >=350 declared days) or deficient (orange).
 */
fun buildYearCards(
    records: List<ObjectionInsuranceHistoryPR>,
    edits: Map<Int, Map<Int, String>>,
): ImmutableList<ObjectionYearCardPR> = records.withIndex()
    .groupBy { (_, record) -> record.year }
    .entries
    .sortedByDescending { it.key.toIntOrNull() ?: 0 }
    .map { (year, indexed) ->
        val recordIndices = indexed.map { it.index }
        val edited = recordIndices.any { index -> edits[index]?.values?.any { it.isNotBlank() } == true }
        val registeredTotal = indexed.sumOf { (_, record) -> record.totalRegisteredDays() }
        val declaredTotal = indexed.sumOf { (index, record) -> record.totalDeclaredDays(edits[index].orEmpty()) }
        val shown = minOf(DAYS_IN_YEAR, if (edited) declaredTotal else registeredTotal)
        val fraction = maxOf(MIN_RING_FRACTION, shown / DAYS_IN_YEAR.toFloat())
        val status = when {
            edited -> YearCompletionStatus.Edited
            shown >= COMPLETE_DAYS_THRESHOLD -> YearCompletionStatus.Complete
            else -> YearCompletionStatus.Deficit
        }
        val subtitle = when {
            edited -> ObjectionYearSubtitle.Edited
            indexed.size > 1 -> ObjectionYearSubtitle.WorkshopCount(indexed.size)
            else -> ObjectionYearSubtitle.TotalDays(registeredTotal)
        }
        ObjectionYearCardPR(
            year = year.toPersianDigits(),
            subtitle = subtitle,
            days = shown.toString().toPersianDigits(),
            fraction = fraction,
            status = status,
            recordIndices = recordIndices.toImmutableList(),
        )
    }
    .toImmutableList()

/** One chip per year that currently has at least one real staged edit, newest first. */
fun buildStagedYearChips(
    records: List<ObjectionInsuranceHistoryPR>,
    edits: Map<Int, Map<Int, String>>,
): ImmutableList<StagedYearChipPR> = records.withIndex()
    .groupBy { (_, record) -> record.year }
    .entries
    .sortedByDescending { it.key.toIntOrNull() ?: 0 }
    .mapNotNull { (year, indexed) ->
        val recordIndices = indexed.map { it.index }
        val edited = recordIndices.filter { edits[it]?.hasRealEdit() == true }
        if (edited.isEmpty()) return@mapNotNull null
        StagedYearChipPR(label = year.toPersianDigits(), recordIndices = edited.toImmutableList())
    }
    .toImmutableList()

/** The 12 registered day values, index 0 = فروردین, in calendar order. */
fun ObjectionInsuranceHistoryPR.registeredMonthValues(): List<Int> = listOf(
    oldMonth1, oldMonth2, oldMonth3, oldMonth4, oldMonth5, oldMonth6,
    oldMonth7, oldMonth8, oldMonth9, oldMonth10, oldMonth11, oldMonth12,
).map { it.toIntOrNull() ?: 0 }

private fun ObjectionInsuranceHistoryPR.totalRegisteredDays(): Int = registeredMonthValues().sum()

private fun ObjectionInsuranceHistoryPR.totalDeclaredDays(recordEdits: Map<Int, String>): Int =
    registeredMonthValues().mapIndexed { month, old -> recordEdits[month]?.toIntOrNull() ?: old }.sum()

/** Jalali month names, month labels for the detail screen's chart + table. */
private val monthLabels = PersianDateFormatter.monthNames

/** Jalali seasons in calendar order — the table groups 3 months under each. */
private val seasonLabels = listOf("بهار", "تابستان", "پاییز", "زمستان")

private fun ObjectionInsuranceHistoryPR.maxDaysFor(month0Based: Int): Int =
    year.toIntOrNull()?.let { PersianDateFormatter.daysInMonth(it, month0Based + 1) } ?: DEFAULT_MAX_DAYS

/** The month table's 4 season groups for [record], with [draft] overlaid as the typed value. */
fun buildSeasonGroups(
    record: ObjectionInsuranceHistoryPR,
    draft: Map<Int, String>,
): ImmutableList<ObjectionSeasonGroupPR> {
    val registered = record.registeredMonthValues()
    val rows = List(MONTHS_IN_YEAR) { index ->
        ObjectionMonthRowPR(
            index = index,
            label = monthLabels[index],
            registeredDays = registered[index],
            isRegistered = registered[index] > 0,
            maxDays = record.maxDaysFor(index),
            value = draft[index].orEmpty(),
        )
    }
    return seasonLabels.indices.map { season ->
        ObjectionSeasonGroupPR(
            seasonLabel = seasonLabels[season],
            rows = rows.subList(season * MONTHS_PER_SEASON, season * MONTHS_PER_SEASON + MONTHS_PER_SEASON)
                .toImmutableList(),
        )
    }.toImmutableList()
}

/**
 * One two-tone bar per month for the chart: the registered portion (green when the month is fully
 * covered, orange otherwise) plus whatever [draft] adds on top of it, in blue.
 */
fun buildMonthBars(
    record: ObjectionInsuranceHistoryPR,
    draft: Map<Int, String>,
): ImmutableList<ObjectionMonthBarPR> {
    val registered = record.registeredMonthValues()
    return List(MONTHS_IN_YEAR) { index ->
        val maxDays = record.maxDaysFor(index).coerceAtLeast(1)
        val rec = registered[index].coerceIn(0, maxDays)
        val declared = draft[index]?.toIntOrNull()?.coerceIn(0, maxDays) ?: rec
        val extra = (declared - rec).coerceAtLeast(0)
        ObjectionMonthBarPR(
            index = index,
            label = monthLabels[index],
            recFraction = rec / maxDays.toFloat(),
            isFull = rec >= maxDays,
            extraFraction = extra / maxDays.toFloat(),
        )
    }.toImmutableList()
}

/** How this record's declared total compares to what's registered, for the chart's delta pill. */
fun buildDetailDelta(record: ObjectionInsuranceHistoryPR, draft: Map<Int, String>): ObjectionDelta {
    val registered = record.registeredMonthValues()
    val declaredTotal = registered.mapIndexed { index, old -> draft[index]?.toIntOrNull() ?: old }.sum()
    val delta = declaredTotal - registered.sum()
    return when {
        delta == 0 -> ObjectionDelta.None
        delta > 0 -> ObjectionDelta.Added(delta)
        else -> ObjectionDelta.Reduced(-delta)
    }
}

/** One card per workshop record for [year] — the multi-workshop picker's list. */
fun buildWorkshopPickerRows(
    records: List<ObjectionInsuranceHistoryPR>,
    edits: Map<Int, Map<Int, String>>,
    year: String,
): ImmutableList<ObjectionWorkshopPickerRowPR> = records.withIndex()
    .filter { (_, record) -> record.year == year }
    .map { (index, record) ->
        val registered = record.registeredMonthValues()
        val opacities = registered.mapIndexed { month, days ->
            val maxDays = record.maxDaysFor(month)
            if (maxDays > 0) (days / maxDays.toFloat()).coerceIn(0f, 1f) else 0f
        }
        ObjectionWorkshopPickerRowPR(
            recordIndex = index,
            workshopName = record.workshopName,
            meta = record.historyTypeName,
            monthOpacities = opacities.toImmutableList(),
            totalDays = registered.sum(),
            isEdited = edits[index]?.hasRealEdit() == true,
        )
    }
    .toImmutableList()

/**
 * Normalizes a day-count as the user types it: Persian digits become ASCII, everything else is
 * dropped, and the result is clamped to [maxDays] — mirroring the product's own input behaviour
 * (typing past a month's length snaps back to it rather than rejecting the keystroke).
 */
fun sanitizeDayInput(raw: String, maxDays: Int): String {
    val digitsOnly = raw.toEnglishDigits().filter(Char::isDigit)
    if (digitsOnly.isEmpty()) return ""
    val clamped = digitsOnly.toIntOrNull()?.coerceIn(0, maxDays) ?: return ""
    return clamped.toString()
}

/** A record counts as edited only once it carries a real, non-zero declared value. */
fun Map<Int, String>.hasRealEdit(): Boolean = values.any { it.isNotBlank() && it.toIntOrNull() != 0 }

/** Writes [edits] (month 0..11 -> new value) onto this record's flat `newMonthX` fields. */
private fun ObjectionInsuranceHistoryDN.withMonthEdits(edits: Map<Int, String>): ObjectionInsuranceHistoryDN {
    var result = this
    edits.forEach { (month, value) ->
        result = when (month) {
            0 -> result.copy(newMonth1 = value)
            1 -> result.copy(newMonth2 = value)
            2 -> result.copy(newMonth3 = value)
            3 -> result.copy(newMonth4 = value)
            4 -> result.copy(newMonth5 = value)
            5 -> result.copy(newMonth6 = value)
            6 -> result.copy(newMonth7 = value)
            7 -> result.copy(newMonth8 = value)
            8 -> result.copy(newMonth9 = value)
            9 -> result.copy(newMonth10 = value)
            10 -> result.copy(newMonth11 = value)
            11 -> result.copy(newMonth12 = value)
            else -> result
        }
    }
    return result
}

/**
 * Legacy `ObjectionInsuranceHistoryModel.setDefaultValue()` plus the detail dialog's
 * `editedValue = newMonth ?: "0"` seed: blank/null `mm*` become `"0"`, and `prow`/`reqno`/
 * `reqtype` are cleared to null before `saveconflict`.
 */
private fun ObjectionInsuranceHistoryDN.withLegacySaveDefaults(): ObjectionInsuranceHistoryDN = copy(
    newMonth1 = newMonth1.blankToZero(),
    newMonth2 = newMonth2.blankToZero(),
    newMonth3 = newMonth3.blankToZero(),
    newMonth4 = newMonth4.blankToZero(),
    newMonth5 = newMonth5.blankToZero(),
    newMonth6 = newMonth6.blankToZero(),
    newMonth7 = newMonth7.blankToZero(),
    newMonth8 = newMonth8.blankToZero(),
    newMonth9 = newMonth9.blankToZero(),
    newMonth10 = newMonth10.blankToZero(),
    newMonth11 = newMonth11.blankToZero(),
    newMonth12 = newMonth12.blankToZero(),
    prow = null,
    requestNumber = null,
    requestType = null,
    userDesc = "",
    isDeleted = false,
)

private fun String?.blankToZero(): String = if (isNullOrBlank()) "0" else this

/** The records to send to `saveconflict` — only the ones this session actually declared a change for. */
fun buildEditedRecords(
    records: List<ObjectionInsuranceHistoryPR>,
    edits: Map<Int, Map<Int, String>>,
): List<ObjectionInsuranceHistoryDN> = edits
    .filterValues { it.hasRealEdit() }
    .mapNotNull { (index, recordEdits) ->
        records.getOrNull(index)
            ?.toDomain()
            ?.withMonthEdits(recordEdits)
            ?.withLegacySaveDefaults()
    }

private const val MONTHS_IN_YEAR = 12
private const val MONTHS_PER_SEASON = 3
private const val DEFAULT_MAX_DAYS = 31
