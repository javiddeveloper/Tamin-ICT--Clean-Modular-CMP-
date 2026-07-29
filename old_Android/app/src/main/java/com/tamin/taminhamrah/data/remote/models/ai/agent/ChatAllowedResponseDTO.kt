package com.tamin.taminhamrah.data.remote.models.ai.agent

import com.google.gson.annotations.SerializedName

data class ChatAllowedResponseDTO(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("family")
    val family: String?,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("traceId")
    val traceId: String?,
    @SerializedName("data")
    val data: ChatAllowedData?,
    @SerializedName("canSendVoice")
    val canSendVoice: Boolean?
)

data class ChatAllowedData(
    @SerializedName("canStartChat")
    val canStartChat: Boolean?,
    @SerializedName("chatToken")
    val chatToken: String?,
    @SerializedName("ttl")
    val ttl: Int?,
    @SerializedName("errorMessage")
    val errorMessage: String?,
    var expiresAt: Long? = null,
    @SerializedName("canSendVoice")
    var canSendVoice: Boolean? = null
)
