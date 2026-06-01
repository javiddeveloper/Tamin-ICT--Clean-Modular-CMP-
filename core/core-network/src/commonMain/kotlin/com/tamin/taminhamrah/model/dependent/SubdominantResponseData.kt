package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class SubdominantResponseData(
    @SerialName("list") val list: List<RelationWithTaminItem>? = null,
    @SerialName("total") val total: String? = null,
)
