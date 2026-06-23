package com.tamin.taminhamrah.model.pension.fish

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PayRollDTO (
    @SerialName("id") val id: Int? = null,
    @SerialName("clpType") val clpType: String? = null,
    @SerialName("tprDesc") val tprDesc: String? = null,
    @SerialName("sumAmount") val sumAmount: Long? = null,
    @SerialName("textNumber") val textNumber: String? = null,
    @SerialName("sumPay") val sumPay: Long? = null,
    @SerialName("hisYear") val hisYear: String? = null,
    @SerialName("hisMon") val hisMon: String? = null,
    @SerialName("hisDay") val hisDay: String? = null,
    @SerialName("hisYearPlus") val hisYearPlus: String? = null,
    @SerialName("hisMonPlus") val hisMonPlus: String? = null,
    @SerialName("hisDayPlus") val hisDayPlus: String? = null
)
