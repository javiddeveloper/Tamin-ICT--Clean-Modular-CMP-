package com.tamin.taminhamrah.model.agent

import kotlinx.serialization.json.JsonElement

/**
 * وضعیت‌های مختلف یک درخواست polling
 *
 * این sealed class از repository به لایه‌های بالاتر emit می‌شود.
 *
 * Flow lifecycle:
 * [Pending] → تکرار چندباره در صورت نیاز → [Done] یا [Failed] یا [Cancelled]
 */
sealed class AgentPollingState {
    /** درخواست ثبت شد و در صف پردازش است — ETA اعلام شده است */
    data class Pending(val requestId: String, val etaSeconds: Int) : AgentPollingState()

    /** پردازش کامل شد و پاسخ آماده است */
    data class Done(val response: AgentResponseDN) : AgentPollingState()

    /** پردازش ناموفق بود */
    data class Failed(val message: String) : AgentPollingState()

    /** کاربر یا سیستم درخواست را لغو کرد */
    object Cancelled : AgentPollingState()
}

/**
 * مدل پاسخ کامل Agent بعد از اتمام polling
 */
data class AgentResponseDN(
    val sessionId: String?,
    val lastEntity: String?,
    val entities: List<AiEntityDN>,
    val message: String? = null
)

/**
 * هر Entity یک action قابل اجرا در Pipeline است
 *
 * @param action شناسه سرویس به عنوان [AgentActionKey]
 * @param stepNumber ترتیب اجرا در pipeline (مرتب‌سازی صعودی)
 * @param payload داده‌هایی که AI برای اجرای سرویس فراهم کرده
 * @param data خروجی مستقیم (اگر AI داده کامل داشته باشد)
 * @param message پیام متنی اختیاری
 * @param itemType نوع نمایش: "button" | "key_value" | "message" | "form" | ...
 */
data class AiEntityDN(
    val action: AgentActionKey,
    val stepNumber: Int,
    val payload: JsonElement?,
    val data: JsonElement?,
    val message: String?,
    val itemType: String?
)

/**
 * نتیجه بررسی مجاز بودن چت
 */
data class ChatAllowedDN(
    val canStartChat: Boolean,
    val chatToken: String?,
    val errorMessage: String?
)
