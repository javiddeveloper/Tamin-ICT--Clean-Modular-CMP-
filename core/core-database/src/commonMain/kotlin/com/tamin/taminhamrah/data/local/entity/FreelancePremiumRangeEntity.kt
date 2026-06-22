package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "freelance_premium_ranges")
data class FreelancePremiumRangeEntity(
    @PrimaryKey
    val id: String,
    val treatmentSupportCode: String,
    val spcRateCode: String,
    val insuranceId: String,
    val paymentTabayi: Long,
    val lowPremium: Long,
    val highPremium: Long,
    val history: Int,
)
