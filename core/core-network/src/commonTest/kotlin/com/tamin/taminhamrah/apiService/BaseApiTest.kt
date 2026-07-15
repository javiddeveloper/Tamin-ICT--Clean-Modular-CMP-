package com.tamin.taminhamrah.apiService

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.DefaultRequest
import io.ktor.http.contentType
import kotlinx.serialization.json.Json


import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header

import io.ktor.client.plugins.defaultRequest
import io.ktor.http.contentType

abstract class BaseApiTest {

    protected fun createMockKtorfit(
        content: String,
        status: HttpStatusCode = HttpStatusCode.OK,
        contentType: ContentType = ContentType.Application.Json
    ): Ktorfit {
        val mockEngine = MockEngine { _ ->
            respond(
                content = content,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, contentType.toString())
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                })
            }
            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(DefaultRequest) {
                headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
        }

        return Ktorfit.Builder()
            .httpClient(httpClient)
            .baseUrl("https://api.tamin.ir/")
            .build()
    }
}
