package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.InstallmentLetterPageEntity
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

// ---- Offline page cache (installment_letter_pages) ----

internal fun InstallmentLetterDN.toPageEntity(listKey: String, position: Int) = InstallmentLetterPageEntity(
    listKey = listKey,
    position = position,
    workshopId = workshopId,
    debitNumber = debitNumber,
    debitStepDescription = debitStepDescription,
    debitStatusDescription = debitStatusDescription,
    debitStartDate = debitStartDate,
    debitEndDate = debitEndDate,
    remainingAmount = remainingAmount,
    debitNumberOld = debitNumberOld,
)

internal fun InstallmentLetterPageEntity.toDomain() = InstallmentLetterDN(
    workshopId = workshopId,
    debitNumber = debitNumber,
    debitStepDescription = debitStepDescription,
    debitStatusDescription = debitStatusDescription,
    debitStartDate = debitStartDate,
    debitEndDate = debitEndDate,
    remainingAmount = remainingAmount,
    debitNumberOld = debitNumberOld,
)
