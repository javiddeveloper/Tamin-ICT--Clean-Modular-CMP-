package com.tamin.taminhamrah.model.treatment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ElectronicPrescriptionPricePR(
    val headInsuPayment: String,
    val headSsoPayment: String,
    val noteHeadEprescID: String,
    val requestPrice: String
)
