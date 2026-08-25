package com.tamin.taminhamrah.tools.errorHandling

import io.ktor.client.call.save
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

/**
 * Intercepts non-success HTTP responses whose body is a bare localized string
 * (not JSON) and converts them to [TaminErrorUriException] before Ktorfit tries
 * to deserialize them as [com.tamin.taminhamrah.tools.BaseDTO].
 */
val PlainTextErrorResponsePlugin = createClientPlugin("PlainTextErrorResponsePlugin") {
    on(Send) { request ->
        val call = proceed(request)
        if (call.response.status.isSuccess()) {
            return@on call
        }

        val savedCall = call.save()
        val bodyText = try {
            savedCall.response.bodyAsText()
        } catch (_: Exception) {
            return@on savedCall
        }

        plainTextErrorFromHttpBody(savedCall.response.status.value, bodyText)?.let { throw it }

        savedCall
    }
}
