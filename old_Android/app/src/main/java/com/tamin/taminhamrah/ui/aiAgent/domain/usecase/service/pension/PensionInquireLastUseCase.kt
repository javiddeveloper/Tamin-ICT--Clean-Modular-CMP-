package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.pension

import android.content.Context
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.PensionInquiryModel
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PensionInquireLastUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum=ServiceNameEnum.PENSION_INQUIRY_LAST

    override suspend fun execute(params: ServiceParams): ServiceResult {

        return try {
            val response = repository.getResultOfInquirePension(getDefaultParamsMap())

            if (response.baseStatus?.serviceStatus == ServiceStatus.SUCCESS) {
                val list = response.data?.list ?: emptyList()

                val kv = PensionInquiryModel.createKeyValue(list)

                if (kv.isNotEmpty()) {
                    var items: List<ServiceResponse>
                    if (list[0].statusDesc == "00"){
                        items = listOf(
                            ServiceResponse(
                                action = params.serviceName,
//                                itemType = params.itemType,
                                title = params.message ?: "",
                                data = ServiceData.StringMessage("شما مستمری بگیر نمی باشید.")
                            )
                        )
                    } else {
                        items = listOf(
                            ServiceResponse(
                                action = params.serviceName,
//                                itemType = params.itemType,
                                title = params.message ?: "",
                                data = ServiceData.KeyValueMessage(listOf(kv[kv.lastIndex]))
                            )
                        )
                    }
                    ServiceResult.Success(items)
                } else {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.StringMessage("اطلاعات استعلام مستمری یافت نشد")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                }
            } else {
                val errorMessage = response.getMessage().ifBlank { "متاسفانه در دریافت استعلام مستمری خطایی رخ داده است!" }
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