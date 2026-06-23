package com.tamin.taminhamrah.model.pension

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PayRollPR(
    val id: Int = 0,
    val clpType: String = "",
    val tprDesc: String = "",
    val sumAmount: Long = 0,
    val textNumber: String = "",
    val sumPay: Long = 0,
    val hisYear: String = "",
    val hisMon: String = "",
    val hisDay: String = "",
    val hisYearPlus: String = "",
    val hisMonPlus: String = "",
    val hisDayPlus: String = ""
)
