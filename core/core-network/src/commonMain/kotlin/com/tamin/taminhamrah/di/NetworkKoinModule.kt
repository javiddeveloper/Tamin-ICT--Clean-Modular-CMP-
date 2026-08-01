package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSourceImpl
import com.tamin.taminhamrah.repository.AuthRepository
import com.tamin.taminhamrah.repository.authRepository.AuthRepositoryImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.util.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import co.touchlab.kermit.Logger as KermitLogger

val networkModule = module {

    // Error Parser
    singleOf(::ErrorParserImpl) bind ErrorParser::class

    // Data Sources
    single<AuthRemoteDataSource> {
        AuthRemoteDataSourceImpl(
            userApiService = get(named("authUserApiService")),
            errorParser = get()
        )
    }

    single<UserRemoteDataSource> {
        UserRemoteDataSourceImpl(
            userApiService = get(),
//            httpClient = get(named("mainHttpClient")),
            errorParser = get(),
            queryBuilder = get()
        )
    }

    // Repositories
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

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
            engine = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC
        )
    }

    // Main HTTP Client (60 seconds timeout)
    single(named("mainHttpClient")) {
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC
        )
    }

    // Upload HTTP Client (5 minutes timeout)
    single(named("uploadHttpClient")) {
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_5_MIN
        )
    }

    // AI HTTP Client
    single(named("aiHttpClient")) {
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC,
            baseUrl = NetworkConstants.AI_BASE_URL
        ).config {
            install(com.tamin.taminhamrah.apiService.agent.AiChatTokenPlugin) {
                this.json = get<Json>()
                this.aiBaseUrl = NetworkConstants.AI_BASE_URL
            }
        }
    }
}

private fun createHttpClient(
    engine: HttpClientEngine,
    authRepository: AuthRepository,
    json: Json,
    timeoutMillis: Long,
    baseUrl: String = NetworkConstants.BASE_URL
): HttpClient {
    return HttpClient(engine) {
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
                sendWithoutRequest { true }
                loadTokens {
                    val token = authRepository.getAccessToken()
                    // Since AuthRepository handles persistence, we don't need to manually read from DataStore here
                    // However, BearerTokens needs a non-null refreshToken (even if it's empty) for Ktor to trigger refreshTokens block
                    token?.let {
                        BearerTokens(
                            accessToken = it,
                            refreshToken = "" // We'll handle the actual refresh token inside AuthRepository
                        )
                    }
                }
                refreshTokens {
                    val success = authRepository.refreshToken()
                    if (success) {
                        val newAccessToken = authRepository.getAccessToken()
                        if (newAccessToken != null) {
                            BearerTokens(
                                accessToken = newAccessToken,
                                refreshToken = ""
                            )
                        } else null
                    } else {
                        null
                    }
                }
            }
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = if (AppConfig.isDebug) LogLevel.ALL else LogLevel.NONE
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
            logger = object : Logger {
                override fun log(message: String) {
                    KermitLogger.d(tag = "KtorClient", messageString = message)
                }
            }
        }

        defaultRequest {
            url(baseUrl)
            header(HttpHeaders.Accept, "*/*")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }
}

private fun createAuthHttpClient(
    engine: HttpClientEngine,
    json: Json,
    timeoutMillis: Long
): HttpClient {
    return HttpClient(engine) {
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
            logger = Logger.DEFAULT
            level = if (AppConfig.isDebug) LogLevel.ALL else LogLevel.NONE
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
            logger = object : Logger {
                override fun log(message: String) {
                    KermitLogger.d(tag = "KtorClient", messageString = message)
                }
            }
        }

        defaultRequest {
            url(NetworkConstants.BASE_URL)
            header(HttpHeaders.Accept, "*/*")
        }
    }
}
