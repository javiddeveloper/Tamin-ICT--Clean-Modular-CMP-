package com.tamin.taminhamrah.model.contracts

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
data class BranchPR(
    val code: String,
    val name: String,
)
