package com.tamin.taminhamrah.feature.retirementPension.ui

import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import kotlin.math.ceil

/**
 * The insurance-history figures step 5 shows, worked out the way the legacy app does it
 * (`Utility.calculateDayAndWageOfHistory` / `Utility.normalizeHistoryDuration`).
 *
 * Kept as pure functions on plain numbers so the arithmetic is testable and stays off the
 * presentation edge — the digits are only made Persian when they are drawn.
 */
internal object RetirementHistoryCalculator {

    /**
     * The legal minimum monthly pension, in rial.
     *
     * A figure the government restates every year; ported as-is from the legacy app's
     * `basicWage` so today's app shows today's numbers. **It has to be revisited each Persian new
     * year** — an estimate that silently uses last year's floor is wrong, not merely stale.
     */
    const val MINIMUM_MONTHLY_PENSION: Long = 11_112_690L

    /** Wages of the last this-many days feed the average — two years, per the legacy rule. */
    private const val AVERAGE_WINDOW_DAYS = 730

    /** Months the average is spread over. */
    private const val AVERAGE_WINDOW_MONTHS = 24

    /** A month, for turning a daily rate into a monthly one. */
    private const val DAYS_PER_MONTH = 30

    /** Contribution years below which the floor is prorated rather than applied whole. */
    private const val FULL_FLOOR_YEARS = 20.0

    private const val DAYS_PER_YEAR = 365.0

    /** The estimate is reported to the nearest thousand rial. */
    private const val ROUNDING_STEP = 1_000L
    private const val ROUNDING_HALF = ROUNDING_STEP / 2

    /** A duration carried as separate units, already normalized. */
    data class Duration(val years: Int, val months: Int, val days: Int)

    /**
     * Spills overflowing days into months and overflowing months into years.
     *
     * The service reports the three counts independently, and they routinely arrive un-normalized —
     * 40 days, 14 months — which reads as nonsense next to a "years / months / days" caption.
     */
    fun normalize(years: Int, months: Int, days: Int): Duration {
        val carriedMonths = months + days / DAYS_PER_MONTH
        return Duration(
            years = years + carriedMonths / 12,
            months = carriedMonths % 12,
            days = days % DAYS_PER_MONTH,
        )
    }

    /**
     * Mean monthly wage over the most recent [AVERAGE_WINDOW_DAYS] of contributions.
     *
     * Walks the reported months newest-first and stops once two years are covered, then divides by
     * 24 — so a part-covered final month still contributes its wage, exactly as the legacy
     * calculation does.
     */
    fun averageWage(items: List<DastmozdInfoItemDN>): Long {
        val entries = items.flatMap(::monthlyEntries)
        var coveredDays = 0
        var totalWage = 0L
        for ((days, wage) in entries.asReversed()) {
            if (coveredDays >= AVERAGE_WINDOW_DAYS) break
            coveredDays += days
            totalWage += wage
        }
        if (totalWage == 0L) return 0L
        return ceil(totalWage.toDouble() / AVERAGE_WINDOW_MONTHS).toLong()
    }

    /**
     * Estimated monthly pension: the daily wage times the contribution years, floored at the legal
     * minimum.
     *
     * In accordance with `old_android` (`Utility.calculateDayAndWageOfHistory`), credited service years
     * are not capped at 35.
     *
     * Below [FULL_FLOOR_YEARS] the floor is scaled by `years / 30` rather than applied whole. That
     * divisor is the legacy app's, and it is not a month count — it is reproduced here verbatim
     * because the old app decides the numbers this service is compared against.
     */
    fun estimatedPension(averageWage: Long, totalHistoryDays: Int): Long {
        if (averageWage <= 0L) return 0L
        val contributionYears = totalHistoryDays / DAYS_PER_YEAR
        // Rounded to the nearest thousand, the way the design prints it: an estimate carrying
        // single rials claims a precision this calculation does not have. The floor below is left
        // exact — it is a legal figure, not an estimate, and must not be rounded past.
        val estimate =
            roundToThousand(ceil(averageWage.toDouble() / DAYS_PER_MONTH * contributionYears).toLong())

        val floor = if (contributionYears >= FULL_FLOOR_YEARS) {
            MINIMUM_MONTHLY_PENSION
        } else {
            ceil(contributionYears / DAYS_PER_MONTH * MINIMUM_MONTHLY_PENSION).toLong()
        }
        return maxOf(estimate, floor)
    }

    private fun roundToThousand(value: Long): Long =
        (value + ROUNDING_HALF) / ROUNDING_STEP * ROUNDING_STEP

    /**
     * The (days, wage) pairs one yearly row carries, skipping months with no contribution.
     *
     * `wageDetails` arrives in calendar order (فروردین first) because the mapper builds it from
     * `hismon1..hismon12`, which is what lets [averageWage] walk it in reverse to get the most
     * recent months.
     */
    private fun monthlyEntries(item: DastmozdInfoItemDN): List<Pair<Int, Long>> =
        item.wageDetails.mapNotNull { detail ->
            val days = detail.month?.trim()?.toIntOrNull() ?: return@mapNotNull null
            val wage = detail.wage?.trim()?.toLongOrNull() ?: 0L
            days to wage
        }
}
