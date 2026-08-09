package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSourceImpl
import com.tamin.taminhamrah.repository.AuthRepository
import com.tamin.taminhamrah.repository.AuthTokenInvalidator
import com.tamin.taminhamrah.repository.authRepository.AuthRepositoryImpl
import com.tamin.taminhamrah.repository.authRepository.AuthTokenInvalidatorImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.util.AppConfig
import com.tamin.taminhamrah.util.NetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
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
    single<AuthTokenInvalidator> { AuthTokenInvalidatorImpl() }
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
            authTokenInvalidator = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC
        )
    }

    // Health HTTP Client (No Auth plugin, specific for health services)
    single(named("healthHttpClient")) {
        createHealthHttpClient(
            engine = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC
        )
    }

    // Upload HTTP Client (5 minutes timeout)
    single(named("uploadHttpClient")) {
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            authTokenInvalidator = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_5_MIN
        )
    }

    // AI HTTP Client
    single(named("aiHttpClient")) {
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            authTokenInvalidator = get(),
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
    authTokenInvalidator: AuthTokenInvalidator,
    json: Json,
    timeoutMillis: Long,
    baseUrl: String = NetworkConstants.BASE_URL
): HttpClient {
    val client = HttpClient(engine) {
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
                    // Ktor caches whatever this returns — null included — and never calls it
                    // again until clearToken(). A null here therefore strips the Authorization
                    // header from every later request on this client, which is why every token
                    // write goes through AuthTokenInvalidator.
                    if (token.isNullOrBlank()) {
                        KermitLogger.w(tag = "KtorAuth") { "loadTokens: no stored token; requests will be unauthenticated until invalidated." }
                        null
                    } else {
                        // refreshToken is deliberately blank: the real one lives in
                        // TokenStoreManager, and Ktor only needs a non-null value to arm refresh.
                        BearerTokens(accessToken = token, refreshToken = "")
                    }
                }
                refreshTokens {
                    val refreshed = authRepository.refreshToken()
                    val token = authRepository.getAccessToken()
                    when {
                        refreshed && !token.isNullOrBlank() ->
                            BearerTokens(accessToken = token, refreshToken = "")

                        // Refresh failed but a token is still stored. Returning null would set the
                        // holder to null and unauthenticate every subsequent request for the rest
                        // of the process; hand back what we have instead. Ktor retries the request
                        // once and then surfaces the 401, so this cannot loop.
                        !token.isNullOrBlank() -> {
                            KermitLogger.w(tag = "KtorAuth") { "refreshTokens: refresh failed, reusing stored token rather than clearing it." }
                            BearerTokens(accessToken = token, refreshToken = "")
                        }

                        else -> {
                            KermitLogger.e(tag = "KtorAuth") { "refreshTokens: refresh failed and no stored token; the user is signed out." }
                            null
                        }
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

    // The Auth plugin's bearer provider caches its BearerTokens after the first authenticated
    // request and won't call loadTokens() again on its own (see AuthTokenInvalidator KDoc), so
    // register a way for AuthRepository to force it to drop the cached token on logout/login.
    client.authProviders.filterIsInstance<BearerAuthProvider>().forEach { provider ->
        authTokenInvalidator.registerClearAction { provider.clearToken() }
    }

    return client
}

private fun createHealthHttpClient(
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
            logger = object : Logger {
                override fun log(message: String) {
                    KermitLogger.d(tag = "KtorHealthClient", messageString = message)
                }
            }
        }

        defaultRequest {
            url(NetworkConstants.BASE_URL_HEALTH_PROFILE)
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
