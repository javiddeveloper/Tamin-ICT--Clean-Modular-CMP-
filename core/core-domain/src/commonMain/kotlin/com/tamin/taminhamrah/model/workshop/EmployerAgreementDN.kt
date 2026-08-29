package com.tamin.taminhamrah.model.workshop

data class EmployerAgreementDN(
    val pymseq: String? = null,
    val regno: String? = null,
    val firstname: String? = null,
    val emailaddr: String? = null,
    val workshop: EmployerWorkshopDN? = null,
    val nationalno: String? = null,
    val mobileno: String? = null,
    val startdate: String? = null,
    val mastcusttype: String? = null,
    val createdt: String? = null,
    val masttyp: String? = null,
    val logicalDeleted: Boolean? = null,
    val regemailseq: String? = null,
    val lastname: String? = null,
    val special: String? = null,
    val risuid: String? = null,
    val nationalcode: String? = null,
    val enddate: String? = null,
    val letDate: String? = null,
    val regdate: String? = null,
    val roletype: String? = null,
    val dname: String? = null,
    val letNo: String? = null,
    val createuid: String? = null,
)

data class EmployerWorkshopDN(
    val sswn: String? = null,
    val branchTitle: String? = null,
    val branchName: String? = null,
    val lastAddress: String? = null,
    /** `"01"` = حقیقی, `"02"` = حقوقی. Null when the server omitted the block. */
    val characterCode: String? = null,
    val characterDesc: String? = null,
    val workshopApproveDate: String? = null,
    val inclusionDate: String? = null,
    val brhCode: String? = null,
    val activityName: String? = null,
    val workshopRegisterDate: String? = null,
    val branchCode: String? = null,
    val workshopName: String? = null,
    val employerName: String? = null,
    val actitvityCode: String? = null,
    val userId: String? = null,
    val workshopId: String? = null,
    val workshopUnemployedStat: String? = null,
)

data class EmployerAgreementListDN(
    val list: List<EmployerAgreementDN>?,
    val total: Int
)
