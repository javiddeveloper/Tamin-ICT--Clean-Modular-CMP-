package com.tamin.taminhamrah.model.subDominant.insuredActiveBranch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuredActiveBranchResponse(
    @SerialName("data") val `data`: List<InsuredActiveBranchData>? = null,
    @SerialName("family") val family: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("status") val status: Int? = null,
    @SerialName("traceId") val traceId: String? = null,
)
