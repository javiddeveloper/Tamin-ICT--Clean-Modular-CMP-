package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployerAgreementDTO(
    @SerialName("pymseq") val pymseq: String? = null,
    @SerialName("regno") val regno: String? = null,
    @SerialName("firstname") val firstname: String? = null,
    @SerialName("emailaddr") val emailaddr: String? = null,
    @SerialName("workshop") val workshop: EmployerWorkshopDTO? = null,
    @SerialName("nationalno") val nationalno: String? = null,
    @SerialName("mobileno") val mobileno: String? = null,
    @SerialName("startdate") val startdate: String? = null,
    @SerialName("mastcusttype") val mastcusttype: String? = null,
    @SerialName("createdt") val createdt: String? = null,
    @SerialName("masttyp") val masttyp: String? = null,
    @SerialName("logicalDeleted") val logicalDeleted: Boolean? = null,
    @SerialName("regemailseq") val regemailseq: String? = null,
    @SerialName("lastname") val lastname: String? = null,
    @SerialName("special") val special: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("nationalcode") val nationalcode: String? = null,
    @SerialName("enddate") val enddate: String? = null,
    @SerialName("letDate") val letDate: String? = null,
    @SerialName("regdate") val regdate: String? = null,
    @SerialName("roletype") val roletype: String? = null,
    @SerialName("dname") val dname: String? = null,
    @SerialName("letNo") val letNo: String? = null,
    @SerialName("createuid") val createuid: String? = null,
)

@Serializable
data class EmployerWorkshopDTO(
    @SerialName("sswn") val sswn: String? = null,
    @SerialName("branchTitle") val branchTitle: String? = null,
    @SerialName("workshopApproveDate") val workshopApproveDate: String? = null,
    @SerialName("inclusionDate") val inclusionDate: String? = null,
    @SerialName("brhCode") val brhCode: String? = null,
    @SerialName("activityName") val activityName: String? = null,
    @SerialName("workshopRegisterDate") val workshopRegisterDate: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("employerName") val employerName: String? = null,
    @SerialName("actitvityCode") val actitvityCode: String? = null,
    @SerialName("userId") val userId: String? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("workshopUnemployedStat") val workshopUnemployedStat: String? = null,
)
