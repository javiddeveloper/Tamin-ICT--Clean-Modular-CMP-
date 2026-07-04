package com.tamin.taminhamrah.model.pension.authenticationTicket

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AuthenticationTicketPR(
    val mobileNumber: String? = null
)
