package com.tamin.taminhamrah.dataSource.agent

import com.tamin.taminhamrah.model.agent.AgentEntityDTO
import com.tamin.taminhamrah.model.agent.AgentMockMode
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.AgentResponseDTO
import com.tamin.taminhamrah.model.agent.CancelDataDTO
import com.tamin.taminhamrah.model.agent.CancelResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDataDTO
import com.tamin.taminhamrah.model.agent.ChatTokenExpiredException
import com.tamin.taminhamrah.model.agent.PollingDataDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.put

/**
 * What the fake answers a prompt with. The first scenario whose hint appears in the prompt
 * (case-insensitive) wins, so the more specific spellings are listed before the ones they contain;
 * a prompt with no hint gets [ALL].
 */
internal enum class AgentMockScenario(vararg val hints: String) {
    /** A markdown bubble listing every scenario and its hint. */
    HELP("help", "راهنما"),

    /** `FAILED` with no server message — the client's generic failure text. */
    FAILED_SILENT("failed-silent", "خطای بی‌پیام", "خطای بی پیام"),

    /** `FAILED` carrying the server's own explanation. */
    FAILED("failed", "خطای سرور", "خطا"),

    /** `PENDING` on every poll until the client gives up. */
    TIMEOUT("timeout", "تایم‌اوت", "تایم اوت", "تایم"),

    /** `PENDING` twice, then a short answer — shows the attempt counter moving. */
    SLOW("slow", "آهسته"),

    /** `CANCEL` from the server side. */
    CANCEL("cancel", "لغو"),

    /** The track call itself fails (connection lost mid-request). */
    NETWORK("network", "قطع"),

    /** The first send rejects the chat token; the resend with a fresh token succeeds. */
    TOKEN_EXPIRED("token", "توکن"),

    /** `DONE` with no entities at all. */
    EMPTY("empty", "خالی"),

    /** Only the server-rendered markdown fixture. */
    MARKDOWN("markdown", "مارک‌داون", "مارکداون", "مارک داون"),

    /** Only the one-of-every-payload-type fixture. */
    SHOWCASE("showcase", "ویترین"),

    /** Only the edge-case fixture. */
    EDGE_CASES("edge", "حالت لبه"),

    /** Every client-service key; these call the app's own use cases, so a login is needed. */
    SERVICES("services", "سرویس‌ها"),

    /** Markdown + showcase + edge cases in one answer: every bubble the app can draw. */
    ALL,

    /** A voice prompt with no text: acknowledged with the file name and size, nothing else. */
    VOICE;

    companion object {
        fun of(prompt: String): AgentMockScenario =
            entries.firstOrNull { scenario -> scenario.hints.any { prompt.contains(it, ignoreCase = true) } } ?: ALL
    }
}

/**
 * Answers the assistant's API from local fixtures so every shape of answer — and every way a
 * request can end — can be looked at without a backend. Selected by
 * [com.tamin.taminhamrah.model.agent.AgentMockMode] in Developer Options through
 * [AgentRemoteDataSourceSelector]; [mode] is read per call so a switch takes effect at once.
 *
 * - `chat-allowed` follows the mode: allowed (with or without voice), refused, or unreachable.
 * - A prompt picks an [AgentMockScenario] by keyword; anything else is answered with every
 *   bubble type at once. Send "راهنما" for the list.
 * - A voice prompt is acknowledged with the file's name and size, since there is no transcription.
 */
