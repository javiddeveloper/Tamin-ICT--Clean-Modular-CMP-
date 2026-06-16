package com.tamin.taminhamrah.model.pension

import kotlinx.serialization.Serializable

@Serializable
data class EdictPensionerDetailDTO(
    val fieldDesc: String? = null,
    val fieldValue: String? = "0",
    val index: String? = null,
    val packageName: String? = null,
)
