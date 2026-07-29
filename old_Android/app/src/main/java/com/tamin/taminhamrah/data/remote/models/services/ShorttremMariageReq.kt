package com.tamin.taminhamrah.data.remote.models.services


data class ShorttremMariageReq(
    val partnerNationalId: String,
    val shorttermRequest: MarriageGiftReq,
    val weddingDateTimeStamp: Long
)
