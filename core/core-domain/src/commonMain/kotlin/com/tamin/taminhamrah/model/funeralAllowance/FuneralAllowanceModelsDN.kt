package com.tamin.taminhamrah.model.funeralAllowance

/** Default help-type code the native app sends for a funeral-allowance request ("07"). */
const val FUNERAL_ALLOWANCE_HELP_TYPE = "07"

/**
 * Insured + last-branch info for the funeral-allowance flow.
 * When [hasBankAccountIssue] is true the screen shows [registeredRequest] and the
 * "I fixed my bank account" action instead of the deceased-eligibility inquiry.
 */
data class FuneralAllowanceInfoDN(
    val firstName: String,
    val lastName: String,
    val insuranceNumber: String,        // risuid
    val bankAccount: String,
    val bankName: String,
    val mobileNumber: String,
    val branchName: String,
    val branchCode: String,
    val nationalCode: String,
    val deceasedNationalId: String,     // partnerNationalId from the backend
    val requestHelpType: String,
    val hasBankAccountIssue: Boolean,   // backend `flag`
    val registeredRequest: RegisteredFuneralRequestDN?,
) {
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

/** A previously registered request the branch could not confirm because of a bank-account problem. */
data class RegisteredFuneralRequestDN(
    val requestId: Long,
    val deceasedNationalId: String,
    val deathTimestamp: Long?,
    val requestTimestamp: Long?,
    val statusName: String,
)

/**
 * Result of `shortterm/validateFuneral/{nationalCode}`. The backend's raw positional string array
 * is decoded into named fields by `DeceasedValidationDTO.fromPositional` in core-network; this is
 * the mapped domain view. [isEligible] mirrors legacy (the raw list must carry at least 8 entries
 * and `data[6] == "1"`); [deathDate] is already formatted `yyyy/MM/dd` by the DTO→domain mapper.
 */
data class DeceasedValidationDN(
    val deceasedFullName: String,
    val relationship: String,
    val isEligible: Boolean,
    /** Backend message, shown when [isEligible] is false. */
    val message: String,
    val dependentStatus: String,
    val deathDate: String,
)

/** Everything needed to POST `funeral-no-presence/saveShorttremFuneral`. */
data class SubmitFuneralAllowanceParamsDN(
    val deceasedNationalId: String,
    val branchCode: String,
    val branchName: String,
    val insuranceFirstName: String,
    val insuranceLastName: String,
    val mobileNumber: String,
    val nationalCode: String,
    val insuranceNumber: String,   // risuid
    val requestHelpType: String = FUNERAL_ALLOWANCE_HELP_TYPE,
)

/** Builds the submit params from the loaded info plus the deceased's national id the user entered. */
fun FuneralAllowanceInfoDN.toSubmitParams(deceasedNationalId: String): SubmitFuneralAllowanceParamsDN =
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
