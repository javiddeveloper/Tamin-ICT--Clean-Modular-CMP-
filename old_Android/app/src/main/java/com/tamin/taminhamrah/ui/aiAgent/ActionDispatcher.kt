package com.tamin.taminhamrah.ui.aiAgent

import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.GetServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import timber.log.Timber
import javax.inject.Inject


class ActionDispatcher @Inject constructor(
    private val getServiceUseCase: GetServiceUseCase
) {

    suspend fun dispatch(serviceName: ServiceNameEnum?, params: ServiceParams): ServiceResult? {
        if (serviceName == null) {
            return ServiceResult.Failure(IllegalArgumentException("Unknown action: $serviceName"))
        }
        Timber.tag("ActionDispatcher" ).d( serviceName.name ?:"")

        val useCase = getServiceUseCase(serviceName)

        return useCase?.execute(params)
    }
}
