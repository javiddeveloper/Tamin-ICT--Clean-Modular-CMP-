package com.tamin.taminhamrah.model.fractionContract

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FractionContractResultDTO(
    @SerialName("contractNumber") val contractNumber: Long? = null,
    @SerialName("contractDate") val contractDate: Long? = null,
)
