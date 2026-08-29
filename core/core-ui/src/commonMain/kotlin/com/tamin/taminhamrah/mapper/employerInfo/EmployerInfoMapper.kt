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

fun EmployerAgreementDN.toWorkshopItemPR(): WorkshopItemPR {
    val ws = workshop
    val isLegal = ws?.characterCode == "02"
    val branchDisplay = ws?.branchName ?: ws?.branchTitle ?: ""
    val branchCode = ws?.brhCode ?: ws?.branchCode ?: ""
    return WorkshopItemPR(
        id = ws?.sswn ?: ws?.workshopId ?: "",
        name = ws?.workshopName ?: ws?.employerName ?: dname ?: "",
        code = ws?.sswn ?: ws?.workshopId ?: "",
        branch = branchDisplay,
        bcode = branchCode,
        isLegal = isLegal,
        characterDesc = ws?.characterDesc ?: if (isLegal) "شخصیت حقوقی" else "شخصیت حقیقی",
        letDate = letDate ?: ws?.workshopApproveDate ?: "",
        email = emailaddr ?: "",
        mobile = mobileno ?: "",
        address = ws?.lastAddress ?: "",
    )
}
