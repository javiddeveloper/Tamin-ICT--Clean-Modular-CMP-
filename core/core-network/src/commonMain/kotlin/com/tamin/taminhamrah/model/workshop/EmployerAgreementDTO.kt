package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row of `workshop-services/employer/get-all-employer-agreement-by-national-id`.
 *
 * A row is an *employer agreement*, not a workshop record: the workshop arrives nested. The
 * envelope carries ~25 more columns (`regno`, `risuid`, `masttyp`, …) that no screen reads;
 * `ignoreUnknownKeys` drops them, so only the fields the UI can show are modelled.
 *
 * The same shape is served by two routes: `employer/get-all-employer-agreement-by-national-id`
 * for the کارگاه‌های کارفرما list, and
 * `get-employer-agreement-by-workshop-id-and-branch-code/{workshopId}/{branchCode}` for one
 * workshop's ردیف پیمان rows. One DTO serves both.
 *
 * The lower-case wire names (`startdate`, `emailaddr`, `mobileno`) are the server's spelling.
 */
@Serializable
data class EmployerAgreementDTO(
    /**
     * ردیف پیمان — the contract row, on the one endpoint that names it `pymseq`.
     *
     * Lower-case and abbreviated on purpose: that is the column name the service sends. The same
     * value is called `contractRow` on the sibling
     * `contract-employer-workshop-info-with-workshop-and-branch-code` response, and neither
     * spelling is a typo to be tidied up — renaming either makes the field stop deserialising.
     */
    @SerialName("pymseq") val contractRow: String? = null,
    @SerialName("startdate") val startDate: String? = null,
    @SerialName("letDate") val commitmentDate: String? = null,
    @SerialName("emailaddr") val email: String? = null,
    @SerialName("mobileno") val mobile: String? = null,
    @SerialName("workshop") val workshop: EmployerWorkshopDTO? = null,
)

@Serializable
data class EmployerWorkshopDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    /** Identity half two, and what every downstream service takes as its branch path segment. */
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("employerName") val employerName: String? = null,
    @SerialName("activityName") val activityName: String? = null,
    @SerialName("lastAddress") val lastAddress: String? = null,
    @SerialName("workshopRegisterDate") val workshopRegisterDate: String? = null,
    @SerialName("workshopApproveDate") val workshopApproveDate: String? = null,
    @SerialName("contractRow") val contractRow: String? = null,
    /**
     * The branch *office*: `branch.code` is what the card shows as کد شعبه, not [branchCode].
     *
     * This endpoint sends `branch` as null and names the same office flat instead, in
     * [branchTitle] and [brhCode] — verified against a live response. Both shapes are read, or the
     * card's شعبه cell comes out empty.
     */
    @SerialName("branch") val branch: WorkshopBranchDTO? = null,
    @SerialName("branchTitle") val branchTitle: String? = null,
    @SerialName("brhCode") val brhCode: String? = null,
    @SerialName("character") val character: WorkshopCharacterDTO? = null,
    /**
     * Present only for a حقوقی workshop (`character.characterCode == "02"`); null otherwise.
     * `pay-normal-debit` sends its `nationalId` alongside the character code.
     */
    @SerialName("legalWorkshop") val legalWorkshop: WorkshopLegalDTO? = null,
    @SerialName("workshopType") val workshopType: WorkshopTypeDTO? = null,
    @SerialName("workshopStatus") val workshopStatus: WorkshopStatusDTO? = null,
)
