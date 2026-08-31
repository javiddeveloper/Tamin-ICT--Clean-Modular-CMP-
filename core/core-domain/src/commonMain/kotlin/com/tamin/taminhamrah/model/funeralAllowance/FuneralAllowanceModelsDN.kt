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
 * Result of `shortterm/validateFuneral/{nationalCode}`, parsed from the positional string array
 * (native `DeceasedInfoResponse`). [isEligible] mirrors legacy: the raw list must have at least
 * 8 entries and `data[6] == "1"`.
 */
data class DeceasedValidationDN(
    val deceasedFullName: String,
    val relationship: String,
    val isEligible: Boolean,
    /** Backend message, shown when [isEligible] is false. */
    val message: String,
    val dependentStatus: String,
    val deathDate: String,
) {
    companion object {
        fun fromRawList(data: List<String?>): DeceasedValidationDN {
            val eligible = data.size >= 8 && data.getOrNull(6) == "1"
            val rawDeathDate = data.getOrNull(13).orEmpty()
            val formattedDeathDate = if (rawDeathDate.length == 8) {
                "${rawDeathDate.substring(0, 4)}/${rawDeathDate.substring(4, 6)}/${rawDeathDate.substring(6, 8)}"
            } else rawDeathDate

            return DeceasedValidationDN(
                deceasedFullName = data.getOrNull(4).orEmpty(),
                relationship = data.getOrNull(5).orEmpty(),
                isEligible = eligible,
                message = data.getOrNull(7).orEmpty(),
                dependentStatus = data.getOrNull(9).orEmpty(),
                deathDate = formattedDeathDate,
            )
        }
    }
}

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
