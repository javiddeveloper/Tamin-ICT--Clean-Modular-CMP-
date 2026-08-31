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
        .distinctBy { item ->
            WorkshopItemContent(
                name = item.name,
                code = item.code,
                branch = item.branch,
                bcode = item.bcode,
                isLegal = item.isLegal,
                branchLabel = item.branchLabel,
                letDate = item.letDate,
                email = item.email,
                mobile = item.mobile,
                address = item.address,
            )
        }

private data class WorkshopItemContent(
    val name: String,
    val code: String,
    val branch: String,
    val bcode: String,
    val isLegal: Boolean,
    val branchLabel: String,
    val letDate: String,
    val email: String,
    val mobile: String,
    val address: String,
)

fun EmployerAgreementDN.toWorkshopItemPR(fallbackId: String = ""): WorkshopItemPR {
    // Two different branch codes: `branchCode` is identity half two, the path segment every
    // downstream call takes, and is what the request must carry. `branchOfficeCode` is the office
    // number the card labels کد شعبه. They are not interchangeable.
    val requestBranchCode = workshop.branchCode
    val officeName = workshop.branchOfficeName
    val officeCode = workshop.branchOfficeCode
    return WorkshopItemPR(
        // The service returns one row per agreement, so one workshop can arrive several times;
        // the position is what separates the twins.
        id = "${workshop.workshopId}-$requestBranchCode-$fallbackId",
        name = workshop.name.ifBlank { workshop.employerName },
        code = workshop.workshopId,
        branch = officeName,
        bcode = requestBranchCode,
        isLegal = workshop.characterCode == CHARACTER_CODE_LEGAL,
        branchLabel = if (officeCode.isBlank()) officeName else "$officeName – $officeCode",
        // The service sends `yyyyMMdd` with no separators; the old app's list row separates it
        // the same way before showing it.
        letDate = commitmentDate.ifBlank { workshop.approveDate }.toFormattedDate(),
        email = email,
        mobile = mobile,
        address = workshop.address,
    )
}

