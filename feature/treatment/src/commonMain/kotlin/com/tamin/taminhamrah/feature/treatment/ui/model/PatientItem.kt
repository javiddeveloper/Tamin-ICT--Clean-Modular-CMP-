package com.tamin.taminhamrah.feature.treatment.ui.model

data class PatientItem(
    val nationalId: String,
    val fullName: String,
    val isDependent: Boolean,
    val relation: String? = null,
    val brhName: String? = null,
    val insuranceType: String? = null
)
