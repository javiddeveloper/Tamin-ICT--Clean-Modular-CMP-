package com.tamin.taminhamrah.model.treatment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DeservedTreatmentPR(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val nationalId: String,
    val natCode: String,
    val birthDate: String,
    val brhCode: String,
    val brhName: String,
    val dependenceType: String,
    val fatherName: String,
    val feranshiz: String,
    val gender: String,
    val healthBookletDate: String,
    val insuranceType: String,
    val lastBookletDate: String,
    val parentRisuid: String,
    val provinceCode: String,
    val provinceName: String,
    val regWorkshopId: String,
    val regWorkshopName: String,
    val risuid: String,
    val message: String,
    val finalDesc: String,
    val illness: String,
    val trackingCode: String
)
