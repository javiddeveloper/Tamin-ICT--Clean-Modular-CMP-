package com.tamin.taminhamrah.feature.retirementPension.ui

import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.WageDetailDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The wage and pension arithmetic ported from the legacy app. */
class RetirementHistoryCalculatorTest {

    @Test
    fun normalizeSpillsDaysIntoMonthsAndMonthsIntoYears() {
        val duration = RetirementHistoryCalculator.normalize(years = 30, months = 14, days = 65)
        // 65 days = 2 months + 5 days; 14 + 2 = 16 months = 1 year + 4 months.
        assertEquals(31, duration.years)
        assertEquals(4, duration.months)
        assertEquals(5, duration.days)
    }

    @Test
    fun normalizeLeavesAnAlreadyTidyDurationAlone() {
        val duration = RetirementHistoryCalculator.normalize(years = 31, months = 4, days = 12)
        assertEquals(31, duration.years)
        assertEquals(4, duration.months)
        assertEquals(12, duration.days)
    }

    @Test
    fun averageWageSpreadsTheLastTwoYearsOverTwentyFourMonths() {
        // Two years of full months at a flat wage must average back to that wage.
        val items = listOf(yearOf(days = 30, wage = 10_000_000), yearOf(days = 30, wage = 10_000_000))
        assertEquals(10_000_000L, RetirementHistoryCalculator.averageWage(items))
    }

    @Test
    fun averageWagePrefersTheMostRecentMonths() {
        // Older year is worthless, newest year pays 24m; only the newest 730 days should count.
        val items = listOf(yearOf(days = 30, wage = 0), yearOf(days = 30, wage = 24_000_000))
        val average = RetirementHistoryCalculator.averageWage(items)
        assertTrue(average > 0L, "the recent year must reach the average, was $average")
    }

    @Test
    fun averageWageIgnoresMonthsWithNoContribution() {
        val item = DastmozdInfoItemDN(
            wageDetails = List(12) { index ->
                if (index < 6) WageDetailDN("30", "10000000") else WageDetailDN(null, null)
            },
            hisyear = "1403", id = null, risufname = null, risubirthdate = null,
            risuidserial2 = null, risuidserial1 = null, rwshname = null, expcitycode = null,
            brhcode = null, risuidno = null, risudname = null, risuid = null, risulname = null,
            risunatcode = null, brhname = null, historytypedesc = null, rwshid = null,
        )
        // Six paid months of 10m spread over the 24-month window.
        assertEquals(2_500_000L, RetirementHistoryCalculator.averageWage(listOf(item)))
    }

    @Test
    fun averageWageOfNothingIsZeroRatherThanACrash() {
        assertEquals(0L, RetirementHistoryCalculator.averageWage(emptyList()))
    }

    @Test
    fun estimatedPensionScalesWithCreditedYears() {
        // 20 years of a 30,000,000 monthly wage: 1,000,000 a day x 20 years.
        val estimate = RetirementHistoryCalculator.estimatedPension(
            averageWage = 30_000_000L,
            totalHistoryDays = 20 * 365,
        )
        assertEquals(20_000_000L, estimate)
    }

    @Test
    fun estimatedPensionStopsCreditingPastThirtyFiveYears() {
        val atCap = RetirementHistoryCalculator.estimatedPension(30_000_000L, 35 * 365)
        val wellPast = RetirementHistoryCalculator.estimatedPension(30_000_000L, 45 * 365)
        assertEquals(atCap, wellPast)
    }

    @Test
    fun estimatedPensionNeverFallsBelowTheLegalMinimum() {
        val estimate = RetirementHistoryCalculator.estimatedPension(
            averageWage = 1_000_000L,
            totalHistoryDays = 25 * 365,
        )
        assertEquals(RetirementHistoryCalculator.MINIMUM_MONTHLY_PENSION, estimate)
    }

    @Test
    fun noWageMeansNoEstimateRatherThanTheFloor() {
        assertEquals(0L, RetirementHistoryCalculator.estimatedPension(0L, 25 * 365))
    }

    /** Twelve months of the same length and wage. */
    private fun yearOf(days: Int, wage: Long) = DastmozdInfoItemDN(
        wageDetails = List(12) { WageDetailDN(days.toString(), wage.toString()) },
        hisyear = "1403", id = null, risufname = null, risubirthdate = null,
        risuidserial2 = null, risuidserial1 = null, rwshname = null, expcitycode = null,
        brhcode = null, risuidno = null, risudname = null, risuid = null, risulname = null,
        risunatcode = null, brhname = null, historytypedesc = null, rwshid = null,
    )
}
