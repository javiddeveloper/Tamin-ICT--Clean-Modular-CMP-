package com.tamin.taminhamrah.model.subDominant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubDominantResponseItem(
    @SerialName("id") val id: Long? = null,
    @SerialName("relationWithTamin") val relationWithTamin: RelationWithTamin? = null
)
