package com.tamin.taminhamrah.model.inbox

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.ui.ActionMenuItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PersonalInboxItemPR(
    val id: Long,
    val refCode: String,
    val requestDate: String,
    val system: String,
    val subject: String,
    val passwordCode: String,
    val seen: Boolean,
    val natCode: String,
    val email: String,
    val mobile: String,
    val permissionPassword: String,
    val actions: ImmutableList<ActionMenuItem<String>> = persistentListOf()
)

@Immutable
data class PersonalInboxSizePR(
    val usageMb: String,
    val totalMb: String,
    val usageLabel: String,
    val totalLabel: String,
)

@Immutable
data class PermitDurationPR(
    val label: String,
    val valueInDays: Int,
)
