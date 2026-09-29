package com.tamin.taminhamrah.model.user

import com.tamin.taminhamrah.tools.ErrorCarrier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `data` of the image-request reply: `null` on success, `{message, cause}` on an error. */
@Serializable
data class SendImageRequestDTO(
    @SerialName("message") override val message: String? = null,
    @SerialName("cause") override val cause: String? = null,
) : ErrorCarrier
