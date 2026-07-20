package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * درخواست ارسالی به سرور AI
 *
 * @param prompt متن پرامپت کاربر
 * @param sessionId شناسه جلسه چت (null در اولین پیام)
 * @param lastEntity آخرین Entity دریافتی (برای حفظ context)
 * @param chatToken توکن مخصوص چت (از checkChatAllowed دریافت می‌شود)
 */
@Serializable
data class AgentRequestDTO(
    @SerialName("prompt") val prompt: String,
    @SerialName("session_id") val sessionId: String? = null,
    @SerialName("last_entity") val lastEntity: String? = null,
    @SerialName("chat_token") val chatToken: String? = null
)
