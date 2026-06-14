package com.tamin.taminhamrah.model.subDominant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubDominantResponseData(
    @SerialName("list") val list: List<SubDominantResponseItem> ? = null,
    @SerialName("pageNumber") val pageNumber: Int ? = null,
    @SerialName("pageSize") val pageSize: Int ? = null,
    @SerialName("totalCount") val totalCount: Int ? = null,
)
