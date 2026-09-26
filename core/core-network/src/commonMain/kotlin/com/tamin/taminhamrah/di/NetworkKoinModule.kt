package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSourceImpl
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.repository.AuthRepository
import com.tamin.taminhamrah.repository.AuthTokenInvalidator
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.repository.authRepository.AuthRepositoryImpl
import com.tamin.taminhamrah.repository.authRepository.AuthTokenInvalidatorImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.tools.errorHandling.PlainTextErrorResponsePlugin
import com.tamin.taminhamrah.util.AppConfig
import com.tamin.taminhamrah.util.NetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRedirect
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
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.envelopeErrorText
import com.tamin.taminhamrah.tools.errorHandling.errorFromHttpBody
import com.tamin.taminhamrah.tools.errorHandling.isFailed
import com.tamin.taminhamrah.tools.errorHandling.taminEnvelopeOrNull
import io.ktor.serialization.ContentConverter
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readRemaining
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import co.touchlab.kermit.Logger as KermitLogger

/**
 * The one Json every API client is built on. Tests use this same instance: the API tests once ran
 * a laxer copy, so a reply production could not read passed them.
 */
internal val taminJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
}

/**
 * Sends requests with [json] exactly as given, and reads replies the way the old app's Gson did: a
 * field the server leaves out is null. Without that, every nullable DTO field lacking a `= null`
 * default is required, and one absent field turns a successful reply into an error screen
 * (EM-2716). Only reading is relaxed — `explicitNulls = false` would also drop nulls from request
 * bodies, and those stay byte-for-byte what they were.
 *
 * A failed reply is read the way the old app's `getErrorResult` read it, whatever type was asked
 * for: its message reaches [BaseDTO.errorText] even when the typed `data` cannot hold it (or
 * cannot be decoded at all — a validation array, a bare string), and a type that has no `status`
 * of its own gets the failure instead of an all-null "success".
 */
internal class LenientReplyConverter(json: Json) : ContentConverter by KotlinxSerializationConverter(json) {
    private val replyJson = Json(json) { explicitNulls = false }
    private val replies = KotlinxSerializationConverter(replyJson)

    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {
        val text = content.readRemaining().readText(charset)
        val failed = taminEnvelopeOrNull(text)?.takeIf { it.isFailed }
        if (failed?.status != null && !modelsEnvelope(typeInfo)) throw errorFromHttpBody(failed.status, text)

        val decoded = try {
            decode(charset, typeInfo, text)
        } catch (e: JsonConvertException) {
            // A failed reply's `data` is never read as the payload, so a `data` the type cannot hold
            // must not cost the reason: rebuild the envelope without it.
            if (failed?.status == null || typeInfo.type != BaseDTO::class) throw e
            BaseDTO<Any?>(
                status = failed.status,
                family = failed.family.orEmpty(),
                reason = failed.reason.orEmpty(),
                hasError = failed.hasError,
                problems = failed.problems,
            )
        }
        if (failed == null || decoded !is BaseDTO<*>) return decoded
        val (message, cause) = envelopeErrorText(text)
        @Suppress("UNCHECKED_CAST")
        return (decoded as BaseDTO<Any?>).copy(errorText = message, errorCauseText = cause)
    }

    private suspend fun decode(charset: Charset, typeInfo: TypeInfo, text: String): Any? =
        replies.deserialize(charset, typeInfo, ByteReadChannel(text.toByteArray(charset)))

    /** The envelope itself, or a type that declares the envelope's `status`. */
    private fun modelsEnvelope(typeInfo: TypeInfo): Boolean {
        if (typeInfo.type == BaseDTO::class) return true
        val type = typeInfo.kotlinType ?: return true
        val descriptor = runCatching { replyJson.serializersModule.serializer(type).descriptor }.getOrNull()
            ?: return true
        return (0 until descriptor.elementsCount).any { descriptor.getElementName(it) == "status" }
    }
}

