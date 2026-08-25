package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Same legacy occurrence backend family as [com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO] — see its doc comment. */
@Serializable
data class OccurrenceDocTypeDTO(
    @SerialName("docTypeId") val docTypeId: String? = null,
    @SerialName("docDesc") val docDesc: String? = null,
)
