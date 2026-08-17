package com.tamin.taminhamrah.feature.history.ui.model

import com.tamin.taminhamrah.model.history.TalfighInfoItemPR
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YearHistoryTest {

    @Test
    fun mergeByYear_addsTheMonthsOfEveryEmployerReportingTheSameYear() {
        val merged = listOf(
            row(year = "1400", months = List(12) { "10" }),
            row(year = "1400", months = List(12) { "5" }),
        ).mergeByYear()

        assertEquals(1, merged.size, "the same year must not appear twice")
        assertEquals(List(12) { 15 }, merged.first().monthDays)
        assertEquals(180, merged.first().totalDays)
    }

    @Test
    fun mergeByYear_keepsDistinctYearsApartInServerOrder() {
        val merged = listOf(
            row(year = "1402", months = List(12) { "30" }),
            row(year = "1401", months = List(12) { "30" }),
            row(year = "1402", months = List(12) { "1" }),
        ).mergeByYear()

        assertEquals(listOf("1402", "1401"), merged.map { it.year })
        assertEquals(372, merged.first().totalDays, "both 1402 rows should be folded together")
    }

    /** The bug the previous app shipped: the sheet is keyed on the year, never on a position. */
    @Test
    fun mergeByYear_leavesEveryRowIdentifiableByItsOwnYear() {
        val merged = listOf(
            row(year = "1400", months = List(12) { "10" }),
            row(year = "1400", months = List(12) { "10" }),
            row(year = "1399", months = List(12) { "2" }),
        ).mergeByYear()

        assertEquals(2, merged.size)
        assertEquals("1399", merged[1].year)
        assertEquals(24, merged[1].totalDays, "the second card must carry 1399's own days")
    }

    @Test
    fun mergeByYear_treatsMissingAndUnparsableMonthsAsNoDays() {
        val merged = listOf(
            row(year = "1400", months = listOf("31", "", "x", "10")),
        ).mergeByYear()

        assertEquals(12, merged.first().monthDays.size, "always twelve months")
        assertEquals(41, merged.first().totalDays)
    }

    @Test
    fun mergeByYear_returnsNothingForNoRows() {
        assertTrue(emptyList<TalfighInfoItemPR>().mergeByYear().isEmpty())
    }

    @Test
    fun yearIsCompleteOnlyFromAFullYearOfCover() {
        val merged = listOf(
            row(year = "1402", months = List(12) { "31" }),
            row(year = "1401", months = List(12) { "30" }),
        ).mergeByYear()

        assertTrue(merged[0].isComplete, "372 days is a full year")
        assertFalse(merged[1].isComplete, "360 days is short of 365")
    }

    @Test
    fun careerTotal_carriesDaysIntoMonthsAndMonthsIntoYears() {
        val total = listOf(
            row(year = "1400", months = emptyList(), years = 2, monthsCount = 13, days = 65),
        ).careerTotal()

        // 65 days = 2 months + 5 days; 13 + 2 = 15 months = 1 year + 3 months.
        assertEquals(3, total.years)
        assertEquals(3, total.months)
        assertEquals(5, total.days)
    }

    @Test
    fun careerTotal_readsOnlyTheFirstRow_whichIsWhereTheServicePutsIt() {
        val total = listOf(
            row(year = "1402", months = emptyList(), years = 4, monthsCount = 0, days = 0),
            row(year = "1401", months = emptyList(), years = 99, monthsCount = 99, days = 99),
        ).careerTotal()

        assertEquals(4, total.years)
        assertEquals(0, total.months)
    }

    @Test
    fun careerTotal_isZeroWhenNothingCameBack() {
        val total = emptyList<TalfighInfoItemPR>().careerTotal()

        assertEquals(0, total.years)
        assertEquals(0, total.totalDays)
    }

    private fun row(
        year: String,
        months: List<String>,
        years: Int = 0,
        monthsCount: Int = 0,
        days: Int = 0,
    ) = TalfighInfoItemPR(
        months = months,
        risuid = "1",
        historyYears = years,
        historyMonths = monthsCount,
        sumYear = 0,
        historyDays = days,
        sumHistoryYears = 0,
        id = 0,
        hisYear = year,
    )
}
