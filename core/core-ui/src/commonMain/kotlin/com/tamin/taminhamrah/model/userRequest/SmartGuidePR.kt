package com.tamin.taminhamrah.model.userRequest

import androidx.compose.runtime.Immutable

@Immutable
data class SmartGuidePR(
    val id: Long,
    val question: String,
    val reply: String,
    val requestCode: String,
    val requestDesc: String,
    val isPublic: Boolean,
    val title: String,
    val description: String,
)
