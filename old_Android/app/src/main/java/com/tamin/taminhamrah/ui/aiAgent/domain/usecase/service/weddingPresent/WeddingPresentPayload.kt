package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent

import com.tamin.taminhamrah.data.remote.models.services.ShorttremMariageReq
import com.tamin.taminhamrah.data.remote.models.services.WeddingPresentModel
import com.tamin.taminhamrah.data.remote.models.services.asRequestInput

fun WeddingPresentModel.toPayloadMap(): Map<String, String?> = mapOf(
    "risuid" to risuid,
    "insuranceFirstName" to insuranceFirstName,
    "insuranceLastName" to insuranceLastName,
    "nationalCode" to nationalCode,
    "insuranceTypeDesc" to insuranceTypeDesc,
    "insuranceStatusDesc" to insuranceStatusDesc,
    "bankAccount" to bankAccount,
    "bankName" to bankName,
    "branchName" to branchName,
    "mobilNumber" to mobilNumber,
    "requestHelpType" to requestHelpType,
    "serviceDateTimeStamp" to serviceDateTimeStamp,
    "branchCode" to branchCode,
)

fun Map<String, Any?>?.toWeddingPresentModel(): WeddingPresentModel {
    return WeddingPresentModel(
        risuid = this?.get("risuid")?.toString(),
        insuranceFirstName = this?.get("insuranceFirstName")?.toString(),
        insuranceLastName = this?.get("insuranceLastName")?.toString(),
        nationalCode = this?.get("nationalCode")?.toString(),
        insuranceTypeDesc = this?.get("insuranceTypeDesc")?.toString(),
        insuranceStatusDesc = this?.get("insuranceStatusDesc")?.toString(),
        bankAccount = this?.get("bankAccount")?.toString(),
        bankName = this?.get("bankName")?.toString(),
        branchName = this?.get("branchName")?.toString(),
        mobilNumber = this?.get("mobilNumber")?.toString(),
        requestHelpType = this?.get("requestHelpType")?.toString(),
        serviceDateTimeStamp = this?.get("serviceDateTimeStamp")?.toString(),
        branchCode = this?.get("branchCode")?.toString(),
    )
}

fun buildMarriageGiftRequest(payload: Map<String, Any?>?): ShorttremMariageReq {
    val partnerNationalId = payload?.get("partnerNationalCode")?.toString().orEmpty()
    val weddingDateTimeStamp = payload?.get("weddingDateTimestamp")?.toString()?.toLongOrNull() ?: 0L
    val userInfo = payload.toWeddingPresentModel()
    return ShorttremMariageReq(
        partnerNationalId = partnerNationalId,
        shorttermRequest = userInfo.asRequestInput(),
        weddingDateTimeStamp = weddingDateTimeStamp
    )
}

fun Map<String, Any?>?.mergePayload(updates: Map<String, Any?>): MutableMap<String, Any?> {
    val merged = this?.toMutableMap() ?: mutableMapOf()
    merged.putAll(updates)
    return merged
}
