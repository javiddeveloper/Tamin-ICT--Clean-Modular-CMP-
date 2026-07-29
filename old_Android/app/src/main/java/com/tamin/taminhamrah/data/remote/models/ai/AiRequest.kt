package com.tamin.taminhamrah.data.remote.models.ai

import kotlinx.serialization.Serializable


@Serializable
data class AiRequest(
    var prompt: String? = "",
    var sessionId: String? = null,

    var lastEntity: String? = ""
)

