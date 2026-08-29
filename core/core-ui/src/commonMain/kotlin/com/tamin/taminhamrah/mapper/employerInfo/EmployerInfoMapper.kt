package com.tamin.taminhamrah.mapper.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoPR
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopPR
import com.tamin.taminhamrah.model.employerInfo.WorkshopItemPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN

fun LegalWorkshopDN.toPresentation(): LegalWorkshopPR =
    LegalWorkshopPR(
        name = name,
        nationalCode = nationalCode,
    )

fun LegalWorkshopCeoDN.toPresentation(): LegalWorkshopCeoPR =
    LegalWorkshopCeoPR(
        firstName = firstName,
        lastName = lastName,
        fullName = fullName,
    )

/** Legal persons carry character code `"02"`; `"01"` is a natural person. */
private const val CHARACTER_CODE_LEGAL = "02"

fun EmployerAgreementDN.toWorkshopItemPR(): WorkshopItemPR {
    val ws = workshop
    // `workshopId` and `branchCode` are the pair the submit call is addressed with, so the row
    // shows exactly what it will send. `sswn` and `brhCode` are different fields on the same
    // object and are not interchangeable with them.
    val workshopId = ws?.workshopId.orEmpty()
    val branchCode = ws?.branchCode.orEmpty()
    val branchName = ws?.branchName ?: ws?.branchTitle.orEmpty()
    return WorkshopItemPR(
        id = "$workshopId-$branchCode",
        name = ws?.workshopName ?: ws?.employerName ?: dname.orEmpty(),
        code = workshopId,
        branch = branchName,
        bcode = branchCode,
        isLegal = ws?.characterCode == CHARACTER_CODE_LEGAL,
        branchLabel = if (branchCode.isBlank()) branchName else "$branchName · $branchCode",
        letDate = letDate ?: ws?.workshopApproveDate.orEmpty(),
        email = emailaddr.orEmpty(),
        mobile = mobileno.orEmpty(),
        address = ws?.lastAddress.orEmpty(),
    )
}
