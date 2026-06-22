package com.tamin.taminhamrah.model.personal.saveSurvivorInfo

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SaveSurvivorInfoPR(
    val address: String? = null,
    val age: String? = null,
    val birthDate: Long? = null,
    val branchCode: String? = null,
    val survivorInsuranceId: String? = null,
    val survivorNationalId: String? = null,
    val deathType: String? = null,
    val dependencyType: DependencyTypePR? = null,
    val fatherName: String? = null,
    val firstName: String? = null,
    val gender: String? = null,
    val idCardNumber: String? = null,
    val insuranceNumber: String? = null,
    val issuePlace: String? = null,
    val lastName: String? = null,
    val mobileNumber: String? = null,
    val deceasedNationalId: String? = null,
    val pensionId: String? = null,
    val pensionRequestDocList: List<PensionDocPR>? = null,
    val phoneNumber: String? = null,
    val status: String? = null,
)
