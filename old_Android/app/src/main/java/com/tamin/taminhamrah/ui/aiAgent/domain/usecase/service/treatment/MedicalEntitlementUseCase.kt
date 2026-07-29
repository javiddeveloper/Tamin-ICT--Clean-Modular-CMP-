package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.treatment

import android.content.Context
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved.createKeyValueAI
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//implemented
//mapped to Booklet
class MedicalEntitlementUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,

) : ServiceUseCase {
    override val serviceName: ServiceNameEnum=ServiceNameEnum.BOOKLET

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val response = repository.getDeservedTreatment()
            if (response.baseStatus?.serviceStatus == ServiceStatus.SUCCESS) {
                val list = response.data?.list ?: emptyList()
                val item = list.firstOrNull()
                
                if (item != null) {
                    val items = mutableListOf<KeyValueModel>()
                    list.forEach {
                        it.createKeyValueAI().forEach { keyValue ->
                            items.add(keyValue)
                        }
                    }
                    val serviceResponses = listOf(
                        ServiceResponse(
                            action = params.serviceName,
//                            itemType = ItemType.KeyValue,
                            title = params.message,
                            data = ServiceData.KeyValueMessage(items)
                        )
                    )
                    ServiceResult.Success(serviceResponses)
                } else {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.StringMessage("متاسفانه اطلاعات استحقاق درمان یافت نشد!")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                }
            } else {
                val errorMessage = response.getMessage().ifBlank { "متاسفانه در دریافت وضعیت استحقاق درمان خطایی رخ داده است!" }
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
