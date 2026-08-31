package com.tamin.taminhamrah.feature.contractFlow.ui.mapper

import com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.contracts.PremiumRateDN

fun PremiumRateDN.toSpcPremiumRateOption(): SpcPremiumRateOptionPR = SpcPremiumRateOptionPR(
    code = spcrateCode ?: "",
    description = spcrateDescription ?: "",
    insurancePercent = insurDpercent,
)

fun List<PremiumRateDN>.toSpcPremiumRateOptions(): List<SpcPremiumRateOptionPR> =
    map { it.toSpcPremiumRateOption() }
