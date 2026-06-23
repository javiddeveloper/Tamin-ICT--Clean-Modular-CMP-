package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper

import com.tamin.taminhamrah.model.studentContract.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.contracts.PremiumRateDN

fun PremiumRateDN.toSpcPremiumRateOption(): SpcPremiumRateOptionPR = SpcPremiumRateOptionPR(
    code = spcrateCode?:"",
    description = spcrateDescription?:"",
    insurancePercent = insurDpercent,
)

fun List<PremiumRateDN>.toSpcPremiumRateOptions(): List<SpcPremiumRateOptionPR> =
    map { it.toSpcPremiumRateOption() }
