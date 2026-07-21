package com.tamin.taminhamrah.data.remote.models.ai.agent

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgentRequest(
    @SerializedName("prompt")
    @SerialName("prompt")
    var prompt: String? = "",
    @SerializedName("sessionId")
    @SerialName("sessionId")
    var sessionId: String? = null,
    @SerializedName("lastEntity")
    @SerialName("lastEntity")
    var lastEntity: String? = "",
    @SerializedName("chatToken")
    @SerialName("chatToken")
    var chatToken: String? = ""
)