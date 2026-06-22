package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.contracts.PremiumRateDN

fun PremiumRateDN.toSpcPremiumRateOption(): SpcPremiumRateOptionPR = SpcPremiumRateOptionPR(
    code = spcrateCode.orEmpty(),
    description = spcrateDescription.orEmpty(),
    insurancePercent = insurDpercent,
)

fun List<PremiumRateDN>.toSpcPremiumRateOptions(): List<SpcPremiumRateOptionPR> =
    map { it.toSpcPremiumRateOption() }
