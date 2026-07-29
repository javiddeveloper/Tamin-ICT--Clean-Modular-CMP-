package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base

import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams

interface ServiceUseCase {
     val serviceName: ServiceNameEnum?
    suspend fun execute(params: ServiceParams): ServiceResult
}