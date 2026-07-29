package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.treatment

import android.content.Context
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//implemented
class TreatmentCostsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.TREATMENT_COST

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val requestParams = mutableMapOf<String, String>()
        requestParams["limit"] = Constants.QUERY_PAGE_SIZE_10.toString()
        return try {
            val response = repository.getTreatmentCosts(requestParams)
            if (response.isSuccess) {
                val items = response.data?.list?.flatMap {
                    it.createKeyValue()
                }
                if (items.isNullOrEmpty()) {
                    val serviceResponse = ServiceResponse(
                        action = params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.StringMessage("متاسفانه اطلاعات خسارت متفرقه ای یافت نشد.")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                } else {
                    val serviceResponse = ServiceResponse(
                        action = params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.KeyValueMessage(items )
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                }

            } else {
                val errorMessage = response.getMessage().ifBlank { "متاسفانه در دریافت اطلاعات خسارت متفرقه خطایی رخ داده است!" }
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}

