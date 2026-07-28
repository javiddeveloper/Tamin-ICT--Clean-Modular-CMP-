package com.tamin.taminhamrah.model.treatment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ElectronicPrescriptionPR(
    val id: String,
    val docId: String,
    val docName: String,
    val flagSata: String,
    val location: String,
    val noteHeadEprescID: String,
    val patientID: String,
    val patientName: String,
    val prescDate: String,
    val prescName: String,
    val specDesc: String,
    val prescType: String,
    val trackingCode: String
)
