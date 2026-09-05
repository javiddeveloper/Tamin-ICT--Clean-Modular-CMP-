package com.tamin.taminhamrah.model.treatment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row of `shortterm-request/commission-confrimation`.
 *
 * **Every `@SerialName` here is the literal wire name and none of them may be "corrected".** They
 * are abbreviations, not words: `ddSar` is the outpatient day count, `ddBas` the inpatient one,
 * `risuid` the insurance number, `reqHelptype` the support type. The previous revision named them
 * after the *Kotlin properties* of the old app's model instead of its `@SerializedName` values, so
 * not one field matched the payload and the screen rendered an empty list.
 *
 * The day counts are strings on the wire in the old app's declaration; they stay strings here
 * rather than becoming `Int`, so a value the service pads or blanks does not fail the whole row.
 */
@Serializable
data class MedicalConfirmationDTO(
    @SerialName("reqHelptype") val supportType: String? = null,
    @SerialName("centerName") val treatmentCenter: String? = null,
    // confirmGet is the medical authority's verdict; confirmOk is the branch's own status. The
    // two are separate fields and the wire names give no hint which is which -- do not swap them.
    @SerialName("confirmGet") val statusDesc: String? = null,
    @SerialName("confirmOk") val branchStatus: String? = null,
    @SerialName("risuid") val insuranceNumber: String? = null,
    @SerialName("nationalId") val nationalCode: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("fromDate") val outpatientRestStartDate: String? = null,
    @SerialName("toDate") val outpatientRestEndDate: String? = null,
    @SerialName("ddSar") val numberOfOutpatientDays: String? = null,
    @SerialName("sDate") val inpatientRestStartDate: String? = null,
    @SerialName("eDate") val inpatientRestEndDate: String? = null,
    @SerialName("ddBas") val numberOfInpatientDays: String? = null,
    @SerialName("comment") val description: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    // The unapproved period arrives as two dates, not one phrase; the card joins them for display.
    @SerialName("fromDateNotConfirm") val unapprovedFromDate: String? = null,
    @SerialName("toDateNotConfirm") val unapprovedToDate: String? = null,
    /**
     * Not present in any payload the old app reads -- it has no per-row identifier at all (its
     * `DiffUtil` falls back to `risuid`). Kept nullable because the certificate download and the
     * send-to-inbox action both need one; both stay hidden while it is absent rather than posting
     * a placeholder id. See the KDoc on `TreatmentApiService.getMedicalConfirmationPdf`.
     */
    @SerialName("repId") val repId: String? = null,
)
