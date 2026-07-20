package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.repository.AgentRepository

/**
 * بررسی اینکه آیا این کاربر مجاز به استفاده از چت‌بات AI است
 *
 * باید قبل از نمایش صفحه چت فراخوانی شود.
 * در صورت عدم مجاز بودن، [ChatAllowedDN.errorMessage] پیام را نگه می‌دارد.
 */
class CheckChatAllowedUseCase(
    private val agentRepository: AgentRepository
) {
    suspend operator fun invoke(): Result<ChatAllowedDN> =
        agentRepository.checkChatAllowed()
}
