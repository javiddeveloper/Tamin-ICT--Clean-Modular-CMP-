package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import kotlinx.coroutines.flow.Flow

/**
 * قرارداد لایه داده برای سیستم Agent
 *
 * - [sendPrompt]: ارسال پرامپت و دریافت Flow از وضعیت‌های polling
 * - [cancelRequest]: لغو یک درخواست در حال پردازش
 * - [checkChatAllowed]: بررسی اینکه آیا کاربر مجاز به استفاده از چت هست
 */
interface AgentRepository {

    /**
     * ارسال پرامپت به سرور AI و poll کردن تا دریافت پاسخ
     *
     * Flow emit می‌کند:
     * 1. [AgentPollingState.Pending] — بلافاصله بعد از ثبت درخواست
     * 2. [AgentPollingState.Done] — وقتی پردازش تمام شد
     * 3. [AgentPollingState.Failed] — در صورت خطا یا timeout
     */
    fun sendPrompt(request: AgentRequest): Flow<AgentPollingState>

    /**
     * لغو یک درخواست polling در حال اجرا
     */
    suspend fun cancelRequest(requestId: String): Result<Unit>

    /**
     * بررسی مجاز بودن کاربر برای استفاده از چت‌بات
     */
    suspend fun checkChatAllowed(): Result<ChatAllowedDN>
}
