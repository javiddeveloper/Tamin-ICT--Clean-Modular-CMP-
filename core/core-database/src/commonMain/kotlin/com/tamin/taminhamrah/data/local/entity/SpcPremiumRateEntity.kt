package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spc_premium_rates")
data class SpcPremiumRateEntity(
    @PrimaryKey
    val spcrateCode: String,
    val spcrateDescription: String,
    val selfIsuTypeCode: String?,
    val spcLowDayWage: String?,
    val insurDpercent: String?,
    val govermentPercent: String?,
    val treatmentPercap: String?,
    val payrespitelOne: String?,
    val payrespitelTwo: String?,
    val status: String?,
    val statusStDate: String?,
)
