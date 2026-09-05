package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One entry of `debit-reason` — the علت ایجاد بدهی picker behind the payment-sheet search. */
@Serializable
data class DebitReasonDTO(
    @SerialName("debitCreateReasonCode") val code: String? = null,
    @SerialName("debitCreateReasonDesc") val title: String? = null,
    @SerialName("amtStatus") val amountStatus: String? = null,
    @SerialName("ordCase") val orderCase: String? = null,
)
