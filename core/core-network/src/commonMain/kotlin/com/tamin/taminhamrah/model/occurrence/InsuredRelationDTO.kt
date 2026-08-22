package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * occurence/insured-relation is part of the same legacy occurrence backend as
 * [com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO] and
 * [com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO], both confirmed to omit keys outright
 * in real responses — defaulting every field here defensively avoids the same
 * [kotlinx.serialization.MissingFieldException].
 */
@Serializable
data class InsuredRelationDTO(
    @SerialName("isuType") val insuranceTypeCode: String? = null,
    @SerialName("isuTypeDesc") val insuranceType: String? = null,
    @SerialName("brhCode") val branchCode: String? = null,
    @SerialName("brhName") val branchName: String? = null,
)
