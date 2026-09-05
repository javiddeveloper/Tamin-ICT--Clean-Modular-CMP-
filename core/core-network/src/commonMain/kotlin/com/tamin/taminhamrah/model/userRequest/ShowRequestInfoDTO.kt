package com.tamin.taminhamrah.model.userRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShortTermRequestStatusDTO(
    @SerialName("date_acc") val dateAcc: Long? = null,
    @SerialName("process_result") val processResult: String? = null,
    @SerialName("rejectReson") val rejectReason: String? = null,
)

@Serializable
data class ShortTermRequestInfoDTO(
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("risuFullName") val risuFullName: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("shorttermArutz") val shorttermArutz: List<ShortTermOrthoticsDTO>? = null,
    @SerialName("consequential") val consequential: List<ShortTermConsequentialDTO>? = null,
    @SerialName("shorttermIllness") val shorttermIllness: List<ShortTermIllnessDTO>? = null,
    @SerialName("shorttermPragnent") val shorttermPragnent: List<ShortTermPregnancyDTO>? = null,
)

@Serializable
data class ShortTermOrthoticsDTO(
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermRequestDTO? = null,
    @SerialName("useTaj") val useTaj: Long? = null,
    @SerialName("bimSDate") val bimSDate: Long? = null,
    @SerialName("bimEdate") val bimEdate: Long? = null,
    @SerialName("bimDrname") val bimDrname: String? = null,
    @SerialName("bimDrid") val bimDrid: String? = null,
)

@Serializable
data class ShortTermConsequentialDTO(
    @SerialName("risuId") val risuId: String? = null,
    @SerialName("bletenddate") val bletEndDate: String? = null,
    @SerialName("brithDate") val birthDate: String? = null,
    @SerialName("relationShip") val relationship: String? = null,
    @SerialName("risuLName") val risuLName: String? = null,
    @SerialName("risuFname") val risuFname: String? = null,
    @SerialName("cityName") val cityName: String? = null,
    @SerialName("risuIdNo") val risuIdNo: String? = null,
)

@Serializable
data class ShortTermIllnessDTO(
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermRequestDTO? = null,
    @SerialName("bimSDate") val bimSDate: Long? = null,
    @SerialName("bimEdate") val bimEdate: Long? = null,
    @SerialName("bimDrname") val bimDrname: String? = null,
    @SerialName("bimDrid") val bimDrid: String? = null,
)

@Serializable
data class ShortTermPregnancyDTO(
    @SerialName("barDRid") val barDrid: String? = null,
    @SerialName("barDd") val barDd: String? = null,
    @SerialName("barDemDat") val barDemDat: Long? = null,
    @SerialName("barDrname") val drName: String? = null,
    @SerialName("barSDate") val startDate: Long? = null,
    @SerialName("barEDate") val endDate: Long? = null,
    @SerialName("barType") val barType: String? = null,
    @SerialName("barChild") val barChild: String? = null,
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermRequestDTO? = null,
)

@Serializable
data class ShortTermRequestDTO(
    @SerialName("bankAccount") val bankAccount: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("resultMessage") val resultMessage: String? = null,
    @SerialName("requestFileList") val fileList: List<ShortTermRequestFileDTO>? = null,
)

@Serializable
data class ShortTermRequestFileDTO(
    @SerialName("documentFile") val documentFile: String? = null,
    @SerialName("documentType") val documentType: String? = null,
)

@Serializable
data class PregnancyLookupDTO(
    @SerialName("code") val code: String? = null,
    @SerialName("name") val name: String? = null,
)

@Serializable
data class ArticleSixteenRequestInfoDTO(
    @SerialName("defectDesc") val defectDesc: String? = null,
    @SerialName("objectionPhotos") val objectionPhotos: List<ArticleSixteenObjectionPhotoDTO>? = null,
)

@Serializable
data class ArticleSixteenObjectionPhotoDTO(
    @SerialName("guid") val guid: String? = null,
    @SerialName("seqNo") val seqNo: Int? = null,
    @SerialName("type") val type: String? = null,
)

@Serializable
data class DeferredInstallmentInfoDTO(
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("birthDate") val birthDate: Long? = null,
    @SerialName("pensionerNationalId") val pensionerNationalId: String? = null,
    @SerialName("userFirstName") val userFirstName: String? = null,
    @SerialName("userLastName") val userLastName: String? = null,
    @SerialName("pensionerId") val pensionerId: String? = null,
    @SerialName("bank") val bank: DeferredInstallmentBankDTO? = null,
    @SerialName("bankBranch") val bankBranch: String? = null,
    @SerialName("installmentAmount") val installmentAmount: Long? = null,
    @SerialName("installmentCount") val installmentCount: Int? = null,
    @SerialName("loanAmount") val loanAmount: Long? = null,
    @SerialName("guaranteeAmount") val guaranteeAmount: Long? = null,
)

@Serializable
data class DeferredInstallmentBankDTO(
    @SerialName("bankName") val bankName: String? = null,
)

@Serializable
data class FollowUpObjectionHistoryDTO(
    @SerialName("reqno") val requestNumber: String? = null,
    @SerialName("reqtype") val requestType: String? = null,
    @SerialName("brchcode") val branchId: String? = null,
    @SerialName("requestDesc") val requestDesc: String? = null,
    @SerialName("cStatusDesc") val statusDesc: String? = null,
    @SerialName("answerTypeDesc") val answerTypeDesc: String? = null,
    @SerialName("resultDesc") val resultDesc: String? = null,
    @SerialName("userDesc") val userDesc: String? = null,
    @SerialName("requestDate") val requestDate: String? = null,
    @SerialName("answerDate") val answerDate: String? = null,
    @SerialName("brchName") val branchName: String? = null,
)
