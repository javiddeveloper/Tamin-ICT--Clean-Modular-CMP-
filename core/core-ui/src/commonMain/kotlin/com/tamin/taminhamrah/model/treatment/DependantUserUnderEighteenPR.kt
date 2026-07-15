package com.tamin.taminhamrah.model.treatment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DependantUserUnderEighteenPR(
    val id: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val nationalId: String
)
