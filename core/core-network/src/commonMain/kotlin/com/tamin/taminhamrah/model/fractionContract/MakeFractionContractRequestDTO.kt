package com.tamin.taminhamrah.model.fractionContract

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Body matches legacy `FractionRequestDataModel` — premium is required (no placeholder default). */
@Serializable
data class MakeFractionContractRequestDTO(
    @SerialName("premium") val premium: String,
)
