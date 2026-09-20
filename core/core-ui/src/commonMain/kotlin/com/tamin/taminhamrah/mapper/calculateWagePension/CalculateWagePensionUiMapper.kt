package com.tamin.taminhamrah.mapper.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDN
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoPR
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDN
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultPR
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionCalculationDN
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionCalculationPR
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionChartItemDN
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionChartItemPR

fun MultipleWorkshopPersonalInfoDN.toPresentation(): MultipleWorkshopPersonalInfoPR {
    return MultipleWorkshopPersonalInfoPR(
        branchCode = branchCode,
        insuranceNumber = insuranceNumber,
        branch = branch.orEmpty()
    )
}

fun MultipleWorkshopResultDN.toPresentation(): MultipleWorkshopResultPR {
    return MultipleWorkshopResultPR(
        result = result,
        isMultiple = isMultiple,
        pensionAmount = pensionAmount
    )
}

fun WagePensionChartItemDN.toPresentation(): WagePensionChartItemPR {
    return WagePensionChartItemPR(
        hisYear = hisYear,
        sumYear = sumYear,
        months = months,
    )
}

fun WagePensionCalculationDN.toPresentation(): WagePensionCalculationPR {
    return WagePensionCalculationPR(
        premiumPaymentHistoryYear = premiumPaymentHistoryYear,
        averageSalaryLastTwoYears = averageSalaryLastTwoYears,
        eligibleAmountPension = eligibleAmountPension,
        historyYears = historyYears,
        historyMonths = historyMonths,
        historyDays = historyDays,
        totalHistoryDays = totalHistoryDays,
        chartItems = chartItems.map { it.toPresentation() },
        legalFloorApplied = legalFloorApplied,
    )
}
