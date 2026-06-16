package com.tamin.taminhamrah.model.pension

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class EdictPensionerDetailPR(
    val fieldDesc: String,
    val fieldValue: String,
    val index: String,
    val packageName: String,
)
