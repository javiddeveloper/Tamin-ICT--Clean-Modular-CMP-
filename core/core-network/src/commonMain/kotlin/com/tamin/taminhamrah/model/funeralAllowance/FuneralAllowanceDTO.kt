package com.tamin.taminhamrah.model.funeralAllowance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `data` of `GET funeral-no-presence/getFuneralNoPresenceLoadData`.
 * Insured + last-branch info plus, when [flag] is true, a previously registered request that the
 * branch could not confirm because of a bank-account problem.
 *
 * Legacy names kept verbatim from the native app's `FuneralAllowanceModel`:
 * - [partnerNationalId] is the deceased's national id (the field was copy-pasted from the
 *   marriage-allowance model, hence "partner").
 * - [weddingTimestamp] is the date of death for the same reason.
 */
@Serializable
data class FuneralAllowanceInfoDTO(
    @SerialName("bankAccount") val bankAccount: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("branchWorkshop") val branchWorkshop: List<BranchWorkshopDTO>? = null,
    @SerialName("flag") val flag: Boolean = false,
    @SerialName("genderCode") val genderCode: String? = null,
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = null,
    @SerialName("insuranceLastName") val insuranceLastName: String? = null,
    @SerialName("mobilNumber") val mobileNumber: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("partnerNationalId") val partnerNationalId: String? = null,
    @SerialName("request") val request: RequestFuneralDTO? = null,
    @SerialName("requestHelpType") val requestHelpType: String? = null,
    @SerialName("requestHelpTypeDesc") val requestHelpTypeDesc: String? = null,
    @SerialName("resultMessage") val resultMessage: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("serviceDate") val serviceDate: Long? = null,
    @SerialName("weddingTimestamp") val deathTimestamp: Long? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
)

@Serializable
data class RequestFuneralDTO(
    @SerialName("brchCode") val brchCode: String? = null,
    @SerialName("editDate") val editDate: Long? = null,
    @SerialName("editUser") val editUser: String? = null,
    @SerialName("id") val id: Long? = null,
    @SerialName("refrenceCode") val referenceCode: String? = null,
    @SerialName("requestDate") val requestDate: Long? = null,
    @SerialName("requestType") val requestType: RequestTypeDTO? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusId") val statusId: String? = null,
    @SerialName("statusName") val statusName: String? = null,
    @SerialName("systemType") val systemType: String? = null,
    @SerialName("userId") val userId: String? = null,
)

@Serializable
data class RequestTypeDTO(
    @SerialName("form") val form: String? = null,
    @SerialName("requestTypeCode") val requestTypeCode: String? = null,
    @SerialName("requestTypeDesc") val requestTypeDesc: String? = null,
    @SerialName("systemId") val systemId: String? = null,
)

@Serializable
data class BranchWorkshopDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
)

/**
 * Body of `POST funeral-no-presence/saveShorttremFuneral`.
 * Mirrors the native `FuneralAllowanceRequest`: [deadNationalId] is serialized as `deadnationalId`
 * (backend spelling), and [shorttermRequest] carries the insured/branch payload.
 */
@Serializable
data class FuneralAllowanceRequestDTO(
    @SerialName("deadnationalId") val deadNationalId: String = "",
    @SerialName("shorttermRequest") val shorttermRequest: FuneralShorttermRequestDTO? = null,
)

@Serializable
data class FuneralShorttermRequestDTO(
    @SerialName("branchCode") val branchCode: String? = "",
    @SerialName("branchName") val branchName: String? = "",
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = "",
    @SerialName("insuranceLastName") val insuranceLastName: String? = "",
    @SerialName("mobilNumber") val mobileNumber: String? = "",
    @SerialName("nationalCode") val nationalCode: String? = "",
    // The backend expects an empty object here (native app sent `object Request`).
    @SerialName("request") val request: Map<String, String> = emptyMap(),
    @SerialName("requestFileList") val requestFileList: List<String> = emptyList(),
    @SerialName("requestHelpType") val requestHelpType: String? = "",
    @SerialName("risuid") val risuid: String? = "",
)

/**
 * `data` of `GET shortterm/validateFuneral/{nationalCode}`.
 *
 * The backend returns a **bare positional string array** (legacy `DeceasedInfoResponse`), not a
 * keyed object. [fromPositional] is the single place in the codebase that knows the slot map —
 * nothing above the remote data source ever sees the raw list or an index.
 *
 * Slot map (legacy, `data[i]`) — matches `DeceasedInfoResponse`/`getDeceasedInfo()` in
 * `old_android`/`D:\my-tamin` exactly; legacy never reads past `[7]`, so no other slot is decoded
 * here either:
 * - `[4]` deceased full name
 * - `[5]` relationship to the insured
 * - `[6]` eligibility flag (`"1"` = eligible)
 * - `[7]` message, shown when not eligible
 *
 * Null-safety: a null list, a list shorter than the highest slot, and `null` entries all collapse
 * to `""` (via [List.getOrNull] + [orEmpty]). [isEligible] additionally requires at least
 * [MIN_ELIGIBILITY_ENTRIES] entries, mirroring the legacy `data.size >= 8 && data[6] == "1"` rule,
 * so a truncated/garbled response reads as "not eligible" exactly like the native app.
 */
data class DeceasedValidationDTO(
    val fullName: String = "",
    val relationship: String = "",
    val isEligible: Boolean = false,
    val message: String = "",
) {
    companion object {
        /** Legacy gate: fewer entries than this and the response can't be trusted as "eligible". */
        const val MIN_ELIGIBILITY_ENTRIES = 8
        private const val ELIGIBLE_FLAG = "1"

        private const val SLOT_FULL_NAME = 4
        private const val SLOT_RELATIONSHIP = 5
        private const val SLOT_ELIGIBLE_FLAG = 6
        private const val SLOT_MESSAGE = 7

        fun fromPositional(raw: List<String?>?): DeceasedValidationDTO {
            val slots = raw.orEmpty()
            fun slot(index: Int): String = slots.getOrNull(index).orEmpty()
            return DeceasedValidationDTO(
                fullName = slot(SLOT_FULL_NAME),
                relationship = slot(SLOT_RELATIONSHIP),
                isEligible = slots.size >= MIN_ELIGIBILITY_ENTRIES &&
                    slot(SLOT_ELIGIBLE_FLAG) == ELIGIBLE_FLAG,
                message = slot(SLOT_MESSAGE),
            )
        }
    }
}
