package com.tamin.taminhamrah.model.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchDTO(
    @SerialName("code") val code: String?,
    @SerialName("name") val name: String?,
    @SerialName("minCode") val minCode: String?,
    @SerialName("maxCode") val maxCode: String?,
    @SerialName("type") val type: String?,
    @SerialName("branchAddress") val branchAddress: String?,
    @SerialName("cityCode") val cityCode: String?,
    @SerialName("status") val status: String?,
)
