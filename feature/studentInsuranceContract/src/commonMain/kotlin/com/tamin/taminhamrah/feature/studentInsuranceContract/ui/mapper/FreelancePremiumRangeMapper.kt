package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper

import com.tamin.taminhamrah.model.studentContract.FreelancePremiumRangePR
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN

fun FreelancePremiumRangeDN.toPresentation(): FreelancePremiumRangePR = FreelancePremiumRangePR(
    lowPremium = lowPremium,
    highPremium = highPremium,
    paymentTabayi = paymentTabayi,
    history = history,
)
