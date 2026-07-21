package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class WorkshopStackHolderPR(
    val stackId: Int?,
    val nationalId: String?,
    val mobile: String?,
    val stackType: String?
)
