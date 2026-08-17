 package com.tamin.taminhamrah.apiService.agent

import com.tamin.taminhamrah.model.agent.AgentResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.http.encodedPath
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import co.touchlab.kermit.Logger
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.client.call.save

val AiChatTokenPlugin = createClientPlugin("AiChatTokenPlugin", createConfiguration = ::AiChatTokenConfig) {
    val json = pluginConfig.json
    val aiBaseUrl = pluginConfig.aiBaseUrl

    on(Send) { request ->
        val originalCall = proceed(request)

        val path = request.url.encodedPath
        if (path.contains("chat-allowed") || (!path.contains("search/service") && !path.contains("search/rule"))) {
            return@on originalCall
        }

        // Save the call so its body can be read multiple times (replaces the need for DoubleReceive)
        val savedCall = originalCall.save()
        val response = savedCall.response

        val bodyText = try {
            response.bodyAsText()
        } catch (e: Exception) {
            ""
        }

        val isTokenExpired = response.status.value == 400 || bodyText.contains("INVALID_OR_EXPIRED_TOKEN")

        if (isTokenExpired) {
            Logger.withTag("AiChatTokenPlugin").d { "Token expired. Fetching new token..." }

            var newToken: String? = null
            try {
                // Fetch new token
                // We use the same client to hit checkChatAllowed
                // But we must construct a raw request to avoid infinite loops, though the path check above prevents it.
                val authHeader = request.headers[HttpHeaders.Authorization]
                val checkAllowedResponse = client.request {
                    url("${aiBaseUrl.trimEnd('/')}/chat-allowed")
                    method = HttpMethod.Get
                    if (authHeader != null) {
                        header(HttpHeaders.Authorization, authHeader)
                    }
                }

                if (checkAllowedResponse.status.value == 200) {
                    val allowedDto = checkAllowedResponse.body<ChatAllowedDTO>()
                    newToken = allowedDto.data?.chatToken
                }
            } catch (e: Exception) {
                Logger.withTag("AiChatTokenPlugin").e(e) { "Failed to fetch new token" }
            }

            if (newToken != null) {
                Logger.withTag("AiChatTokenPlugin").d { "Got new token, retrying request..." }

                // Rebuild the request body with the new token
                val oldBody = request.body as? TextContent
                val newBodyContent = if (oldBody != null) {
                    try {
                        val element = json.parseToJsonElement(oldBody.text).jsonObject
                        val newElement = JsonObject(element.toMutableMap().apply {
                            put("chatToken", kotlinx.serialization.json.JsonPrimitive(newToken))
                        })
                        TextContent(
                            text = newElement.toString(),
                            contentType = ContentType.Application.Json
                        )
                    } catch (e: Exception) {
                        oldBody
                    }
                } else {
                    request.body
                }

                val retryRequest = io.ktor.client.request.HttpRequestBuilder().apply { takeFrom(request) }
                retryRequest.setBody(newBodyContent)

                return@on proceed(retryRequest)
            } else {
                // If we couldn't get a new token, throw an exception so the flow handles it
                throw RuntimeException("خطای سرور")
            }
        }

        savedCall
    }
}

class AiChatTokenConfig {
    var json: Json = Json { ignoreUnknownKeys = true }
    var aiBaseUrl: String = ""
}
