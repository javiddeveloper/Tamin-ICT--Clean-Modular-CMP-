package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.illness

import android.content.Context
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.createKeyValue
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RepIllnessLastUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val serviceRepository: ServiceRepository,
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.REPILLNESS_LAST

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val response = serviceRepository.getViewShorttermRequestList(getDefaultParamsMap())
            if (response.isSuccess) {
                val items = mutableListOf<KeyValueModel>()

                val lastItem = response.data?.list
                    ?.filter { it.sDate != null }
                    ?.maxByOrNull { it.sDate!! }
                    ?: response.data?.list?.lastOrNull()

                lastItem?.let { model ->
                    model.createKeyValue().forEach { keyValue ->
                        items.add(keyValue)
                    }
                }

                val serviceResponse = ServiceResponse(
                    params.serviceName,
//                    itemType = ItemType.KeyValue,
                    title = params.message,
                    data = ServiceData.KeyValueMessage(items)
                )
                ServiceResult.Success(listOf(serviceResponse))
            } else {
                val errorMessage = response.getMessage().ifBlank { "متاسفانه در دریافت اطلاعات خطایی رخ داده است!" }
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
    fun getDefaultParamsMap(): MutableMap<String, String> {
        val paramsMap = mutableMapOf<String, String>()
        paramsMap[Constants.PAGE] = Constants.DEFAULT_START_INDEX
        paramsMap[Constants.QUERY_PAGE_SIZE] = Constants.QUERY_PAGE_SIZE_60.toString()
        paramsMap[Constants.START] = Constants.DEFAULT_START_INDEX
        return paramsMap
    }
}
