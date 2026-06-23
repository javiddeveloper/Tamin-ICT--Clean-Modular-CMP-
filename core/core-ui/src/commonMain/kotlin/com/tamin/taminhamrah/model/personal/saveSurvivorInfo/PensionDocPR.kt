package com.tamin.taminhamrah.model.personal.saveSurvivorInfo

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PensionDocPR(
    val documentType: String? = null,
    val guid: String? = null,
)
