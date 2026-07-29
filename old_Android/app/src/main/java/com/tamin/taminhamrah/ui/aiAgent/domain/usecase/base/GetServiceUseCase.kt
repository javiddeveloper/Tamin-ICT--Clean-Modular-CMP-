package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base

import timber.log.Timber
import javax.inject.Inject

class GetServiceUseCase @Inject constructor(
    private val useCaseList: List<@JvmSuppressWildcards ServiceUseCase>
) {
    operator fun invoke(serviceName: ServiceNameEnum): ServiceUseCase? {
        return useCaseList.firstOrNull() {
            it.serviceName == serviceName
        }

    }
}