package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.InstallmentConstructionPageEntity
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

// ---- Offline page cache (installment_construction_pages) ----

internal fun InstallmentConstructionListDN.toPageEntity(listKey: String, position: Int) = InstallmentConstructionPageEntity(
    listKey = listKey,
    position = position,
    workshopId = workshopId,
    debitNumber = debitNumber,
    debitSubCode = debitSubCode,
    dtnAmount = dtnAmount,
    lastPaymentSheetAmount = lastPaymentSheetAmount,
    dtnExpireDate = dtnExpireDate,
    lastPaymentSheetDescription = lastPaymentSheetDescription,
    paymentDate = paymentDate,
)

internal fun InstallmentConstructionPageEntity.toDomain() = InstallmentConstructionListDN(
    workshopId = workshopId,
    debitNumber = debitNumber,
    debitSubCode = debitSubCode,
    dtnAmount = dtnAmount,
    lastPaymentSheetAmount = lastPaymentSheetAmount,
    dtnExpireDate = dtnExpireDate,
    lastPaymentSheetDescription = lastPaymentSheetDescription,
    paymentDate = paymentDate,
)
