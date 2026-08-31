package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contractFlow.FreelancePremiumRangePR
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN

fun FreelancePremiumRangeDN.toPresentation(): FreelancePremiumRangePR = FreelancePremiumRangePR(
    lowPremium = lowPremium,
    highPremium = highPremium,
    paymentTabayi = paymentTabayi,
    history = history,
)
