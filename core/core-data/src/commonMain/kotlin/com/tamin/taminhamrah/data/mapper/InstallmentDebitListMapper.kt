package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.InstallmentDebitPageEntity
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

// ---- Offline page cache (installment_debit_pages) ----

internal fun InstallmentDebitListDN.toPageEntity(listKey: String, position: Int) = InstallmentDebitPageEntity(
    listKey = listKey,
    position = position,
    workshopId = workshopId,
    debitNumber = debitNumber,
    debitStepDescription = debitStepDescription,
    debitStatusDescription = debitStatusDescription,
    debitStartDate = debitStartDate,
    debitEndDate = debitEndDate,
    remainingAmount = remainingAmount,
)

internal fun InstallmentDebitPageEntity.toDomain() = InstallmentDebitListDN(
    workshopId = workshopId,
    debitNumber = debitNumber,
    debitStepDescription = debitStepDescription,
    debitStatusDescription = debitStatusDescription,
    debitStartDate = debitStartDate,
    debitEndDate = debitEndDate,
    remainingAmount = remainingAmount,
)
