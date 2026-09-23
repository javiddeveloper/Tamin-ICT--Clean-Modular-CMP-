package com.tamin.taminhamrah.mapper

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListPR

fun InstallmentDebitListDN.toPR(): InstallmentDebitListPR {
    return InstallmentDebitListPR(
        workshopId = workshopId,
        debitNumber = debitNumber,
        debitStepDescription = debitStepDescription,
        debitStatusDescription = debitStatusDescription,
        debitStartDate = debitStartDate,
        debitEndDate = debitEndDate,
        remainingAmount = remainingAmount,
    )
}
