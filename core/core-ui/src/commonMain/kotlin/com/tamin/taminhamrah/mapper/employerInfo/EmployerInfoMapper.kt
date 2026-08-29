package com.tamin.taminhamrah.mapper.employerInfo

import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopCeoPR
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopDN
import com.tamin.taminhamrah.model.employerInfo.LegalWorkshopPR
import com.tamin.taminhamrah.model.employerInfo.WorkshopItemPR
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.util.toFormattedDate

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

/**
 * Maps the whole list, because a row's identity is only decidable here.
 *
 * The service returns one entry per *agreement*, not per workshop, so the same workshop and branch
 * can legitimately appear several times over. `pymseq` is the agreement's own sequence and is what
 * makes a row unique; when it is missing the position is the last resort, since keying duplicates
 * on workshop and branch crashed the list and made one card's expander open all of its twins.
 */
fun List<EmployerAgreementDN>.toWorkshopItemPRs(): List<WorkshopItemPR> =
    mapIndexed { index, agreement -> agreement.toWorkshopItemPR(fallbackId = index.toString()) }

fun EmployerAgreementDN.toWorkshopItemPR(fallbackId: String = ""): WorkshopItemPR {
    val ws = workshop
    // `workshopId` and `branchCode` are the pair the submit call is addressed with, so the row
    // shows exactly what it will send. `sswn` and `brhCode` are different fields on the same
    // object and are not interchangeable with them.
    val workshopId = ws?.workshopId.orEmpty()
    val branchCode = ws?.branchCode.orEmpty()
    val branchName = ws?.branchTitle ?: ws?.branchName.orEmpty()
    return WorkshopItemPR(
        id = pymseq ?: "$workshopId-$branchCode-$fallbackId",
        name = ws?.workshopName ?: ws?.employerName ?: dname.orEmpty(),
        code = workshopId,
        branch = branchName,
        bcode = branchCode,
        isLegal = ws?.characterCode == CHARACTER_CODE_LEGAL,
        branchLabel = if (branchCode.isBlank()) branchName else "$branchName – $branchCode",
        // The service sends `yyyyMMdd` with no separators; the old app's list row separates it
        // the same way before showing it.
        letDate = (letDate ?: ws?.workshopApproveDate).orEmpty().toFormattedDate(),
        email = emailaddr.orEmpty(),
        mobile = mobileno.orEmpty(),
        address = ws?.lastAddress.orEmpty(),
    )
}
