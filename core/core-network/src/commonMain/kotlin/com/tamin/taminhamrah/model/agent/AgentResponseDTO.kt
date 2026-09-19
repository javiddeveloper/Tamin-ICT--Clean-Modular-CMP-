package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

/**
 * پاسخ کامل AI بعد از اتمام پردازش
 *
 * این مدل داخل [PollingDataDTO.result] قرار می‌گیرد.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AgentResponseDTO(
    @JsonNames("session_id") @SerialName("sessionId") val sessionId: String? = null,
    @SerialName("lastEntity") val lastEntity: String? = null,
    @SerialName("entities") val entities: List<AgentEntityDTO>? = null,
    @SerialName("message") val message: String? = null,
    /** SERVER when entities carry already-rendered content (markdown); CLIENT or absent otherwise. */
    @SerialName("render_mode") val renderMode: String? = null,
    /** The server's conversation memory; any shape, returned untouched with the next prompt. */
    @SerialName("state") val state: JsonElement? = null,
    @SerialName("history") val history: JsonElement? = null,
)

/**
 * هر Entity یک "action" هست که باید اجرا شود
 *
 * @param key شناسه سرویس - مثل "dastmozd_infos"، "pension_inquiry_all"
 * @param stepNumber ترتیب اجرا در Pipeline
 * @param payload داده‌هایی که AI برای اجرای سرویس ارسال می‌کند
 * @param data خروجی اولیه (اگر AI مستقیم داده داشته باشد)
 * @param message پیام متنی مربوط به این entity
 * @param itemType نوع نمایش (مثلاً markdown)
 *
 * سرور این دو فیلد را snake_case (`step_number`, `item_type`) می‌فرستد؛ هر دو شکل پذیرفته می‌شود.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AgentEntityDTO(
    @SerialName("key") val key: String? = null,
    @JsonNames("step_number") @SerialName("stepNumber") val stepNumber: Int? = null,
    @SerialName("payload") val payload: JsonElement? = null,
    @SerialName("data") val data: JsonElement? = null,
    @SerialName("message") val message: String? = null,
    @JsonNames("item_type") @SerialName("itemType") val itemType: String? = null,
    @SerialName("message_id") val messageId: String? = null
)

/**
 * یک آیتم داخل آرایه‌ی `data` که نوعش با `item_type` مشخص می‌شود؛ برای markdown متن در [text] است.
 */
@Serializable
data class AgentDataItemDTO(
    @SerialName("item_type") val itemType: String? = null,
    @SerialName("format") val format: String? = null,
    @SerialName("content_version") val contentVersion: String? = null,
    @SerialName("text") val text: String? = null
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
    @SerialName("canStartChat") val canStartChat: Boolean? = null,
    @SerialName("chatToken") val chatToken: String? = null,
    @SerialName("errorMessage") val errorMessage: String? = null,
    @SerialName("canSendVoice") val canSendVoice: Boolean? = null,
    @SerialName("ttl") val ttl: Long? = null
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
