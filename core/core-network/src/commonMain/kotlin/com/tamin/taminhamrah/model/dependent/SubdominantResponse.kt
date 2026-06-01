package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class SubdominantResponse(
    @SerialName("data") val data: SubdominantResponseData? = null,
    @SerialName("traceId") val traceId: String? = null,
)
