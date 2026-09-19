package com.tamin.taminhamrah.apiService.agent

import kotlin.coroutines.cancellation.CancellationException
import com.tamin.taminhamrah.model.agent.ChatTokenExpiredException
import io.ktor.client.call.save
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath

/**
 * Detects a rejected chat token on the assistant's prompt endpoints and raises
 * [ChatTokenExpiredException] instead of handing back the response.
 *
 * It deliberately does not retry by itself: the prompt goes out as multipart form data, which
 * cannot be read back and rewritten here, so the retry happens one layer up in
 * `SendAgentPromptUseCase`, where the request is rebuilt with the new token. The native app's rule
 * is kept: HTTP 400 or a body carrying the expired-token marker counts as expired.
 */
val AiChatTokenPlugin = createClientPlugin("AiChatTokenPlugin") {
    on(Send) { request ->
        val call = proceed(request)
        val path = request.url.encodedPath
        if (PROMPT_PATHS.none { path.contains(it) }) return@on call

        val savedCall = call.save()
        val response = savedCall.response
        val body = if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.BadRequest) {
            try {
                response.bodyAsText()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }

        val isExpired = response.status == HttpStatusCode.BadRequest ||
            EXPIRED_TOKEN_MARKERS.any { body.contains(it) }
        if (isExpired) throw ChatTokenExpiredException()
        savedCall
    }
}

private val PROMPT_PATHS = listOf("search/service", "search/rule")

/** The native client matched the first; the second is what earlier KMP code expected. */
private val EXPIRED_TOKEN_MARKERS = listOf("INVALID_OR_EXPIRED_CHAT_TOKEN", "INVALID_OR_EXPIRED_TOKEN")