internal class AgentRemoteDataSourceFakeImpl(
    private val json: Json,
    private val mode: () -> AgentMockMode = { AgentMockMode.RESPONSES },
) : AgentRemoteDataSource {

    private var scenario: AgentMockScenario = AgentMockScenario.ALL
    private var voiceNote: String? = null
    private var pollsSoFar = 0

    /** Set while a [AgentMockScenario.TOKEN_EXPIRED] prompt waits for its resend. */
    private var tokenRejected = false
    private var tokenGeneration = 0

    override suspend fun checkChatAllowed(): ChatAllowedDTO {
        delay(400)
        val data = when (mode()) {
            AgentMockMode.OFFLINE -> throw IllegalStateException("mock assistant: chat-allowed unreachable")
            AgentMockMode.ACCESS_DENIED -> ChatAllowedDataDTO(
                canStartChat = false,
                chatToken = null,
                errorMessage = "دستیار هوشمند برای حساب شما فعال نیست. (پاسخ ساختگی حالت «عدم دسترسی»)",
                canSendVoice = false,
            )
            AgentMockMode.NO_VOICE -> allowed(canSendVoice = false)
            AgentMockMode.RESPONSES, AgentMockMode.DISABLED -> allowed(canSendVoice = true)
        }
        return ChatAllowedDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = data)
    }

    private fun allowed(canSendVoice: Boolean) = ChatAllowedDataDTO(
        canStartChat = true,
        chatToken = "fake-token-${++tokenGeneration}",
        errorMessage = null,
        canSendVoice = canSendVoice,
        ttl = 3600,
    )

    override suspend fun sendServicePrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO {
        delay(500)
        voiceNote = voiceBytes?.let { "**${voiceFileName ?: "voice.wav"}** — ${it.size} بایت" }
        scenario = if (voiceNote != null && request.prompt.isBlank()) AgentMockScenario.VOICE else AgentMockScenario.of(request.prompt)
        pollsSoFar = 0

        if (scenario == AgentMockScenario.TOKEN_EXPIRED && !tokenRejected) {
            tokenRejected = true
            throw ChatTokenExpiredException()
        }
        tokenRejected = false

        return polling(
            PollingDataDTO(id = "fake-request-${scenario.name.lowercase()}", eta = 1, status = "PENDING")
        )
    }

    override suspend fun sendLawPrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO = sendServicePrompt(request, voiceBytes, voiceFileName)

    override suspend fun trackRequest(requestId: String): PollingResponseDTO {
        delay(600)
        pollsSoFar++
        val data = when (scenario) {
            AgentMockScenario.TIMEOUT -> pending(requestId)
            AgentMockScenario.SLOW ->
                if (pollsSoFar < 3) pending(requestId)
                else done(requestId, markdownEntity("### پاسخ دیرهنگام\n\nاین جواب بعد از **${pollsSoFar}** بار پیگیری رسید."))
            AgentMockScenario.CANCEL -> PollingDataDTO(id = requestId, eta = 1, status = "CANCEL")
            AgentMockScenario.FAILED -> PollingDataDTO(
                id = requestId, eta = 1, status = "FAILED",
                message = "پردازش درخواست با خطا مواجه شد. (پیام ساختگی سرور)", errorStatus = "MOCK_FAILURE",
            )
            AgentMockScenario.FAILED_SILENT -> PollingDataDTO(id = requestId, eta = 1, status = "FAILED")
            AgentMockScenario.NETWORK -> throw IllegalStateException("mock assistant: connection lost while tracking")
            AgentMockScenario.EMPTY -> done(requestId)
            AgentMockScenario.VOICE -> done(requestId)
            AgentMockScenario.HELP -> done(requestId, markdownEntity(HELP_MARKDOWN))
            AgentMockScenario.TOKEN_EXPIRED -> done(
                requestId,
                markdownEntity("### توکن تازه شد\n\nارسال اول با توکن منقضی رد شد؛ مجوز دوباره گرفته شد و همین پرامپت با **fake-token-$tokenGeneration** دوباره فرستاده شد."),
            )
            AgentMockScenario.MARKDOWN -> done(requestId, *entitiesOf(FAKE_AGENT_MARKDOWN_RESPONSE))
            AgentMockScenario.SHOWCASE -> done(requestId, *entitiesOf(FAKE_AGENT_SHOWCASE_RESPONSE))
            AgentMockScenario.EDGE_CASES -> done(requestId, *entitiesOf(FAKE_AGENT_EDGE_CASES_RESPONSE))
            AgentMockScenario.SERVICES -> done(requestId, *entitiesOf(FAKE_AGENT_ONE_RESPONSE))
            AgentMockScenario.ALL -> done(
                requestId,
                *entitiesOf(FAKE_AGENT_MARKDOWN_RESPONSE),
                *entitiesOf(FAKE_AGENT_SHOWCASE_RESPONSE),
                *entitiesOf(FAKE_AGENT_EDGE_CASES_RESPONSE),
            )
        }
        return polling(data)
    }

    override suspend fun cancelRequest(requestId: String): CancelResponseDTO {
        delay(300)
        return CancelResponseDTO(
            status = 200, family = "SUCCESSFUL", reason = "OK",
            data = CancelDataDTO(success = true, message = "Cancelled"),
        )
    }

    // ─── Building answers ─────────────────────────────────────────────────────

    private fun polling(data: PollingDataDTO) =
        PollingResponseDTO(status = 200, family = "SUCCESSFUL", reason = "OK", data = data)

    private fun pending(requestId: String) = PollingDataDTO(id = requestId, eta = 1, status = "PENDING")

    /**
     * A finished answer. A voice prompt gets its acknowledgement first. Entities are renumbered
     * in the order given: the repository sorts on `step_number`, and every fixture starts at 1.
     */
    private fun done(requestId: String, vararg entities: AgentEntityDTO): PollingDataDTO {
        val all = listOfNotNull(voiceNote?.let { markdownEntity("### پیام صوتی دریافت شد\n\n$it") }) + entities
        return PollingDataDTO(
            id = requestId,
            eta = 1,
            status = "DONE",
            result = AgentResponseDTO(
                sessionId = "mock-session",
                lastEntity = all.lastOrNull()?.key,
                entities = all.mapIndexed { index, entity -> entity.copy(stepNumber = index + 1) },
                renderMode = "SERVER",
            ),
        )
    }

    private fun entitiesOf(fixture: String): Array<AgentEntityDTO> =
        json.decodeFromString<PollingDataDTO>(fixture).result?.entities.orEmpty().toTypedArray()

    /** A server-rendered markdown entity, the shape `render_mode: SERVER` answers use. */
    private fun markdownEntity(text: String) = AgentEntityDTO(
        key = "general_response",
        itemType = "markdown",
        data = buildJsonArray {
            addJsonObject {
                put("item_type", "markdown_item")
                put("format", "markdown")
                put("content_version", "1")
                put("text", text)
            }
        },
    )
}

