package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model

import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDN
import com.tamin.taminhamrah.model.funeralAllowance.FUNERAL_ALLOWANCE_HELP_TYPE
import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDN
import com.tamin.taminhamrah.model.funeralAllowance.RegisteredFuneralRequestDN
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN
import com.tamin.taminhamrah.util.PersianDateFormatter

fun FuneralAllowanceInfoDN.toPR(): FuneralAllowanceInfoPR = FuneralAllowanceInfoPR(
    fullName = fullName,
    firstName = firstName,
    lastName = lastName,
    insuranceNumber = insuranceNumber,
    bankAccount = bankAccount,
    bankName = bankName,
    mobileNumber = mobileNumber,
    branchName = branchName,
    branchCode = branchCode,
    nationalCode = nationalCode,
    deceasedNationalId = deceasedNationalId,
    requestHelpType = requestHelpType,
    hasBankAccountIssue = hasBankAccountIssue,
    registeredRequest = registeredRequest?.toPR(),
)

fun RegisteredFuneralRequestDN.toPR(): RegisteredFuneralRequestPR = RegisteredFuneralRequestPR(
    requestId = requestId,
    deceasedNationalId = deceasedNationalId,
    deathDate = PersianDateFormatter.formatTimestamp(deathTimestamp),
    requestDate = PersianDateFormatter.formatTimestamp(requestTimestamp),
    statusName = statusName,
)

fun DeceasedValidationDN.toPR(): DeceasedValidationPR = DeceasedValidationPR(
    deceasedFullName = deceasedFullName,
    relationship = relationship,
    isEligible = isEligible,
    message = message,
    dependentStatus = dependentStatus,
    deathDate = deathDate,
)

/** Assembles the submit payload from the loaded info plus the deceased id the user entered. */
fun FuneralAllowanceInfoPR.toSubmitParams(deceasedNationalId: String): SubmitFuneralAllowanceParamsDN =
    SubmitFuneralAllowanceParamsDN(
        deceasedNationalId = deceasedNationalId,
        branchCode = branchCode,
        branchName = branchName,
        insuranceFirstName = firstName,
        insuranceLastName = lastName,
        mobileNumber = mobileNumber,
        nationalCode = nationalCode,
        insuranceNumber = insuranceNumber,
        requestHelpType = requestHelpType.ifBlank { FUNERAL_ALLOWANCE_HELP_TYPE },
    )
