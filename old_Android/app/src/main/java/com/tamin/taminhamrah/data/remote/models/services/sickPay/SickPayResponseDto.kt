package com.tamin.taminhamrah.data.remote.models.services.sickPay

import com.google.gson.annotations.SerializedName

data class SickPayResponseDto(
    @SerializedName("list") val list: List<SickPayItemDto>?
)
data class SickPayItemDto(
    @SerializedName("amount") val amount: Long?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("days") val days: Int?,
    @SerializedName("status") val status: String?)
    // e.g., \"Paid\", \"Pending\")
    // Wrapper for the full API response
    // typealias SickPayFullResponse = BaseResponse<BaseListResponse<SickPayItemDto>?>\n