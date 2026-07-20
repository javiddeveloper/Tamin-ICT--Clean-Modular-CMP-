package com.tamin.taminhamrah.model.pension.checkRetirementStatus

import kotlinx.serialization.Serializable

@Serializable
data class RetirementStatusDTO(
    val requestId: String? = null,
    val requestStatusCode: String? = null
)
