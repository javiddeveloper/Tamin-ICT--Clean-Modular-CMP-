package com.tamin.taminhamrah.feature.agent.service.base

/** A Jalali date the assistant sent as `YYYYMMDD`; month and day are null when not given. */
data class AgentDate(val year: Int, val month: Int?, val day: Int?) {
    /** `YYYYMM`, the shape several services filter on. */
    val yearMonth: Int? get() = month?.let { year * 100 + it }
}

data class AgentDateRange(val start: AgentDate?, val end: AgentDate?) {
    val isEmpty: Boolean get() = start == null && end == null

    fun containsYear(year: Int): Boolean =
        (start == null || year >= start.year) && (end == null || year <= end.year)

    /**
     * The months of [year] inside the range: a year at either edge is trimmed to the edge month,
     * every other year is whole. Mirrors the native wage services.
     */
    fun monthsOf(year: Int): IntRange {
        val first = if (start != null && year == start.year) start.month ?: 1 else 1
        val last = if (end != null && year == end.year) end.month ?: 12 else 12
        return first.coerceIn(1, 12)..last.coerceIn(1, 12)
    }
}

/**
 * Reads `startDate` / `endDate` from the assistant's filters. Unlike the native parser this never
 * throws on a short or malformed value — it is simply ignored.
 */
fun AgentServiceParams.dateRange(): AgentDateRange {
    val filters = getFilters()
    return AgentDateRange(filters["startDate"].toAgentDate(), filters["endDate"].toAgentDate())
}

internal fun String?.toAgentDate(): AgentDate? {
    val digits = this?.filter { it.isDigit() } ?: return null
    if (digits.length < 4) return null
    val year = digits.substring(0, 4).toIntOrNull() ?: return null
    val month = digits.takeIf { it.length >= 6 }?.substring(4, 6)?.toIntOrNull()?.takeIf { it in 1..12 }
    val day = digits.takeIf { it.length >= 8 }?.substring(6, 8)?.toIntOrNull()?.takeIf { it in 1..31 }
    return AgentDate(year, month, day)
}
