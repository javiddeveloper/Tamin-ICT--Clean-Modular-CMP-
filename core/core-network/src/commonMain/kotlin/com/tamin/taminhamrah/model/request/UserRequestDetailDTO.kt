package com.tamin.taminhamrah.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShortTermStatusDto(
    @SerialName("date_acc") val dateAcc: Long? = null,
    @SerialName("process_result") val processResult: String? = null,
    @SerialName("rejectReson") val rejectReason: String? = null
)

@Serializable
data class ShortTermRequestInfoDto(
    @SerialName("mobile") val mobile: String? = null,
    @SerialName("risuFullName") val risuFullName: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("shorttermArutz") val shorttermArutz: List<ShortTermOrthoticsDto>? = null,
    @SerialName("consequential") val consequential: List<ConsequentialDto>? = null,
    @SerialName("shorttermIllness") val shorttermIllness: List<ShortTermIllnessDto>? = null,
    @SerialName("shorttermPragnent") val shorttermPragnent: List<ShortTermPregnancyDto>? = null
)

@Serializable
data class ShortTermOrthoticsDto(
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermRequestDto? = null,
    @SerialName("useTaj") val useTaj: Long? = null,
    @SerialName("bimSDate") val bimSDate: Long? = null,
    @SerialName("bimEdate") val bimEdate: Long? = null,
    @SerialName("bimDrname") val bimDrname: String? = null,
    @SerialName("bimDrid") val bimDrid: String? = null
)

@Serializable
data class ConsequentialDto(
    @SerialName("risuId") val risuId: String? = null,
    @SerialName("bletenddate") val bletEndDate: String? = null,
    @SerialName("brithDate") val birthDate: String? = null,
    @SerialName("relationShip") val relationship: String? = null,
    @SerialName("risuLName") val risuLName: String? = null,
    @SerialName("risuFname") val risuFname: String? = null,
    @SerialName("cityName") val cityName: String? = null,
    @SerialName("risuIdNo") val risuIdNo: String? = null
)

@Serializable
data class ShortTermIllnessDto(
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermRequestDto? = null,
    @SerialName("bimSDate") val bimSDate: Long? = null,
    @SerialName("bimEdate") val bimEdate: Long? = null,
    @SerialName("bimDrname") val bimDrname: String? = null,
    @SerialName("bimDrid") val bimDrid: String? = null
)

@Serializable
data class ShortTermPregnancyDto(
    @SerialName("barDRid") val barDrid: String? = null,
    @SerialName("barDd") val barDd: String? = null,
    @SerialName("barDemDat") val barDemDat: Long? = null,
    @SerialName("barDrname") val drName: String? = null,
    @SerialName("barSDate") val startDate: Long? = null,
    @SerialName("barEDate") val endDate: Long? = null,
    @SerialName("barType") val barType: String? = null,
    @SerialName("barChild") val barChild: String? = null,
    @SerialName("shorttermRequest") val shorttermRequest: ShortTermRequestDto? = null,
    val pregnancyStatusDesc: String? = null,
    val pregnancyTypeDesc: String? = null
)

@Serializable
data class ShortTermRequestDto(
    @SerialName("bankAccount") val bankAccount: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("resultMessage") val resultMessage: String? = null,
    @SerialName("requestFileList") val fileList: List<RequestFileDto>? = null
)

@Serializable
data class RequestFileDto(
    @SerialName("documentFile") val documentFile: String? = null,
    @SerialName("documentType") val documentType: String? = null
)

@Serializable
data class PregnancyStatusDto(
    @SerialName("code") val code: String? = null,
    @SerialName("name") val name: String? = null
)

@Serializable
data class PregnancyTypeDto(
    @SerialName("code") val code: String? = null,
    @SerialName("name") val name: String? = null
)

@Serializable
data class Article16RequestInfoDto(
    @SerialName("defectDesc") val defectDesc: String? = null,
    @SerialName("objectionPhotos") val objectionPhotos: List<ObjectionPhotoDto>? = null
)

@Serializable
data class ObjectionPhotoDto(
    @SerialName("guid") val guid: String? = null,
    @SerialName("seqNo") val seqNo: Int? = null,
    @SerialName("type") val type: String? = null
)

@Serializable
data class DeferredInstallmentInfoDto(
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("birthDate") val birthDate: Long? = null,
    @SerialName("pensionerNationalId") val pensionerNationalId: String? = null,
    @SerialName("userFirstName") val userFirstName: String? = null,
    @SerialName("userLastName") val userLastName: String? = null,
    @SerialName("pensionerId") val pensionerId: String? = null,
    @SerialName("bank") val bank: BankDto? = null,
    @SerialName("bankBranch") val bankBranch: String? = null,
    @SerialName("installmentAmount") val installmentAmount: Long? = null,
    @SerialName("installmentCount") val installmentCount: Int? = null,
    @SerialName("loanAmount") val loanAmount: Long? = null,
    @SerialName("guaranteeAmount") val guaranteeAmount: Long? = null
)

@Serializable
data class BankDto(
    @SerialName("bankName") val bankName: String? = null
)

@Serializable
data class ResultFollowUpObjectionDto(
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
    @SerialName("brchName") val branchName: String? = null
)

@Serializable
data class ObjectionTypeDto(
    @SerialName("investigationItems") val investigationItems: List<ObjectionTypeNameValueDto>? = null,
    @SerialName("items") val items: List<ObjectionTypeNameValueDto>? = null
)

@Serializable
data class ObjectionTypeNameValueDto(
    @SerialName("name") val name: String? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class DownloadFileDto(
    @SerialName("guid") val guid: String? = null,
    @SerialName("fileName") val fileName: String? = null,
    @SerialName("fileNameRes") val fileNameRes: String? = null,
    @SerialName("fileType") val fileType: String? = null,
    @SerialName("fileUri") val fileUri: String? = null
)
