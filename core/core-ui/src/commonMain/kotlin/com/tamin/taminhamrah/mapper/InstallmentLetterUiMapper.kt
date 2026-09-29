package com.tamin.taminhamrah.mapper

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterPR

fun InstallmentLetterDN.toPR(): InstallmentLetterPR {
    return InstallmentLetterPR(
        workshopId = workshopId,
        debitNumber = debitNumber,
        debitStepDescription = debitStepDescription,
        debitStatusDescription = debitStatusDescription,
        debitStartDate = debitStartDate,
        debitEndDate = debitEndDate,
        remainingAmount = remainingAmount,
        debitNumberOld = debitNumberOld,
    )
}
