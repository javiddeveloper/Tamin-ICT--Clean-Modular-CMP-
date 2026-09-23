package com.tamin.taminhamrah.mapper

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListPR

fun InstallmentConstructionListDN.toPR(): InstallmentConstructionListPR {
    return InstallmentConstructionListPR(
        workshopId = workshopId,
        debitNumber = debitNumber,
        debitSubCode = debitSubCode,
        dtnAmount = dtnAmount,
        lastPaymentSheetAmount = lastPaymentSheetAmount,
        dtnExpireDate = dtnExpireDate,
        lastPaymentSheetDescription = lastPaymentSheetDescription,
        paymentDate = paymentDate,
    )
}
