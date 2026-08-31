package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row of `workshop-services/employer/get-all-employer-agreement-by-national-id`.
 *
 * A row is an *employer agreement*, not a workshop record: the workshop arrives nested. The
 * envelope carries ~25 more columns (`pymseq`, `regno`, `risuid`, `masttyp`, …) that no screen
 * reads; `ignoreUnknownKeys` drops them, so only the fields the UI can show are modelled.
 *
 * The lower-case wire names (`startdate`, `emailaddr`, `mobileno`) are the server's spelling.
 */
@Serializable
data class EmployerAgreementDTO(
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
    /** The branch *office*: `branch.code` is what the card shows as کد شعبه, not [branchCode]. */
    @SerialName("branch") val branch: WorkshopBranchDTO? = null,
    @SerialName("character") val character: WorkshopCharacterDTO? = null,
    @SerialName("workshopType") val workshopType: WorkshopTypeDTO? = null,
    @SerialName("workshopStatus") val workshopStatus: WorkshopStatusDTO? = null,
)
