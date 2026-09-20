package com.tamin.taminhamrah.model.history

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HistorySummaryPRTest {

    @Test
    fun `no rows has no year to summarise`() {
        assertNull(emptyList<TalfighInfoItemPR>().toHistorySummary(1405, 6))
    }

    @Test
    fun `current year splits into registered, unpaid and upcoming`() {
        val summary = listOf(row("1405", listOf("0", "0", "30", "31", "0", "30")))
            .toHistorySummary(currentYear = 1405, currentMonth = 6)!!

        assertEquals("۱۴۰۵", summary.yearLabel)
        assertEquals(3, summary.registeredCount)
        assertEquals(3, summary.unpaidCount)
        assertEquals(6, summary.elapsedCount)
        assertEquals(5, summary.currentMonthIndex)
        assertEquals("شهریور", summary.lastRegisteredMonth)
        assertEquals(HistoryMonthStatusPR.Upcoming, summary.months[6])
    }

    /** One year arrives once per employer; the days have to be added, not overwritten. */
    @Test
    fun `months are summed across the rows sharing a year`() {
        val summary = listOf(
            row("1405", listOf("30", "0", "0")),
            row("1405", listOf("0", "20", "0")),
        ).toHistorySummary(currentYear = 1405, currentMonth = 3)!!

        assertEquals(2, summary.registeredCount)
        assertEquals(1, summary.unpaidCount)
    }

    /** The newest year, not the current one — and every month of a past year has elapsed. */
    @Test
    fun `a record that stops short is summarised at its own last year`() {
        val summary = listOf(
            row("1401", listOf("30")),
            row("1402", listOf("30", "30")),
        ).toHistorySummary(currentYear = 1405, currentMonth = 6)!!

        assertEquals("۱۴۰۲", summary.yearLabel)
        assertEquals(-1, summary.currentMonthIndex)
        assertEquals(HistorySummaryPR.MONTHS_IN_YEAR, summary.elapsedCount)
        assertEquals(10, summary.unpaidCount)
        assertEquals("اردیبهشت", summary.lastRegisteredMonth)
    }

    private fun row(year: String, months: List<String>) = TalfighInfoItemPR(
        months = months,
        risuid = "",
        historyYears = 0,
        historyMonths = 0,
        sumYear = 0,
        historyDays = 0,
        sumHistoryYears = 0,
        id = 0,
        hisYear = year,
    )
}
