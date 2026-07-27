package com.tamin.taminhamrah.model.common

import kotlinx.serialization.Serializable

@Serializable
data class RoleDto(
    val id: Int? = null,
    val title: String? = null
)
