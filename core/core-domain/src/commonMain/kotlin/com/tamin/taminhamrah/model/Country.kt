/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.core.model

data class Country(
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

