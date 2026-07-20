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
    @SerialName("sessionId") val sessionId: String? = null,
    @SerialName("lastEntity") val lastEntity: String? = null,
    @SerialName("chatToken") val chatToken: String? = null,
    @SerialName("userType") val userType: String? = "INSURED"
)
