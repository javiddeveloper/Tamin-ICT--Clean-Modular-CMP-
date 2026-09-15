package com.tamin.taminhamrah.dataSource.agent

import com.tamin.taminhamrah.apiService.agent.AgentApiService
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.ChatTokenExpiredException
import com.tamin.taminhamrah.model.agent.CancelResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

/**
 * The prompt's `data` part. The server wants every key, as the native app's Gson (`serializeNulls`)
 * wrote them, so defaults and nulls are written too.
 */
internal fun AgentRequestDTO.toRequestBody(json: Json): String =
    Json(json) {
        encodeDefaults = true
        explicitNulls = true
    }.encodeToString(AgentRequestDTO.serializer(), this)

/** Writes a streamed body out in full, keeping its content type (and multipart boundary). */
internal suspend fun OutgoingContent.WriteChannelContent.toByteArrayContent(): ByteArrayContent = coroutineScope {
    val channel = ByteChannel()
    launch {
        writeTo(channel)
        channel.flushAndClose()
    }
    ByteArrayContent(channel.readRemaining().readByteArray(), contentType)
}

private const val VOICE_CONTENT_TYPE = "audio/wav"
private const val DEFAULT_VOICE_FILE_NAME = "voice.wav"

internal class AgentRemoteDataSourceImpl(
    private val agentApiService: AgentApiService,
    private val errorParser: ErrorParser,
    private val json: Json
) : AgentRemoteDataSource {

    override suspend fun checkChatAllowed(): ChatAllowedDTO {
        return try {
            agentApiService.checkChatAllowed()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    /**
     * The prompt as one in-memory multipart body. Ktor would otherwise stream the form as a
     * one-shot body, which the debug HTTP inspector (Chucker) cannot read and shows as empty.
     * The parts are a short JSON and at most one short voice clip, so buffering costs nothing.
     */
    private suspend fun createMultipartRequest(
        request: AgentRequestDTO,
        voiceBytes: ByteArray? = null,
        voiceFileName: String? = null
    ): ByteArrayContent {
        // The native app's order and file part: the recording first, as `file` of type audio/wav,
        // then `data`. The server's firewall rejects any other audio type with a 403 HTML page.
        val form = MultiPartFormDataContent(
            formData {
                if (voiceBytes != null && voiceBytes.isNotEmpty()) {
                    val name = voiceFileName ?: DEFAULT_VOICE_FILE_NAME
                    append("file", voiceBytes, Headers.build {
                        append(HttpHeaders.ContentType, VOICE_CONTENT_TYPE)
                        append(HttpHeaders.ContentDisposition, "filename=\"$name\"")
                    })
                }
                append("data", request.toRequestBody(json), Headers.build {
                    append(HttpHeaders.ContentType, "application/json; charset=UTF-8")
                })
            }
        )
        return form.toByteArrayContent()
    }

    override suspend fun sendServicePrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO {
        return try {
            agentApiService.sendServicePrompt(createMultipartRequest(request, voiceBytes, voiceFileName))
        } catch (e: ChatTokenExpiredException) {
            throw e
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendLawPrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO {
        return try {
            agentApiService.sendLawPrompt(createMultipartRequest(request, voiceBytes, voiceFileName))
        } catch (e: ChatTokenExpiredException) {
            throw e
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun trackRequest(requestId: String): PollingResponseDTO {
        return try {
            agentApiService.trackRequest(requestId)
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun cancelRequest(requestId: String): CancelResponseDTO {
        return try {
            agentApiService.cancelRequest(requestId)
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
