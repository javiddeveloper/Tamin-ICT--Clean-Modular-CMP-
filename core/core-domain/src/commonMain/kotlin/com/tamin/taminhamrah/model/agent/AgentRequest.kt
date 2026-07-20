package com.tamin.taminhamrah.model.agent

/**
 * مدل درخواست ارسالی به repository
 *
 * @param prompt متن پرامپت کاربر
 * @param sessionId شناسه جلسه (null در اولین پیام جلسه)
 * @param lastEntity آخرین entity برگشتی از سرور (برای حفظ context مکالمه)
 * @param chatToken توکن احراز هویت چت‌بات
 * @param isLawPrompt اگر true باشد به endpoint قوانین ارسال می‌شود
 */
data class AgentRequest(
    val prompt: String,
    val sessionId: String? = null,
    val lastEntity: String? = null,
    val chatToken: String? = null,
    val isLawPrompt: Boolean = false
)
