package com.tamin.taminhamrah.model.changeMobile

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class EditMobileResponsePR(
    val traceId: String = "",
    val data: EditMobilePR? = null
)

@Immutable
@Serializable
data class EditMobilePR(
    val hash: String = "",
    val expirationTime: Long? = null
)
