package com.tamin.taminhamrah.di

import com.tamin.core.network.datasource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.utils.NetworkConstants
import com.tamin.taminhamrah.core.datastore.token.TokenStoreManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {

    // JSON Serializer
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
    }


    // Auth HTTP Client (no Auth plugin, used for token endpoints)
    single(named("authHttpClient")) {
        createAuthHttpClient(
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC
        )
    }

    // Main HTTP Client (60 seconds timeout)
    single(named("mainHttpClient")) {
        createHttpClient(
            tokenStoreManager = get<TokenStoreManager>(),
            authRemoteDataSource = get<AuthRemoteDataSource>(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC
        )
    }

    // Upload HTTP Client (5 minutes timeout)
    single(named("uploadHttpClient")) {
        createHttpClient(
            tokenStoreManager = get<TokenStoreManager>(),
            authRemoteDataSource = get<AuthRemoteDataSource>(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_5_MIN
        )
    }
}

private fun createHttpClient(
    tokenStoreManager: TokenStoreManager,
    authRemoteDataSource: AuthRemoteDataSource,
    json: Json,
    timeoutMillis: Long
): HttpClient {
    return HttpClient {
        expectSuccess = false

        install(ContentNegotiation) {
            json(json, contentType = ContentType.Any)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenStoreManager.getToken()
                    val refresh = tokenStoreManager.getRefreshToken().orEmpty()
                    token?.let { BearerTokens(accessToken = it, refreshToken = refresh) }
                }
                refreshTokens {
                    try {
                        val currentRefresh = tokenStoreManager.getRefreshToken()
                        if (currentRefresh.isNullOrBlank()) {
                            tokenStoreManager.setTokenValid(false)
                            null
                        } else {
                            val result = authRemoteDataSource.refreshTokens(
                                refreshToken = currentRefresh,
                                clientId = NetworkConstants.CLIENT_ID,
                            )
                            tokenStoreManager.saveToken(result.accessToken)
                            tokenStoreManager.saveRefreshToken(result.refreshToken)
                            tokenStoreManager.setTokenValid(true)
                            val access = tokenStoreManager.getToken() ?: return@refreshTokens null
                            val refresh = tokenStoreManager.getRefreshToken().orEmpty()
                            BearerTokens(accessToken = access, refreshToken = refresh)
                        }
                    } catch (_: Exception) {
                        tokenStoreManager.setTokenValid(false)
                        null
                    }
                }
            }
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    KtorSimpleLogger(message)
                }
            }
            level = LogLevel.ALL
        }

        defaultRequest {
            url(NetworkConstants.BASE_URL)
            header(HttpHeaders.Accept, "*/*")
        }
    }
}

private fun createAuthHttpClient(
    json: Json,
    timeoutMillis: Long
): HttpClient {
    return HttpClient {
        expectSuccess = false

        install(ContentNegotiation) {
            json(json, contentType = ContentType.Any)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    KtorSimpleLogger(message)
                }
            }
            level = LogLevel.ALL
        }

        defaultRequest {
            url(NetworkConstants.BASE_URL)
            header(HttpHeaders.Accept, "*/*")
        }
    }
}
