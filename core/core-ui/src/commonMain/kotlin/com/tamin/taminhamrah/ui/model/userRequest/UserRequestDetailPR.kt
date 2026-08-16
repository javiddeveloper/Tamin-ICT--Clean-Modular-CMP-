package com.tamin.taminhamrah.ui.model.userRequest

data class UserRequestDetailPR(
    val isLoading: Boolean = false,
    val error: String? = null,
    val statusDetailStatus: StatusDetailPR? = null,
    val graphicDetailInfo: InfoDetailPR? = null,
    val pregnancyList: List<PregnancyDetailPR>,
    val article16Detail: Article16DetailPR?,
    val deferredInstallmentDetail: DeferredInstallmentPR?,
    val followUpObjectionList: List<ResultFollowUpObjectionPR>,
    val documentsList: List<DocumentPR>
)

data class StatusDetailPR(
    val dateAcc: String?,
    val processResult: String?,
    val rejectReason: String?
)

data class InfoDetailPR(
    val mobile: String?,
    val risuFullName: String?,
    val risuid: String?,
    val orthoticsList: List<OrthoticsPR>,
    val consequentialList: List<ConsequentialPR>,
    val illnessList: List<IllnessPR>,
    val pregnancyList: List<PregnancyPR>
)

data class OrthoticsPR(
    val bankAccount: String?,
    val bankName: String?,
    val branchName: String?,
    val resultMessage: String?,
    val fileList: List<RequestFilePR>,
    val useTaj: String?,
    val bimSDate: String?,
    val bimEdate: String?,
    val bimDrname: String?,
    val bimDrid: String?
)

data class ConsequentialPR(
    val risuId: String?,
    val bletEndDate: String?,
    val birthDate: String?,
    val relationship: String?,
    val risuLName: String?,
    val risuFname: String?,
    val cityName: String?,
    val risuIdNo: String?
)

data class IllnessPR(
    val bankAccount: String?,
    val bankName: String?,
    val branchName: String?,
    val resultMessage: String?,
    val fileList: List<RequestFilePR>,
    val bimSDate: String?,
    val bimEdate: String?,
    val bimDrname: String?,
    val bimDrid: String?
)

data class PregnancyPR(
    val barDrid: String?,
    val barDd: String?,
    val barDemDat: String?,
    val drName: String?,
    val startDate: String?,
    val endDate: String?,
    val barType: String?,
    val barChild: String?,
    val orthoticsRequest: OrthoticsPR
)

data class RequestFilePR(
    val documentFile: String?,
    val documentType: String?
)

data class DocumentPR(
    val fileName: String?,
    val fileContent: String?,
    val uri: String?,
    val fileType: String?
)

data class Article16DetailPR(
    val defectDesc: String?,
    val objectionPhotoList: List<ObjectionPhotoPR>
)

data class ObjectionPhotoPR(
    val guid: String?,
    val seqNo: Int?,
    val type: String?,
    val documentTitle: String?
)

data class DeferredInstallmentPR(
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val birthDate: String?,
    val pensionerNationalId: String?,
    val userFirstName: String?,
    val userLastName: String?,
    val pensionerId: String?,
    val bankName: String?,
    val bankBranch: String?,
    val installmentAmount: String?,
    val installmentCount: String?,
    val loanAmount: String?,
    val guaranteeAmount: String?
)

data class ResultFollowUpObjectionPR(
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