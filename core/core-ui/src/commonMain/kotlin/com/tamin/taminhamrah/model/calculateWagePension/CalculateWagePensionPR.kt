package com.tamin.taminhamrah.model.calculateWagePension

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MultipleWorkshopPersonalInfoPR(
    val branchCode: String,
    val insuranceNumber: String,
    val branch: String
)

@Immutable
@Serializable
data class MultipleWorkshopResultPR(
    val result: Int,
    val isMultiple: Boolean,
    val pensionAmount: Long
)

@Immutable
@Serializable
data class WagePensionChartItemPR(
    val hisYear: String,
    val sumYear: Int,
    val months: List<Int> = emptyList(),
)

@Immutable
@Serializable
data class WagePensionCalculationPR(
    val premiumPaymentHistoryYear: Double,
    val averageSalaryLastTwoYears: Long,
    val eligibleAmountPension: Long,
    val historyYears: Int,
    val historyMonths: Int,
    val historyDays: Int,
    val totalHistoryDays: Int,
    val chartItems: List<WagePensionChartItemPR>,
    val legalFloorApplied: Boolean = false,
)
