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
    @SerialName("branchCode") val branchCode: String?,
    @SerialName("branchTitle") val branchTitle: String?,
    @SerialName("brhCode") val brhCode: String?,
    @SerialName("employerName") val employerName: String?,
    @SerialName("inclusionDate") val inclusionDate: String?,
    @SerialName("sswn") val sswn: String?,
    @SerialName("userId") val userId: String?,
    @SerialName("workshopApproveDate") val workshopApproveDate: String?,
    @SerialName("workshopId") val workshopId: String?,
    @SerialName("workshopName") val workshopName: String?,
    @SerialName("workshopRegisterDate") val workshopRegisterDate: String?,
    @SerialName("workshopUnemployedStat") val workshopUnemployedStat: String?,
)
