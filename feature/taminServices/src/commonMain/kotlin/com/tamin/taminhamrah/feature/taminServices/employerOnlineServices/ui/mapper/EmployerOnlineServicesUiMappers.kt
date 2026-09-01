package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper

import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementRowPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.IdentityCardPR
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.util.toJalaliDateLabel
import com.tamin.taminhamrah.util.toPersianDigits

/**
 * Domain → presentation for the Employer Online Services landing screen.
 *
 * The one place this screen's data is formatted: Jalali dates get their separators, numbers become
 * Persian digits, and anything the service left out becomes the design's dash. Doing it here — not
 * in a composable — is what keeps a card free of per-recomposition string work and lets a preview
 * feed the same shapes the screen builds at runtime.
 */

fun UserProfileDN.toIdentityCardPR(): IdentityCardPR = IdentityCardPR(
    fullName = listOfNotNull(firstName?.takeIf { it.isNotBlank() }, lastName?.takeIf { it.isNotBlank() })
        .joinToString(separator = " ")
        .orDash(),
    nationalCode = nationalCode.orDashDigits(),
)

fun EmployerAgreementDN.toRowPR(): EmployerAgreementRowPR = with(workshop) {
    EmployerAgreementRowPR(
        workshopId = workshopId,
        branchCode = branchCode,
        hasIdentity = hasIdentity,
        isActive = WorkshopActivityStatus.fromCode(statusCode) == WorkshopActivityStatus.ACTIVE,
        statusLabel = statusDescription.orEmpty(),
        workshopName = name.orDash(),
        branchLabel = branchOfficeLabel(),
        workshopCodeLabel = workshopId.orDashDigits(),
        commitmentDate = this@toRowPR.commitmentDate.orDashDate(),
        startDate = this@toRowPR.startDate.orDashDate(),
        address = address.orDash(),
        mobile = this@toRowPR.mobile.orDashDigits(),
        email = this@toRowPR.email.orDash(),
    )
}

/** «شعبهٔ ۲ مشهد · ۱۲۰۲» — name and code together, or whichever half is present, or a dash. */
private fun com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN.branchOfficeLabel(): String {
    val name = branchOfficeName.takeIf { it.isNotBlank() }
    val code = branchOfficeCode.takeIf { it.isNotBlank() }?.toPersianDigits()
    return listOfNotNull(name, code).joinToString(separator = " · ").orDash()
}

/** Digits the user reads are Persian; a value the service omitted is the design's dash. */
private fun String?.orDashDigits(): String = this?.takeIf { it.isNotBlank() }?.toPersianDigits().orDash()

/** Compact Jalali (`14030519`) renders as `۱۴۰۳/۰۵/۱۹`; an absent date is a dash. */
private fun String?.orDashDate(): String = this?.takeIf { it.isNotBlank() }?.toJalaliDateLabel().orDash()
