package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.FreelancePremiumRangeEntity
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDTO
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams

internal fun FreelancePremiumRangeDTO.toDomain(): FreelancePremiumRangeDN = FreelancePremiumRangeDN(
    paymentTabayi = paymentTabayi ?: 0L,
    lowPremium = lowPremium ?: 0L,
    history = history ?: 0,
    highPremium = highPremium ?: 0L,
)

internal fun FreelancePremiumRangeDN.toEntity(params: FreelancePremiumRangeParams): FreelancePremiumRangeEntity =
    FreelancePremiumRangeEntity(
        id = params.id,
        treatmentSupportCode = params.treatmentSupportCode,
        spcRateCode = params.spcRateCode,
        insuranceId = params.insuranceId,
        paymentTabayi = paymentTabayi,
        lowPremium = lowPremium,
        highPremium = highPremium,
        history = history,
    )

internal fun FreelancePremiumRangeEntity.toDomain(): FreelancePremiumRangeDN = FreelancePremiumRangeDN(
    paymentTabayi = paymentTabayi,
    lowPremium = lowPremium,
    history = history,
    highPremium = highPremium,
)
