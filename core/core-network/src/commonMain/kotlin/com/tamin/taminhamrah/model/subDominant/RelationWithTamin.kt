package com.tamin.taminhamrah.model.subDominant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelationWithTamin(
    @SerialName("id") val id: Long? = null,
    // Sibling of "personal" in the payload, not nested inside it — e.g. "ax00547573".
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("personal") val personal: Personal? = null,
    @SerialName("relationWithTamin") val relationWithTamin: SubRelationWithTamin? = null,
)
