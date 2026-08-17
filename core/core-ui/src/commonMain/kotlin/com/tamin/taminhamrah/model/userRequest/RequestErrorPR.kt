package com.tamin.taminhamrah.model.userRequest

import androidx.compose.runtime.Immutable

@Immutable
data class RequestErrorPR(
    val id: Long,
    val errorMessage: String,
    val errorType: String,
    val errorStatus: String,
    val creationTimeJalali: String,
)