private val HELP_MARKDOWN = """
### سناریوهای دستیار ساختگی

هر پرامپت بدون کلیدواژه **همه‌ی انواع حباب** را یک‌جا برمی‌گرداند. برای حالت‌های دیگر یکی از این کلمه‌ها را در پرامپت بنویس:

| کلیدواژه | چه می‌شود |
|---|---|
| راهنما / help | همین فهرست |
| خطا / failed | وضعیت FAILED با پیام سرور |
| خطای بی‌پیام / failed-silent | وضعیت FAILED بدون پیام |
| تایم‌اوت / timeout | همیشه PENDING تا سقف ۵ پیگیری |
| آهسته / slow | دو بار PENDING، بعد پاسخ کوتاه |
| لغو / cancel | وضعیت CANCEL از سمت سرور |
| قطع / network | خطای اتصال هنگام پیگیری |
| توکن / token | رد توکن در ارسال اول، ارسال دوباره با توکن تازه |
| خالی / empty | DONE بدون هیچ entity |
| مارک‌داون / markdown | فقط markdown رندرشده‌ی سرور |
| ویترین / showcase | فقط یکی از هر نوع payload |
| حالت لبه / edge | فقط حالت‌های لبه (خطای غیرقابل تکرار، کلید ناشناخته، رسانه‌ی ناقص…) |
| سرویس‌ها / services | همه‌ی کلیدهای سرویس اپ (نیاز به ورود واقعی) |

حالت‌های دسترسی (عدم مجوز، آفلاین، بدون صدا) از **تنظیمات توسعه‌دهنده ← شبیه‌سازی دستیار هوشمند** انتخاب می‌شوند و با باز کردن دوباره‌ی صفحه اعمال می‌شوند.
""".trimIndent()
