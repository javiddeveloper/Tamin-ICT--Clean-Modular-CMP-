package com.tamin.taminhamrah.model.inbox

import androidx.compose.runtime.Immutable

@Immutable
data class PersonalInboxItemPR(
    val id: Long,
    val refCode: String,
    val requestDate: String,
    val system: String,
    val subject: String,
    val passwordCode: String,
    val seen: Boolean,
)

@Immutable
data class PersonalInboxSizePR(
    val usageMb: String,
    val totalMb: String,
    val usageLabel: String,
    val totalLabel: String,
)
