package com.tamin.taminhamrah.model.personal

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PersonalPR(
    val firstName: String,
    val lastName: String,
    val fatherName: String,
    val nationalId: String,
    val ssn: String,
    val genderDesc: String,
    val dateOfBirth: String,
)
