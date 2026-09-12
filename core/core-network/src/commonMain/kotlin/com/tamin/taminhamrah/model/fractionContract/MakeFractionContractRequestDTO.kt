package com.tamin.taminhamrah.model.fractionContract

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Body matches legacy `FractionRequestDataModel` — literal premium string. */
@Serializable
data class MakeFractionContractRequestDTO(
    @SerialName("premium") val premium: String = "this.premium",
)
