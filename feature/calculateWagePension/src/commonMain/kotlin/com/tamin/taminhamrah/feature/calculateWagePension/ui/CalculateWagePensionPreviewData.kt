package com.tamin.taminhamrah.feature.calculateWagePension.ui

import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionUiState
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionCalculationPR
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionChartItemPR
import kotlinx.collections.immutable.persistentListOf

internal object CalculateWagePensionPreviewData {
    val chartItems = persistentListOf(
        WagePensionChartItemPR(
            hisYear = "1401",
            sumYear = 42,
            months = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 14, 14),
        ),
        WagePensionChartItemPR(
            hisYear = "1402",
            sumYear = 365,
            months = List(12) { if (it < 6) 31 else 30 },
        ),
        WagePensionChartItemPR(
            hisYear = "1403",
            sumYear = 366,
            months = List(12) { index ->
                when {
                    index < 6 -> 31
                    index < 11 -> 30
                    else -> 30
                }
            },
        ),
        WagePensionChartItemPR(
            hisYear = "1404",
            sumYear = 165,
            months = listOf(31, 31, 31, 31, 31, 10, 0, 0, 0, 0, 0, 0),
        ),
        WagePensionChartItemPR(
            hisYear = "1405",
            sumYear = 118,
            months = listOf(31, 31, 31, 25, 0, 0, 0, 0, 0, 0, 0, 0),
        ),
    )

    val calculation = WagePensionCalculationPR(
        premiumPaymentHistoryYear = 7.34,
        averageSalaryLastTwoYears = 185_000_000L,
        eligibleAmountPension = 12_450_000L,
        historyYears = 7,
        historyMonths = 4,
        historyDays = 0,
        totalHistoryDays = 2675,
        chartItems = chartItems,
        legalFloorApplied = true,
    )

    val loadedState = CalculateWagePensionUiState(
        isLoading = false,
        calculation = calculation,
        displayedEligibleAmount = calculation.eligibleAmountPension,
    )

    val loadingState = CalculateWagePensionUiState(isLoading = true)

    val errorState = CalculateWagePensionUiState(
        isLoading = false,
        error = "خطا در دریافت اطلاعات",
    )
}
