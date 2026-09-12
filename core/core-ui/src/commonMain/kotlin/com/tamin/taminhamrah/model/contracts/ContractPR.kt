package com.tamin.taminhamrah.model.contracts

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ContractPR(
    val contractNumber: String,
    val statusDesc: String,
    val isActive: Boolean,
    val requestDate: String,
    val insuranceType: String,
    val monthlyPremiumLabel: String,
    val monthlyIncome: String,
    val hasTreatmentSupport: Boolean,
    val jobTitle: String,
)