val networkModule = module {

    // Error Parser
    singleOf(::ErrorParserImpl) bind ErrorParser::class

    // Data Sources
    single<AuthRemoteDataSource> {
        AuthRemoteDataSourceImpl(
            userApiService = get(named("authUserApiService")),
            errorParser = get(),
            developerOptionsRepository = get()
        )
    }

    single<UserRemoteDataSource> {
        UserRemoteDataSourceImpl(
            userApiService = get(),
//            httpClient = get(named("mainHttpClient")),
            errorParser = get(),
            queryBuilder = get(),
            json = get()
        )
    }

    // Repositories
    single<AuthTokenInvalidator> { AuthTokenInvalidatorImpl() }
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    // JSON Serializer
    single { taminJson }


    // Auth HTTP Client (no Auth plugin, used for token endpoints)
    single(named("authHttpClient")) {
        createAuthHttpClient(
            engine = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC,
            baseUrl = get<DeveloperOptionsRepository>().getEffectiveBaseUrl(BaseUrlKey.MAIN)
        )
    }

    // Main HTTP Client (60 seconds timeout)
    single(named("mainHttpClient")) {
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            authTokenInvalidator = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC,
            baseUrl = get<DeveloperOptionsRepository>().getEffectiveBaseUrl(BaseUrlKey.MAIN)
        )
    }

    // Health HTTP Client (No Auth plugin, specific for health services)
    single(named("healthHttpClient")) {
        createHealthHttpClient(
            engine = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC,
            baseUrl = get<DeveloperOptionsRepository>().getEffectiveBaseUrl(BaseUrlKey.HEALTH_PROFILE)
        )
    }

    // Upload HTTP Client (5 minutes timeout)
    single(named("uploadHttpClient")) {
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            authTokenInvalidator = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_5_MIN,
            baseUrl = get<DeveloperOptionsRepository>().getEffectiveBaseUrl(BaseUrlKey.MAIN)
        )
    }

    // Payment gateway HTTP Client (TFH — its own host, bearer-authenticated like the main API)
    single(named("tfhHttpClient")) {
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            authTokenInvalidator = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC,
            baseUrl = get<DeveloperOptionsRepository>().getEffectiveBaseUrl(BaseUrlKey.TFH)
        )
    }

    // AI HTTP Client
    single(named("aiHttpClient")) {
        val aiBaseUrl = get<DeveloperOptionsRepository>().getEffectiveBaseUrl(BaseUrlKey.AI)
        createHttpClient(
            engine = get(),
            authRepository = get<AuthRepository>(),
            authTokenInvalidator = get(),
            json = get<Json>(),
            timeoutMillis = NetworkConstants.REQUEST_TIMEOUT_60_SEC,
            baseUrl = aiBaseUrl
        ).config {
            install(com.tamin.taminhamrah.apiService.agent.AiChatTokenPlugin)
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
            register(ContentType.Any, LenientReplyConverter(json))
        }

        install(PlainTextErrorResponsePlugin)

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        // The backend's load balancer 302s a plain-http request to https on the same host (seen
        // on a developer-options base-URL override entered without a scheme); without this the
        // redirect comes back as an empty 302 body instead of being followed.
        install(HttpRedirect) {
            checkHttpMethod = false
            allowHttpsDowngrade = false
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
    timeoutMillis: Long,
    baseUrl: String
): HttpClient {
    return HttpClient(engine) {
        expectSuccess = false

        install(ContentNegotiation) {
            register(ContentType.Any, LenientReplyConverter(json))
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        install(HttpRedirect) {
            checkHttpMethod = false
            allowHttpsDowngrade = false
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
            url(baseUrl)
            header(HttpHeaders.Accept, "*/*")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }
}

private fun createAuthHttpClient(
    engine: HttpClientEngine,
    json: Json,
    timeoutMillis: Long,
    baseUrl: String
): HttpClient {
    return HttpClient(engine) {
        expectSuccess = false

        install(ContentNegotiation) {
            register(ContentType.Any, LenientReplyConverter(json))
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        install(HttpRedirect) {
            checkHttpMethod = false
            allowHttpsDowngrade = false
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
        }
    }
}
