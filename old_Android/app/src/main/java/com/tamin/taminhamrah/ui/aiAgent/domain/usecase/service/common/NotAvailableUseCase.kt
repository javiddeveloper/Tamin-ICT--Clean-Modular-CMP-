package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.common

import android.content.Context
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotAvailableUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.UN_AVAILABLE_SERVICE

    override suspend fun execute(params: ServiceParams): ServiceResult {
         val serviceResponse = ServiceResponse(
            action = params.serviceName,
//            itemType = ItemType.KeyValue,
            title = params.message,
            data = ServiceData.StringMessage(
                message = "متاسفانه این سرویس در دسترس نمی باشد!"
            )
        )
        return ServiceResult.Success(data = listOf(serviceResponse) , )

    }
}