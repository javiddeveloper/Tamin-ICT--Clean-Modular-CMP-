package com.tamin.taminhamrah.model.workshop

data class EmployerAgreementDN(
    val pymseq: String?,
    val regno: String?,
    val firstname: String?,
    val emailaddr: String?,
    val workshop: EmployerWorkshopDN?,
    val nationalno: String?,
    val mobileno: String?,
    val startdate: String?,
    val mastcusttype: String?,
    val createdt: String?,
    val masttyp: String?,
    val logicalDeleted: Boolean?,
    val regemailseq: String?,
    val lastname: String?,
    val special: String?,
    val risuid: String?,
    val nationalcode: String?,
    val enddate: String?,
    val letDate: String?,
    val regdate: String?,
    val roletype: String?,
    val dname: String?,
    val letNo: String?,
    val createuid: String?,
)

data class EmployerWorkshopDN(
    val sswn: String?,
    val branchTitle: String?,
    val workshopApproveDate: String?,
    val inclusionDate: String?,
    val brhCode: String?,
    val activityName: String?,
    val workshopRegisterDate: String?,
    val branchCode: String?,
    val workshopName: String?,
    val employerName: String?,
    val actitvityCode: String?,
    val userId: String?,
    val workshopId: String?,
    val workshopUnemployedStat: String?,
)

data class EmployerAgreementListDN(
    val list: List<EmployerAgreementDN>?,
    val total: Int
)
