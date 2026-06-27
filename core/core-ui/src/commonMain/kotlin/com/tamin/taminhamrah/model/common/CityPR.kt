package com.tamin.taminhamrah.model.common

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class CityPR(
    val cityCode: String,
    val cityName: String,
    val provinceCode: String? = null,
)
