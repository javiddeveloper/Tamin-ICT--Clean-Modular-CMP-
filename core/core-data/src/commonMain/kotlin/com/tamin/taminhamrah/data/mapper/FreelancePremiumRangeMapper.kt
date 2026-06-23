package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO

internal fun FreelancePremiumRangeDTO.toDomain(): FreelancePremiumRangeDN = FreelancePremiumRangeDN(
    paymentTabayi = paymentTabayi ?: 0L,
    lowPremium = lowPremium ?: 0L,
    history = history ?: 0,
    highPremium = highPremium ?: 0L,
)
