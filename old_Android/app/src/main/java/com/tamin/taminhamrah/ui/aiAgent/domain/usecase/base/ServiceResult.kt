package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base

import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse

sealed interface ServiceResult {

    data class Success(val data:List<ServiceResponse>) : ServiceResult
    data class Failure(val error: Throwable) : ServiceResult
    data class Error(val errorMessage: String) : ServiceResult
}