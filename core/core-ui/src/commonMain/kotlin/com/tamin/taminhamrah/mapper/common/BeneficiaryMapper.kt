package com.tamin.taminhamrah.mapper.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.BeneficiaryPR

fun BeneficiaryDN.toPresentation(): BeneficiaryPR {
    return BeneficiaryPR(
        bankCode = bankCode,
        bankName = bankName
    )
}

fun List<BeneficiaryDN>.toPresentation(): List<BeneficiaryPR> {
    return map { it.toPresentation() }
}
