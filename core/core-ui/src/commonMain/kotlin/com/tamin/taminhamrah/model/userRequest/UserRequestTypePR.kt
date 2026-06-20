package com.tamin.taminhamrah.model.userRequest

import androidx.compose.runtime.Immutable

@Immutable
data class UserRequestTypePR(
    val id: Long,
    val title: String,
    val description: String,
) {
    val displayLabel: String get() = description.ifBlank { title }
}
