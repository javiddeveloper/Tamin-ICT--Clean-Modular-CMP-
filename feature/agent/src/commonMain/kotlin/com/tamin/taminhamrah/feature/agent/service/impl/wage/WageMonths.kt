package com.tamin.taminhamrah.feature.agent.service.impl.wage

import com.tamin.taminhamrah.feature.agent.service.base.AgentDateRange
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN

/**
 * One paid month of a wage-history year. On the wire [WageDetailDN.month] is the number of days
 * worked in that month, and the month itself is the detail's position (index 0 = فروردین).
 */
internal data class PaidMonth(val month: Int, val days: Int, val wage: Long)

internal val DastmozdInfoItemDN.year: Int? get() = hisyear?.trim()?.toIntOrNull()

/** Months with both days and wage recorded — the only months the native services counted. */
internal fun DastmozdInfoItemDN.paidMonths(months: IntRange = 1..12): List<PaidMonth> =
    wageDetails.mapIndexedNotNull { index, detail ->
        val month = index + 1
        if (month !in months) return@mapIndexedNotNull null
        val days = detail.month?.trim()?.toIntOrNull() ?: 0
        val wage = detail.wage?.replace(",", "")?.trim()?.toLongOrNull() ?: 0L
        if (days > 0 && wage > 0) PaidMonth(month, days, wage) else null
    }

/** Days worked over [months]; months with days but no wage still count, as in the native total. */
internal fun DastmozdInfoItemDN.workedDays(months: IntRange = 1..12): Int =
    wageDetails.withIndex()
        .filter { (index, _) -> index + 1 in months }
        .sumOf { (_, detail) -> detail.month?.trim()?.toIntOrNull() ?: 0 }

/** Records whose year is inside [range]; records without a readable year are dropped. */
internal fun List<DastmozdInfoItemDN>.inYears(range: AgentDateRange): List<DastmozdInfoItemDN> =
    filter { item -> item.year?.let(range::containsYear) == true }

/** The newest year with at least one paid month (native `findLastPaidYear`). */
internal fun List<DastmozdInfoItemDN>.lastPaidYear(): DastmozdInfoItemDN? =
    sortedByDescending { it.year ?: 0 }.firstOrNull { it.paidMonths().isNotEmpty() }
