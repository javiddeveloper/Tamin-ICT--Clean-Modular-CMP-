package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class Nation(
    @SerialName("nationCode") val nationCode: String? = null,
    @SerialName("nationDesc") val nationDesc: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: String? = null,
)
