package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class RelationWithTaminItem(
    @SerialName("id") val id: Long? = null,
    @SerialName("relationWithTamin") val relationWithTamin: RelationWithTamin? = null,
    @SerialName("request") val request: Any? = null,
)
