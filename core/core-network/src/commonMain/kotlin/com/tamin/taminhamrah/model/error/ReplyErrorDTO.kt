package com.tamin.taminhamrah.model.error

import com.tamin.taminhamrah.tools.ProblemDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The fields every Tamin reply carries besides `data`. Read on its own because `data` changes
 * shape on a failure — see [ErrorDataEnvelopeDTO], [ViolationsEnvelopeDTO], [TextDataEnvelopeDTO].
 */
@Serializable
internal data class ReplyEnvelopeDTO(
    @SerialName("status") val status: Int? = null,
    @SerialName("family") val family: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("hasError") val hasError: Boolean? = null,
    @SerialName("problems") val problems: List<ProblemDTO>? = null,
    /** Not Tamin's: a framework error body (`{timestamp,status,error,message,path}`) puts its text here. */
    @SerialName("message") val message: String? = null,
)

/** A failed reply whose `data` is `{cause, message}`. */
@Serializable
internal data class ErrorDataEnvelopeDTO(
    @SerialName("data") val data: ErrorDataDTO? = null,
)

@Serializable
internal data class ErrorDataDTO(
    @SerialName("message") val message: String? = null,
    @SerialName("cause") val cause: String? = null,
)

/** A validation failure: `data` is `[{propertyViolations: {field: [message, …]}}]`. */
@Serializable
internal data class ViolationsEnvelopeDTO(
    @SerialName("data") val data: List<ViolationDTO>? = null,
)

@Serializable
internal data class ViolationDTO(
    @SerialName("propertyViolations") val propertyViolations: Map<String, List<String>>? = null,
)

/** A failed reply whose `data` is the message itself. */
@Serializable
internal data class TextDataEnvelopeDTO(
    @SerialName("data") val data: String? = null,
)
