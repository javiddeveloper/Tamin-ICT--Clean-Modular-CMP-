package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.common.BeneficiaryDTO
import com.tamin.taminhamrah.model.common.BeneficiaryDN

fun BeneficiaryDTO.toDomain(): BeneficiaryDN {
    return BeneficiaryDN(
        bankCode = bankCode,
        bankName = bankName
    )
}
