package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListDTO

fun InstallmentDebitListDTO.toDomain(): InstallmentDebitListDN {
    return InstallmentDebitListDN(
        workshopId = workshopId,
        debitNumber = debitNumber,
        debitStepDescription = debitStepDescription,
        debitStatusDescription = debitStatusDescription,
        debitStartDate = debitStartDate,
        debitEndDate = debitEndDate,
        remainingAmount = remainingAmount,
    )
}
