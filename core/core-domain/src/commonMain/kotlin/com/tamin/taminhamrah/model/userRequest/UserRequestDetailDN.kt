package com.tamin.taminhamrah.model.userRequest

import com.tamin.taminhamrah.model.request.ListDataResponse

data class UserRequestDetailDN(
    val statusModel: StatusDN?,
    val infoModel: InfoDN?,
    val pregnancyStatusList: List<PregnancyStatusDN>,
    val pregnancyTypeList: List<PregnancyTypeDN>,
    val article16Detail: Article16DetailDN?,
    val deferredInstallmentDetail: DeferredInstallmentDetailDN?,
    val followUpObjectionHistory: List<ResultFollowUpObjectionDN>
) {
    data class StatusDN(
        val dateAcc: Long?,
        val processResult: String?,
        val rejectReason: String?
    )

    data class InfoDN(
        val mobile: String?,
        val risuFullName: String?,
        val risuid: String?,
        val orthoticsList: List<OrthoticsDN>,
        val consequentialList: List<ConsequentialDN>,
        val illnessList: List<IllnessDN>,
        val pregnancyList: List<PregnancyDN>
    )

    data class OrthoticsDN(
        val bankAccount: String?,
        val bankName: String?,
        val branchName: String?,
        val resultMessage: String?,
        val fileList: List<RequestFileDN>,
        val useTaj: Long?,
        val bimSDate: Long?,
        val bimEdate: Long?,
        val bimDrname: String?,
        val bimDrid: String?
    )

    data class ConsequentialDN(
        val risuId: String?,
        val bletEndDate: String?,
        val birthDate: String?,
        val relationship: String?,
        val risuLName: String?,
        val risuFname: String?,
        val cityName: String?,
        val risuIdNo: String?
    )

    data class IllnessDN(
        val bankAccount: String?,
        val bankName: String?,
        val branchName: String?,
        val resultMessage: String?,
        val fileList: List<RequestFileDN>,
        val bimSDate: Long?,
        val bimEdate: Long?,
        val bimDrname: String?,
        val bimDrid: String?
    )

    data class PregnancyDN(
        val barDrid: String?,
        val barDd: String?,
        val barDemDat: Long?,
        val drName: String?,
        val startDate: Long?,
        val endDate: Long?,
        val barType: String?,
        val barChild: String?,
        val orthoticsRequest: OrthoticsDN
    )

    data class RequestFileDN(
        val documentFile: String?,
        val documentType: String?
    )

    data class PregnancyStatusDN(
        val code: String?,
        val name: String?
    )

    data class PregnancyTypeDN(
        val code: String?,
        val name: String?
    )

    data class Article16DetailDN(
        val defectDesc: String?,
        val objectionPhotoList: List<ObjectionPhotoDN>
    )

    data class ObjectionPhotoDN(
        val guid: String?,
        val seqNo: Int?,
        val type: String?
    )

    data class DeferredInstallmentDetailDN(
        val firstName: String?,
        val lastName: String?,
        val nationalId: String?,
        val birthDate: Long?,
        val pensionerNationalId: String?,
        val userFirstName: String?,
        val userLastName: String?,
        val pensionerId: String?,
        val bankName: String?,
        val bankBranch: String?,
        val installmentAmount: Long?,
        val installmentCount: Int?,
        val loanAmount: Long?,
        val guaranteeAmount: Long?
    )

    data class ResultFollowUpObjectionDN(
        val requestNumber: String?,
        val requestType: String?,
        val branchId: String?,
        val requestDesc: String?,
        val statusDesc: String?,
        val answerTypeDesc: String?,
        val resultDesc: String?,
        val userDesc: String?,
        val requestDate: String?,
        val answerDate: String?,
        val branchName: String?
    )
}