package com.tamin.taminhamrah.model.pension.retirement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response of `POST pension-request`. The nested `request.id` is the `requestId` every later call
 * addresses — `PUT pension-request/{requestId}` has nothing to attach documents to without it.
 */
@Serializable
data class RetirementRequestCreatedDTO(
    @SerialName("request") val request: RetirementRequestIdDTO? = null,
)

@Serializable
data class RetirementRequestIdDTO(
    @SerialName("id") val id: Long? = null,
)
