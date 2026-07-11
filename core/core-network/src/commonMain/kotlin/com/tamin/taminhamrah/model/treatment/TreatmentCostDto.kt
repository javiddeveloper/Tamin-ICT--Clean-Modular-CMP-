package com.tamin.taminhamrah.model.treatment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DependantUserUnderEighteenDTO(
    @SerialName("relationWithTamin") val relationWithTamin: RelationWithTaminDTO? = null,
    @SerialName("id") val id: Long? = null
)

@Serializable
data class RelationWithTaminDTO(
    @SerialName("personal") val personal: PersonalDTO? = null
)

@Serializable
data class PersonalDTO(
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null
)
