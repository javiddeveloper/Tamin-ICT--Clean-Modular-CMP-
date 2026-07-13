package com.tamin.taminhamrah.model.pension.authenticationTicket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationTicketDTO(
    @SerialName("mobileNumber") val mobileNumber: String? = null
)
