package com.tamin.taminhamrah.ui.aiAgent.domain.usecase

import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AgentRepository
import javax.inject.Inject

class IsChatAllowedLocalUseCase @Inject constructor(
    private val repository: AgentRepository
) {
    operator fun invoke(): Boolean {
        return repository.isChatAllowed()
    }
}
