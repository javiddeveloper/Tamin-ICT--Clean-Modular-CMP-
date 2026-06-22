package com.tamin.taminhamrah.model.common

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ProvincePR(
    val provinceCode: String,
    val provinceName: String,
)
