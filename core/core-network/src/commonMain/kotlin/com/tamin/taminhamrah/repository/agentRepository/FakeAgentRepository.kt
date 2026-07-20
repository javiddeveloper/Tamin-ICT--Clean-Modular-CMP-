package com.tamin.taminhamrah.repository.agentRepository

import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.AgentResponseDN
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject

/**
 * Fake implementation of [AgentRepository] for development/testing purposes.
 *
 * Simulates the polling flow with a fixed 5-attempt sequence and returns a
 * hardcoded response that mimics a real "dastmozd_infos" service call.
 */
class FakeAgentRepository : AgentRepository {

    companion object {
        private const val MAX_POLLS = 5
        private const val POLL_DELAY_MS = 1_000L
        private const val FAKE_REQUEST_ID = "fake-req-001"
    }

    override fun sendPrompt(request: AgentRequest): Flow<AgentPollingState> = flow {
        // Simulate polling
        for (attempt in 1..MAX_POLLS) {
            emit(
                AgentPollingState.Pending(
                    requestId = FAKE_REQUEST_ID,
                    etaSeconds = 1,
                    attempt = attempt,
                    maxAttempts = MAX_POLLS
                )
            )
            delay(POLL_DELAY_MS)
        }

        // Emit fake Done response
        emit(AgentPollingState.Done(buildFakeResponse(request.prompt)))
    }

    override suspend fun cancelRequest(requestId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun checkChatAllowed(): Result<ChatAllowedDN> {
        return Result.success(
            ChatAllowedDN(
                canStartChat = true,
                chatToken = "fake-chat-token-12345",
                errorMessage = null
            )
        )
    }

    // ─── Fake Data Builder ────────────────────────────────────────────────────

    private fun buildFakeResponse(prompt: String): AgentResponseDN {
        val lowerPrompt = prompt.lowercase()

        val entities = when {
            "سابقه" in prompt || "history" in lowerPrompt -> buildWageHistoryEntities()
            "دستمزد" in prompt || "دستمزد" in prompt     -> buildWageHistoryEntities()
            else                                           -> buildGeneralMessageEntities(prompt)
        }

        return AgentResponseDN(
            sessionId = "fake-session-001",
            lastEntity = entities.lastOrNull()?.action?.key,
            entities = entities,
            message = null
        )
    }

    private fun buildWageHistoryEntities(): List<AiEntityDN> {
        val fakeJsonData = kotlinx.serialization.json.Json.parseToJsonElement(
            """
            {
              "title": "سابقه بیمه",
              "items": [
                {"label": "نام کارگاه", "value": "شرکت نمونه ایران"},
                {"label": "تاریخ شروع", "value": "1399/01/01"},
                {"label": "تاریخ پایان", "value": "1402/06/31"},
                {"label": "تعداد روز بیمه", "value": "1265 روز"},
                {"label": "نوع کار", "value": "تمام وقت"}
              ]
            }
            """.trimIndent()
        )

        return listOf(
            AiEntityDN(
                action = AgentActionKey.MESSAGE,
                stepNumber = 0,
                payload = null,
                data = null,
                message = "سابقه بیمه‌ای شما با موفقیت دریافت شد:",
                itemType = "message"
            ),
            AiEntityDN(
                action = AgentActionKey.HISTORY_JOB_INFOS_LAST,
                stepNumber = 1,
                payload = null,
                data = fakeJsonData,
                message = null,
                itemType = "key_value"
            )
        )
    }

    private fun buildGeneralMessageEntities(prompt: String): List<AiEntityDN> {
        return listOf(
            AiEntityDN(
                action = AgentActionKey.GENERAL_RESPONSE,
                stepNumber = 0,
                payload = null,
                data = null,
                message = "پرسش شما دریافت شد: «$prompt»\n\nاین یک پاسخ آزمایشی است. برای اتصال به سرور واقعی، ریپازیتوری را به AgentRepositoryImpl تغییر دهید.",
                itemType = "message"
            )
        )
    }
}
