package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentModel(
    var debitNumber: String? = null,
    var amount: Long? = null,
    var reason: String? = null,
    var branchCode: String? = null,
    var workshopId: String? = null,
    var peymanSequence: String? = null,
    val preCheck:Boolean?=false,
    val seporde:Boolean=false
):Parcelable