package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelationWithTamin(
    @SerialName("personal") val personal: Personal? = null,
    @SerialName("relationWithTamin") val relationWithTamin: SubRelationWithTamin? = null,
)
