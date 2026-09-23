package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDTO

fun InstallmentLetterDTO.toDomain(): InstallmentLetterDN {
    return InstallmentLetterDN(
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
