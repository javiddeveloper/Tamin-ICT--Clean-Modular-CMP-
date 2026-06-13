package com.tamin.taminhamrah.model.common

data class CountryDN(
    val code: String,
    val name: String,
    val phoneCode: String,
    val mobilePattern: String,
    val landlinePattern: String? = null,
    val specialPattern: String? = null,
    val formatExample: String,
    val flagEmoji: String,
    val flagResourceName: String,
)
