package com.tamin.taminhamrah.model.personal

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AgePR(
    val age: String,
    val birthDate: String,
)
