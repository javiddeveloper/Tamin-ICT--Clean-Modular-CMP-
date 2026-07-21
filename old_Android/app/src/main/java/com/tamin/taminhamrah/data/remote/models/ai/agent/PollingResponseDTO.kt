package com.tamin.taminhamrah.data.remote.models.ai.agent

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class PollingResponseDTO(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("family")
    val family: String?,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("traceId")
    val traceId: String?,
    @SerializedName("data")
    val data: PollingData?
)

data class PollingData(
    @SerializedName("id")
    val id: String?,
    @SerializedName("eta")
    val eta: Int?,
    @SerializedName("status")
    val status: String?, // PENDING, DONE, FAILED
    @SerializedName("result")
    val result: AgentResponseDTO?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("errorStatus")
    val errorStatus: String?
)

data class CancelResponseDTO(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("family")
    val family: String?,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("traceId")
    val traceId: String?,
    @SerializedName("data")
    val data: CancelData?
)

data class CancelData(
    @SerializedName("message")
    val message: String?
)
