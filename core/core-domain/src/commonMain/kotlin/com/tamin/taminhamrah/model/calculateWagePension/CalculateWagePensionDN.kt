package com.tamin.taminhamrah.model.calculateWagePension

data class MultipleWorkshopPersonalInfoDN(
    val branchCode: String,
    val insuranceNumber: String,
    val branch: String?
)

data class MultipleWorkshopResultDN(
    val result: Int
) {
    val isMultiple: Boolean get() = result == MULTIPLE_WORKSHOPS_YES
    val pensionAmount: Long get() = result.toLong()
}

data class WagePensionChartItemDN(
    val hisYear: String,
    val sumYear: Int,
    val months: List<Int> = emptyList(),
)

data class WagePensionCalculationDN(
    val premiumPaymentHistoryYear: Double,
    val averageSalaryLastTwoYears: Long,
    val eligibleAmountPension: Long,
    val historyYears: Int,
    val historyMonths: Int,
    val historyDays: Int,
    val totalHistoryDays: Int,
    val chartItems: List<WagePensionChartItemDN>,
    val legalFloorApplied: Boolean = false,
)

const val MULTIPLE_WORKSHOPS_YES = 1
const val BASIC_WAGE = 11_112_690L
const val TWO_YEAR_DAYS = 730
const val DAYS_IN_YEAR = 365
const val MONTHS_IN_TWO_YEARS = 24
const val MONTHS_IN_YEAR = 12
const val DAYS_IN_MONTH = 30
