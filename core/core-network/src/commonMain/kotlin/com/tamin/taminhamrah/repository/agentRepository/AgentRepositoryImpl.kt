package com.tamin.taminhamrah.repository.agentRepository

import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.AgentResponseDTO
import com.tamin.taminhamrah.model.agent.AgentResponseDN
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

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
        private const val MAX_POLLS = 30
        private const val DEFAULT_POLL_DELAY_MS = 5_000L
        private const val MAX_POLL_DELAY_MS = 30_000L
    }

    override fun sendPrompt(request: AgentRequest): Flow<AgentPollingState> = flow {
        // 1. Initial request submission
        val initialResponse = if (request.isLawPrompt) {
            remoteDataSource.sendLawPrompt(request.toDTO())
        } else {
            remoteDataSource.sendServicePrompt(request.toDTO())
        }

        val requestId = initialResponse.data?.id
            ?: throw IllegalStateException("Request ID not received")

        val etaSeconds = initialResponse.data?.eta ?: 5

        // 2. Emit Pending state
        emit(AgentPollingState.Pending(requestId = requestId, etaSeconds = etaSeconds))

        // 3. Start polling loop
        var pollCount = 0
        var currentEta = etaSeconds

        while (pollCount < MAX_POLLS) {
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
                    emit(AgentPollingState.Failed(
                        trackData.result?.message ?: trackData.message ?: "Processing failed"
                    ))
                    return@flow
                }
                "CANCEL" -> {
                    emit(AgentPollingState.Cancelled)
                    return@flow
                }
                // PENDING — continue polling
                else -> {
                    currentEta = trackData?.eta ?: currentEta
                }
            }

            pollCount++
        }

        emit(AgentPollingState.Failed("Request timed out. Please try again."))

    }.catch { e ->
        emit(AgentPollingState.Failed(e.message ?: "Unexpected error"))
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
                    errorMessage = data?.errorMessage
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
        lastEntity = lastEntity,
        chatToken = chatToken
    )

    private fun AgentResponseDTO.toDomain(): AgentResponseDN {
        return AgentResponseDN(
            sessionId = sessionId,
            lastEntity = lastEntity,
            entities = entities?.mapIndexed { index, entity ->
                AiEntityDN(
                    action = AgentActionKey.fromString(entity.key),
                    stepNumber = entity.stepNumber ?: index,
                    payload = entity.payload,
                    data = entity.data,
                    message = entity.message,
                    itemType = entity.itemType
                )
            }?.sortedBy { it.stepNumber } ?: emptyList(),
            message = message
        )
    }
}
