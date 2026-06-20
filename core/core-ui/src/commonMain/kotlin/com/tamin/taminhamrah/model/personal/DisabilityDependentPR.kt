package com.tamin.taminhamrah.model.personal

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DisabilityDependentPR(
    val firstName: String,
    val lastName: String,
    val nationalId: String,
    val dateOfBirth: String,
    val fatherName: String,
    val genderDesc: String,
    val relation: String,
    val tendencyDescription: String,
)
