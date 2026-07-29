package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.result

import com.tamin.taminhamrah.ui.aiAgent.domain.AgentResponse

sealed interface AgentResult {
    data class Success(val data: AgentResponse) : AgentResult
    data class Failure(val error: Throwable) : AgentResult
    data class Error(val errorMessage: String) : AgentResult

}