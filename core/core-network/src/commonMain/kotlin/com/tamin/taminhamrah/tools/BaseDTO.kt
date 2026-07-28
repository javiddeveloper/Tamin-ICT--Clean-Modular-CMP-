/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.tools

import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Serializable
data class BaseDTO<out T>(
    @SerialName("status") val status: Int,
    @SerialName("family") val family: String,
    @SerialName("reason") val reason: String,
    @SerialName("data") val data: T? = null
)

/**
 * Extracts data from BaseDTO. Throws exception if status is not 2xx or data is null.
 * Use this when you expect a specific DTO as data.
 */
fun <T> BaseDTO<T>.extractData(): T {
    println("BaseDTO: Extracting data from BaseDTO: status=$status, family=$family, reason=$reason, hasData=${data != null}")

    return when {
        status in 200..299 && data != null -> {
            println("BaseDTO: Data extraction successful")
            data
        }
        status in 400..499 -> {
            println("BaseDTO: Client error: $reason")
            throw TaminErrorUriException(ErrorUri.fromString("CLIENT_ERROR: $reason"))
        }
        status in 500..599 -> {
            println("BaseDTO: Server error: $reason")
            throw TaminErrorUriException(ErrorUri.fromString("SERVER_ERROR: $reason"))
        }
        data == null -> {
            println("BaseDTO: Data is null")
            throw TaminErrorUriException(ErrorUri.fromString("NULL_DATA: $reason"))
        }
        else -> handleCommonErrors()
    }
}

/**
 * Extracts a success message from the response.
 * If data is a primitive (like String), it returns its content.
 * If data is null or an object, it returns the 'reason' field as the message.
 */
fun BaseDTO<JsonElement?>.extractMessage(): String {
    println("BaseDTO: Extracting message: status=$status, family=$family, reason=$reason")

    return when {
        status in 200..299 -> {
            val message = (data as? JsonPrimitive)?.contentOrNull ?: reason
            println("BaseDTO: Success message extracted: $message")
            message
        }
        else -> handleCommonErrors()
    }
}

/**
 * Handles error statuses and throws appropriate TaminErrorUriException.
 */
private fun <T> BaseDTO<T>.handleCommonErrors(): Nothing {
    val errorPrefix = when (status) {
        in 400..499 -> "CLIENT_ERROR"
        in 500..599 -> "SERVER_ERROR"
        else -> "UNKNOWN_ERROR"
    }
    println("BaseDTO: $errorPrefix: $reason")
    throw TaminErrorUriException(ErrorUri.fromString("$errorPrefix: $reason"))
}
