package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.SpcPremiumRateEntity
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO

internal fun PremiumRateDTO.toEntity(): SpcPremiumRateEntity = SpcPremiumRateEntity(
    spcrateCode = spcrateCode.orEmpty(),
    spcrateDescription = spcrateDescription.orEmpty(),
    selfIsuTypeCode = selfIsuTypeCode,
    spcLowDayWage = spcLowDayWage,
    insurDpercent = insurDpercent,
    govermentPercent = govermentPercent,
    treatmentPercap = treatmentPercap,
    payrespitelOne = payrespitelOne,
    payrespitelTwo = payrespitelTwo,
    status = status,
    statusStDate = statusStDate,
)

internal fun SpcPremiumRateEntity.toDomain(): PremiumRateDN = PremiumRateDN(
    govermentPercent = govermentPercent,
    insurDpercent = insurDpercent,
    payrespitelOne = payrespitelOne,
    payrespitelTwo = payrespitelTwo,
    selfIsuTypeCode = selfIsuTypeCode,
    spcLowDayWage = spcLowDayWage,
    spcrateCode = spcrateCode,
    spcrateDescription = spcrateDescription,
    status = status,
    statusStDate = statusStDate,
    treatmentPercap = treatmentPercap,
)
