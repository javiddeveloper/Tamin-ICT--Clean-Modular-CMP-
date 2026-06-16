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

@Serializable
data class BaseDTO<out T>(
    @SerialName("status") val status: Int,
    @SerialName("family") val family: String,
    @SerialName("reason") val reason: String,
    @SerialName("data") val data: T?
)
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
        else -> {
            println("BaseDTO: Unknown error")
            throw TaminErrorUriException(ErrorUri.fromString("UNKNOWN_ERROR: $reason"))
        }
    }
}
