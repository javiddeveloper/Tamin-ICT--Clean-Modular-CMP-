package com.tamin.taminhamrah.tools.errorHandling

import io.ktor.client.call.save
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

/**
 * Intercepts non-success HTTP responses whose body is a bare localized string
 * (not JSON) and converts them to [TaminErrorUriException] before Ktorfit tries
 * to deserialize them as [com.tamin.taminhamrah.tools.BaseDTO].
 *
 * An empty 204 is a failure too: every endpoint here expects a body, so it could only fail
 * decoding; the old app gave it its own wording.
 */
val PlainTextErrorResponsePlugin = createClientPlugin("PlainTextErrorResponsePlugin") {
    on(Send) { request ->
        val call = proceed(request)
        if (call.response.status == HttpStatusCode.NoContent) {
            throw errorFromHttpBody(HttpStatusCode.NoContent.value, "")
        }
        if (call.response.status.isSuccess()) {
            return@on call
        }

        val savedCall = call.save()
        val bodyText = try {
            savedCall.response.bodyAsText()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            return@on savedCall
        }

        plainTextErrorFromHttpBody(savedCall.response.status.value, bodyText)?.let { throw it }

        savedCall
    }
}
