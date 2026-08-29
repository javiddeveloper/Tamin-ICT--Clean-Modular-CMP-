package com.tamin.taminhamrah.model.personal.survivorDependent

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SurvivorDependentPR(
    val firstName: String,
    val lastName: String,
    val nationalId: String,
    val fatherName: String,
    val idCardNumber: String,
    val cityOfIssue: String,
    val genderCode: String,
    val genderDesc: String,
    val dateOfBirth: String,
    val insuranceId: String,
    val tendencyCode: String,
)
