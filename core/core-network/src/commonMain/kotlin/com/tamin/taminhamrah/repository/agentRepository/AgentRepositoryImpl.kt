package com.tamin.taminhamrah.repository.agentRepository

import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentDomainMapper
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
 * پیاده‌سازی [AgentRepository] با مکانیسم polling مبتنی بر Kotlin Flow
 *
 * بهبودهای اصلی نسبت به نسخه Android قدیمی:
 * 1. به جای `while` loop، از `flow {}` با `emit` استفاده می‌شود → lifecycle-safe
 * 2. `MAX_POLLS` حداکثر تعداد تلاش را محدود می‌کند → بدون infinite loop
 * 3. اگر ETA از سرور نیامد، از [DEFAULT_POLL_DELAY_MS] استفاده می‌شود
 * 4. خطاها با `.catch {}` به صورت تمیز مدیریت می‌شوند
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
        // ۱. ارسال اولیه درخواست
        val initialResponse = if (request.isLawPrompt) {
            remoteDataSource.sendLawPrompt(request.toDTO())
        } else {
            remoteDataSource.sendServicePrompt(request.toDTO())
        }

        val requestId = initialResponse.data?.id
            ?: throw IllegalStateException("شناسه درخواست دریافت نشد")

        val etaSeconds = initialResponse.data?.eta ?: 5

        // ۲. emit وضعیت Pending
        emit(AgentPollingState.Pending(requestId = requestId, etaSeconds = etaSeconds))

        // ۳. شروع polling loop
        var pollCount = 0
        var currentEta = etaSeconds

        while (pollCount < MAX_POLLS) {
            // تاخیر هوشمند: از ETA سرور استفاده می‌کنیم اما max را رعایت می‌کنیم
            val delayMs = (currentEta * 1000L).coerceIn(1_000L, MAX_POLL_DELAY_MS)
            delay(delayMs)

            val trackResponse = remoteDataSource.trackRequest(requestId)
            val trackData = trackResponse.data

            when (trackData?.status?.uppercase()) {
                "DONE" -> {
                    val result = trackData.result
                        ?: throw IllegalStateException("پاسخ نهایی خالی است")
                    emit(AgentPollingState.Done(result.toDomain()))
                    return@flow
                }
                "FAILED" -> {
                    emit(AgentPollingState.Failed(
                        trackResponse.data?.result?.message ?: "پردازش با خطا مواجه شد"
                    ))
                    return@flow
                }
                "CANCEL" -> {
                    emit(AgentPollingState.Cancelled)
                    return@flow
                }
                // PENDING — ادامه polling
                else -> {
                    currentEta = trackData?.eta ?: currentEta
                }
            }

            pollCount++
        }

        emit(AgentPollingState.Failed("زمان انتظار به پایان رسید. لطفاً دوباره تلاش کنید."))

    }.catch { e ->
        emit(AgentPollingState.Failed(e.message ?: "خطای غیرمنتظره"))
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
