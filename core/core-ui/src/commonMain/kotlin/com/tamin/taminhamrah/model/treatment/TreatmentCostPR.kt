package com.tamin.taminhamrah.model.treatment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class TreatmentCostPR(
    val repId: String,
    val nameFamil: String,
    val healthcenterName: String,
    val payPrice: String,
    val payStatusDesc: String,
    val estimatePayDate: String,
    val rahgiriCode: String,
    val serviceDate: String,
    val statusDesc: String,
    val accountNumber: String,
    val bimeCode: String,
    val datePaz: String,
    val famil: String,
    val mainNational: String,
    val maliCode: String,
    val name: String,
    val nameAsli: String,
    val noPazir: String,
    val payNatCode: String,
    val payOtherService: String,
    val payService: String,
    val payStatus: String,
    val payType: String,
    val province: String,
    val releaseDate: String,
    val status: String,
    val returnReason: String
)
