package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListDTO

fun InstallmentConstructionListDTO.toDomain(): InstallmentConstructionListDN {
    return InstallmentConstructionListDN(
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
