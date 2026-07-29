package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.appointment

import com.tamin.taminhamrah.data.remote.models.ai.agent.AppointmentModel
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class AppointmentUseCase @Inject constructor(
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.APPOINMET

    override suspend fun execute(params: ServiceParams): ServiceResult {

        return try {
            val appointments = params.data

            if (appointments.isNullOrEmpty()) {
                return ServiceResult.Success(listOf(createEmptyResponse(params)))
            }

            val items = mutableListOf<ServiceResponse>()

            items.add(ServiceResponse(
                action = params.serviceName,
                title = params.message,
                data = ServiceData.HeaderMessage
            ))
            val itemsWithData = appointments.map { it.toServiceResponse(params.serviceName, params.message) }
            val itemsWithPromptInList = itemsWithData.collectAllPromptToList()
            items.addAll(itemsWithPromptInList)
//            items.addAll(appointments.map { it.toServiceResponse(params.serviceName,params.message) })

            ServiceResult.Success(items)

        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

}


private fun createEmptyResponse(params: ServiceParams) = ServiceResponse(
    action = params.serviceName,
    title = params.message,
    data = ServiceData.StringMessage("متاسفانه نوبتی یافت نشد.")
)


