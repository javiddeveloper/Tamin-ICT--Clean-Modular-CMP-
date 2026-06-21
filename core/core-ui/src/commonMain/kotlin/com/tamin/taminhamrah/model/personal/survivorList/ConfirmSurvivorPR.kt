package com.tamin.taminhamrah.model.personal.survivorList

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ConfirmSurvivorPR(
    val request: RequestModelPR?
)

@Immutable
@Serializable
data class RequestModelPR(
    val id: Int?
)
