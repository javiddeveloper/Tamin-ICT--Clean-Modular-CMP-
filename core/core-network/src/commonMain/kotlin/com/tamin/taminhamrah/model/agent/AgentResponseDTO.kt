package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * پاسخ کامل AI بعد از اتمام پردازش
 *
 * این مدل داخل [PollingDataDTO.result] قرار می‌گیرد.
 */
@Serializable
data class AgentResponseDTO(
    @SerialName("session_id") val sessionId: String? = null,
    @SerialName("last_entity") val lastEntity: String? = null,
    @SerialName("entities") val entities: List<AgentEntityDTO>? = null,
    @SerialName("message") val message: String? = null
)

/**
 * هر Entity یک "action" هست که باید اجرا شود
 *
 * @param key شناسه سرویس - مثل "dastmozd_infos"، "pension_inquiry_all"
 * @param stepNumber ترتیب اجرا در Pipeline
 * @param payload داده‌هایی که AI برای اجرای سرویس ارسال می‌کند
 * @param data خروجی اولیه (اگر AI مستقیم داده داشته باشد)
 * @param message پیام متنی مربوط به این entity
 * @param itemType نوع نمایش در UI (button | key_value | message ...)
 */
@Serializable
data class AgentEntityDTO(
    @SerialName("key") val key: String? = null,
    @SerialName("step_number") val stepNumber: Int? = null,
    @SerialName("payload") val payload: JsonElement? = null,
    @SerialName("data") val data: JsonElement? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("item_type") val itemType: String? = null
)

/**
 * پاسخ بررسی مجاز بودن چت
 */
@Serializable
data class ChatAllowedDTO(
    @SerialName("status") val status: Int? = null,
    @SerialName("family") val family: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("data") val data: ChatAllowedDataDTO? = null
)

@Serializable
data class ChatAllowedDataDTO(
    @SerialName("can_start_chat") val canStartChat: Boolean? = null,
    @SerialName("chat_token") val chatToken: String? = null,
    @SerialName("error_message") val errorMessage: String? = null
)

/**
 * پاسخ لغو درخواست
 */
@Serializable
data class CancelResponseDTO(
    @SerialName("status") val status: Int? = null,
    @SerialName("family") val family: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("data") val data: CancelDataDTO? = null
)

@Serializable
data class CancelDataDTO(
    @SerialName("success") val success: Boolean? = null,
    @SerialName("message") val message: String? = null
)
