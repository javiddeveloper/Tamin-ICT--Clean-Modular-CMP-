package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Result of `PUT requests/confirm/{requestId}` — confirming a drafted نام نویسی غیر حضوری.
 *
 * [refCode] is the tracking code the confirmation message quotes. The old client showed it with
 * the *error* dialog type even on success; that is a presentation bug, not part of the contract.
 */
@Serializable
data class NewMemberConfirmResultDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("refCode") val refCode: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("status") val status: NewMemberRequestStatusDTO? = null,
)
