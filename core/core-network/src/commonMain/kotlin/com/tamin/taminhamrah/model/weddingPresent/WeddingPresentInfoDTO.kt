package com.tamin.taminhamrah.model.weddingPresent

import com.tamin.taminhamrah.tools.ErrorCarrier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class WeddingPresentInfoDTO(
    @SerialName("request") val request: WeddingPresentRequestDTO? = null,
    @SerialName("insuranceType") val insuranceType: String? = null,
    @SerialName("insuranceTypeDesc") val insuranceTypeDesc: String? = null,
    @SerialName("insuranceStatus") val insuranceStatus: String? = null,
    @SerialName("insuranceStatusDesc") val insuranceStatusDesc: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("requestHelpType") val requestHelpType: String? = null,
    @SerialName("serviceDate") val serviceDate: String? = null,
    @SerialName("resultMessage") val resultMessage: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("bankAccount") val bankAccount: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("shorttemRequestId") val shortTermRequestId: String? = null,
    @SerialName("payDocNo") val payDocNo: String? = null,
    @SerialName("payment") val payment: String? = null,
    @SerialName("requestFileList") val requestFileList: List<JsonElement>? = null,
    @SerialName("serviceDateTimeStamp") val serviceDateTimeStamp: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("insuranceFirstName") val insuranceFirstName: String? = null,
    @SerialName("insuranceLastName") val insuranceLastName: String? = null,
    @SerialName("mobilNumber") val mobileNumber: String? = null,
    @SerialName("requestHelpTypeDesc") val requestHelpTypeDesc: String? = null,
    @SerialName("stringDocFiles") val stringDocFiles: String? = null,
    @SerialName("branchWorkshop") val branchWorkshop: List<WeddingPresentBranchWorkshopDTO>? = null,
    @SerialName("requestedBrchName") val requestedBranchName: String? = null,
    @SerialName("requestFileList1") val requestFileList1: String? = null,
    @SerialName("genderCode") val genderCode: String? = null,
    @SerialName("flag") val flag: Boolean? = null,
    @SerialName("partnerNationalId") val partnerNationalId: String? = null,
    @SerialName("weddingTimestamp") val weddingTimestamp: Long? = null,
    @SerialName("consequential") val consequential: String? = null,
    @SerialName("message") override val message: String? = null,
    @SerialName("cause") override val cause: String? = null,
) : ErrorCarrier

@Serializable
data class WeddingPresentRequestDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("systemType") val systemType: String? = null,
    @SerialName("requestDate") val requestDate: Long? = null,
    @SerialName("userId") val userId: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("requestType") val requestType: String? = null,
    @SerialName("editDate") val editDate: Long? = null,
    @SerialName("editUser") val editUser: String? = null,
    @SerialName("refrenceCode") val referenceCode: String? = null,
    @SerialName("brchCode") val branchCode: String? = null,
    @SerialName("statusName") val statusName: String? = null,
    @SerialName("statusId") val statusId: String? = null,
)

@Serializable
data class WeddingPresentBranchWorkshopDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
)
