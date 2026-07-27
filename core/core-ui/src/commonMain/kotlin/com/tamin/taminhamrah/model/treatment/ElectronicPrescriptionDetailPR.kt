package com.tamin.taminhamrah.model.treatment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ElectronicPrescriptionDetailPR(
    val sumPriceItem: String,
    val ssoPayment: String,
    val insurancePayment: String,
    val serviceQuantity: String,
    val noteHeadEprescID: String,
    val serverCode: String,
    val serverName: String,
    val serviceName: String,
    val drugInst: String,
    val registerDate: String,
    val drugInstruction: String,
    val deliveredNo: String,
    val drugAmount: String
)
