package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class EmployerAgreementPR(
    val pymseq: String?,
    val regno: String?,
    val firstname: String?,
    val emailaddr: String?,
    val workshop: EmployerWorkshopPR?,
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


@Immutable
@Serializable
data class EmployerWorkshopPR(
    val sswn: String? = null,
    val branchTitle: String? = null,
    val branchName: String? = null,
    val lastAddress: String? = null,
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

@Immutable
@Serializable
data class EmployerAgreementListPR(
    val list: List<EmployerAgreementPR>?,
    val total: Int
)
