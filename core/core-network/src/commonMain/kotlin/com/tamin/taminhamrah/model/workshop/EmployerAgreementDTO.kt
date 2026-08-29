package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployerAgreementDTO(
    @SerialName("createdt") val createdt: String?,
    @SerialName("createuid") val createuid: String?,
    @SerialName("dname") val dname: String?,
    @SerialName("emailaddr") val emailaddr: String?,
    @SerialName("enddate") val enddate: String?,
    @SerialName("firstname") val firstname: String?,
    @SerialName("lastname") val lastname: String?,
    @SerialName("letDate") val letDate: String?,
    @SerialName("letNo") val letNo: String?,
    @SerialName("logicalDeleted") val logicalDeleted: Boolean?,
    @SerialName("mastcusttype") val mastcusttype: String?,
    @SerialName("masttyp") val masttyp: String?,
    @SerialName("mobileno") val mobileno: String?,
    @SerialName("nationalcode") val nationalcode: String?,
    @SerialName("nationalno") val nationalno: String?,
    @SerialName("pymseq") val pymseq: String?,
    @SerialName("regdate") val regdate: String?,
    @SerialName("regemailseq") val regemailseq: String?,
    @SerialName("regno") val regno: String?,
    @SerialName("risuid") val risuid: String?,
    @SerialName("roletype") val roletype: String?,
    @SerialName("special") val special: String?,
    @SerialName("startdate") val startdate: String?,
    @SerialName("workshop") val workshop: EmployerWorkshopDTO?,
)

@Serializable
data class EmployerWorkshopDTO(
    @SerialName("actitvityCode") val actitvityCode: String?,
    @SerialName("activityName") val activityName: String?,
    @SerialName("branch") val branch: EmployerWorkshopBranchDTO? = null,
    @SerialName("branchCode") val branchCode: String?,
    @SerialName("branchTitle") val branchTitle: String?,
    @SerialName("brhCode") val brhCode: String?,
    @SerialName("character") val character: EmployerWorkshopCharacterDTO? = null,
    @SerialName("employerName") val employerName: String?,
    @SerialName("inclusionDate") val inclusionDate: String?,
    @SerialName("lastAddress") val lastAddress: String? = null,
    @SerialName("sswn") val sswn: String?,
    @SerialName("userId") val userId: String?,
    @SerialName("workshopApproveDate") val workshopApproveDate: String?,
    @SerialName("workshopId") val workshopId: String?,
    @SerialName("workshopName") val workshopName: String?,
    @SerialName("workshopRegisterDate") val workshopRegisterDate: String?,
    @SerialName("workshopUnemployedStat") val workshopUnemployedStat: String?,
)

/** The branch handling the workshop. `organizationName` is the name shown beside its code. */
@Serializable
data class EmployerWorkshopBranchDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("organizationName") val organizationName: String? = null,
)

/**
 * Whether the workshop is a natural or a legal person.
 *
 * `characterCode` is `"01"` for حقیقی and `"02"` for حقوقی — only a حقوقی workshop can have its
 * identity details completed, so this code decides whether the list row offers the form at all.
 */
@Serializable
data class EmployerWorkshopCharacterDTO(
    @SerialName("characterCode") val characterCode: String? = null,
    @SerialName("characterDesc") val characterDesc: String? = null,
)
