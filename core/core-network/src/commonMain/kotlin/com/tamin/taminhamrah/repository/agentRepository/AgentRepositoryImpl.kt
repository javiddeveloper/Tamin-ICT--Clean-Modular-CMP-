package com.tamin.taminhamrah.repository.agentRepository

import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentDataItemDTO
import com.tamin.taminhamrah.model.agent.AgentItemType
import com.tamin.taminhamrah.model.agent.AgentRenderMode
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.AgentPersonalInfoDN
import com.tamin.taminhamrah.model.agent.AgentPersonalInfoDTO
import com.tamin.taminhamrah.model.agent.AgentPromptTypeDTO
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.AgentResponseDTO
import com.tamin.taminhamrah.model.agent.AgentResponseDN
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.agent.ChatTokenExpiredException
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

/**
 * Implementation of [AgentRepository] with Kotlin Flow-based polling mechanism.
 *
 * Key improvements over the legacy Android version:
 * 1. Uses `flow {}` with `emit` instead of a `while` loop → lifecycle-safe.
 * 2. `MAX_POLLS` limits the maximum number of attempts → avoids infinite loops.
 * 3. Uses server-provided ETA, falling back to [DEFAULT_POLL_DELAY_MS].
 * 4. Errors are cleanly managed with `.catch {}`.
 */
class AgentRepositoryImpl(
    private val remoteDataSource: AgentRemoteDataSource
) : AgentRepository {

    companion object {
        private val markdownJson = Json { ignoreUnknownKeys = true }
        private const val MAX_POLLS = 5
        private const val MAX_POLL_DELAY_MS = 30_000L
    }

    override fun sendPrompt(request: AgentRequest): Flow<AgentPollingState> = flow {
        // 1. Initial request submission
        val initialResponse = if (request.isLawPrompt) {
            remoteDataSource.sendLawPrompt(request.toDTO(), request.voiceBytes, request.voiceFileName)
        } else {
            remoteDataSource.sendServicePrompt(request.toDTO(), request.voiceBytes, request.voiceFileName)
        }

        val requestId = initialResponse.data?.id
            ?: throw IllegalStateException("Request ID not received")

        val etaSeconds = initialResponse.data?.eta ?: 5

        // 3. Start polling loop
        var pollCount = 1
        var currentEta = etaSeconds

        while (pollCount <= MAX_POLLS) {
            // Emit Pending state with current attempt
            emit(AgentPollingState.Pending(
                requestId = requestId,
                etaSeconds = currentEta,
                attempt = pollCount,
                maxAttempts = MAX_POLLS
            ))

            // Smart delay: Use server ETA but respect the maximum threshold
            val delayMs = (currentEta * 1000L).coerceIn(1_000L, MAX_POLL_DELAY_MS)
            delay(delayMs)

            val trackResponse = remoteDataSource.trackRequest(requestId)
            val trackData = trackResponse.data

            when (trackData?.status?.uppercase()) {
                "DONE" -> {
                    val result = trackData.result
                        ?: throw IllegalStateException("Final response is empty")
                    emit(AgentPollingState.Done(result.toDomain()))
                    return@flow
                }
                "FAILED" -> {
                    emit(AgentPollingState.Failed(trackData.message?.takeIf { it.isNotBlank() }))
                    return@flow
                }
                "CANCEL" -> {
                    emit(AgentPollingState.Cancelled)
                    return@flow
                }
                else -> {
                    // Still processing
                    currentEta = trackData?.eta ?: 5
                }
            }
            pollCount++
        }

        // 4. Timeout after MAX_POLLS
        emit(AgentPollingState.Failed())
    }.catch { e ->
        // An expired chat token is not a failure yet: SendAgentPromptUseCase refreshes and resends.
        if (e is ChatTokenExpiredException) throw e
        emit(AgentPollingState.Failed())
    }

    override suspend fun cancelRequest(requestId: String): Result<Unit> {
        return try {
            remoteDataSource.cancelRequest(requestId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkChatAllowed(): Result<ChatAllowedDN> {
        return try {
            val response = remoteDataSource.checkChatAllowed()
            val data = response.data
            Result.success(
                ChatAllowedDN(
                    canStartChat = data?.canStartChat ?: false,
                    chatToken = data?.chatToken,
                    errorMessage = data?.errorMessage,
                    canSendVoice = data?.canSendVoice ?: false,
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─── Extension Mappers (private) ──────────────────────────────────────

    private fun AgentRequest.toDTO() = AgentRequestDTO(
        prompt = prompt,
        sessionId = sessionId,
        lastEntity = lastEntity.orEmpty(),
        chatToken = chatToken,
        userType = userType,
        personalInfo = personalInfo?.toDTO(),
        promptType = if (isVoice) AgentPromptTypeDTO.VOICE else AgentPromptTypeDTO.TEXT,
        state = state.parseJsonOrNull(),
        history = history.parseJsonOrNull() ?: JsonArray(emptyList()),
    )

    private fun AgentPersonalInfoDN.toDTO() = AgentPersonalInfoDTO(
        nationalId = nationalId,
        pensionerId = pensionerId,
        firstName = firstName,
        lastName = lastName,
    )

    /** Stored conversation JSON back into a tree; anything unreadable is sent as absent. */
    private fun String?.parseJsonOrNull(): JsonElement? =
        this?.let { runCatching { markdownJson.parseToJsonElement(it) }.getOrNull() }
            ?.takeUnless { it is JsonNull }

    private fun JsonElement?.toJsonStringOrNull(): String? =
        this?.takeUnless { it is JsonNull }?.toString()

    private fun AgentResponseDTO.toDomain(): AgentResponseDN {
        return AgentResponseDN(
            sessionId = sessionId,
            lastEntity = lastEntity,
            entities = entities?.mapIndexed { index, entity ->
                val itemType = AgentItemType.fromWireName(entity.itemType)
                AiEntityDN(
                    action = AgentActionKey.fromString(entity.key),
                    stepNumber = entity.stepNumber ?: index,
                    payload = entity.payload,
                    data = entity.data,
                    message = entity.message,
                    itemType = itemType,
                    markdown = if (itemType == AgentItemType.MARKDOWN) entity.data.markdownTexts() else emptyList()
                )
            }?.sortedBy { it.stepNumber } ?: emptyList(),
            message = message,
            renderMode = AgentRenderMode.fromWireName(renderMode),
            state = state.toJsonStringOrNull(),
            history = history.toJsonStringOrNull(),
        )
    }

    /** The non-blank `text` of every `markdown_item` in [this]; anything malformed is skipped. */
    private fun JsonElement?.markdownTexts(): List<String> {
        val items = this as? JsonArray ?: return emptyList()
        return items.mapNotNull { element ->
            val item = runCatching { markdownJson.decodeFromJsonElement(AgentDataItemDTO.serializer(), element) }
                .getOrNull()
                ?: return@mapNotNull null
            item.text?.trim()?.takeIf {
                it.isNotEmpty() && AgentItemType.fromWireName(item.itemType) == AgentItemType.MARKDOWN_ITEM
            }
        }
    }
}
