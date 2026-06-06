package com.tamin.taminhamrah.model.subDominant.insuredActiveBranch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuredActiveBranchData(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
)
