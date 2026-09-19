package com.tamin.taminhamrah.useCases.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.BASIC_WAGE
import com.tamin.taminhamrah.model.calculateWagePension.DAYS_IN_MONTH
import com.tamin.taminhamrah.model.calculateWagePension.DAYS_IN_YEAR
import com.tamin.taminhamrah.model.calculateWagePension.MONTHS_IN_TWO_YEARS
import com.tamin.taminhamrah.model.calculateWagePension.MONTHS_IN_YEAR
import com.tamin.taminhamrah.model.calculateWagePension.TWO_YEAR_DAYS
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionCalculationDN
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionChartItemDN
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import kotlin.math.ceil
import kotlin.math.floor

class CalculateWagePensionUseCase {
    operator fun invoke(
        talfigh: TalfighInfoDN,
        dastmozd: DastmozdInfoDN
    ): WagePensionCalculationDN {
        val firstItem = talfigh.list?.firstOrNull()
        val totalHistoryDays = firstItem?.sumHistoryYears ?: 0
        val premiumPaymentHistoryYear = totalHistoryDays.toDouble() / DAYS_IN_YEAR
        val roundedPremiumYears = roundHalfEvenToTwoDecimals(premiumPaymentHistoryYear)

        val (averageSalary, eligibleAmount, legalFloorApplied) = calculateAmounts(
            list = dastmozd.list.orEmpty(),
            premiumYears = roundedPremiumYears
        )

        val (historyYears, historyMonths, historyDays) = normalizeHistoryDuration(
            years = firstItem?.historyYears ?: 0,
            months = firstItem?.historyMonths ?: 0,
            days = firstItem?.historyDays ?: 0,
        )

        return WagePensionCalculationDN(
            premiumPaymentHistoryYear = premiumPaymentHistoryYear,
            averageSalaryLastTwoYears = averageSalary,
            eligibleAmountPension = eligibleAmount,
            historyYears = historyYears,
            historyMonths = historyMonths,
            historyDays = historyDays,
            totalHistoryDays = totalHistoryDays,
            chartItems = talfigh.list.orEmpty().map { item ->
                WagePensionChartItemDN(
                    hisYear = item.hisYear.orEmpty(),
                    sumYear = item.sumYear ?: 0,
                    months = List(MONTHS_IN_YEAR) { index ->
                        item.months.getOrNull(index)?.toIntOrNull() ?: 0
                    },
                )
            },
            legalFloorApplied = legalFloorApplied,
        )
    }

    /**
     * Carries days into months (≥ [DAYS_IN_MONTH]) and months into years (≥ [MONTHS_IN_YEAR]),
     * matching legacy `Utility.normalizeHistoryDuration` before display.
     */
    private fun normalizeHistoryDuration(
        years: Int,
        months: Int,
        days: Int,
    ): Triple<Int, Int, Int> {
        var normalizedYears = years
        var normalizedMonths = months + days / DAYS_IN_MONTH
        val normalizedDays = days % DAYS_IN_MONTH
        normalizedYears += normalizedMonths / MONTHS_IN_YEAR
        normalizedMonths %= MONTHS_IN_YEAR
        return Triple(normalizedYears, normalizedMonths, normalizedDays)
    }

    private fun calculateAmounts(
        list: List<DastmozdInfoItemDN>,
        premiumYears: Double
    ): Triple<Long, Long, Boolean> {
        val listDays = ArrayList<String>()
        val listWages = ArrayList<String>()

        for (i in list.lastIndex downTo 0) {
            for (detail in list[i].wageDetails.asReversed()) {
                val month = detail.month
                val wage = detail.wage
                if (month != null && wage != null) {
                    listDays.add(month)
                    listWages.add(wage)
                }
            }
        }

        var sumDays = 0
        var sumWages = 0.0
        for (index in listDays.indices) {
            if (sumDays >= TWO_YEAR_DAYS) break
            sumDays += listDays[index].toIntOrNull() ?: 0
            sumWages += listWages[index].toIntOrNull() ?: 0
        }

        val averageSalary = ceil(sumWages / MONTHS_IN_TWO_YEARS)
        var eligibleAmount = ceil((averageSalary / DAYS_IN_MONTH) * premiumYears)
        var legalFloorApplied = false
        if (premiumYears >= 20 && eligibleAmount < BASIC_WAGE) {
            eligibleAmount = BASIC_WAGE.toDouble()
            legalFloorApplied = true
        }
        if (premiumYears < 20) {
            val minWage = (premiumYears / DAYS_IN_MONTH) * BASIC_WAGE
            if (eligibleAmount < minWage) {
                eligibleAmount = minWage
                legalFloorApplied = true
            }
        }
        return Triple(averageSalary.toLong(), eligibleAmount.toLong(), legalFloorApplied)
    }
}

internal fun roundHalfEvenToTwoDecimals(value: Double): Double {
    val scaled = value * 100.0
    val floorValue = floor(scaled)
    val fraction = scaled - floorValue
    val rounded = when {
        fraction < 0.5 -> floorValue
        fraction > 0.5 -> floorValue + 1.0
        else -> if (floorValue.toLong() % 2L == 0L) floorValue else floorValue + 1.0
    }
    return rounded / 100.0
}
