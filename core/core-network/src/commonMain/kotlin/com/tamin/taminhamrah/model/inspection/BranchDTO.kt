package com.tamin.taminhamrah.model.inspection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchDTO(
    @SerialName("operation") val operation: String? = null,
    @SerialName("code") val code: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("minCode") val minCode: String? = null,
    @SerialName("maxCode") val maxCode: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("branchAddress") val branchAddress: String? = null,
    @SerialName("cityCode") val cityCode: String? = null,
    @SerialName("status") val status: String? = null
)
