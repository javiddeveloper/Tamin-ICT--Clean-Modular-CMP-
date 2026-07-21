package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.jobHistory

import android.content.Context
import com.tamin.taminhamrah.data.remote.models.services.AllHistoryInsuranceResponseModel
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateTimestampFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//implemented
//mapped to HistoryServices
//
class HistoryServicesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,

    ) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.HISTORY_SERVICES

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val date = params.getDateTimestampFilter()
        val requestParams = mutableMapOf<String, String>()
        requestParams["limit"] = "10"

        return try {
            val response = repository.getAllHistoryInsurance(requestParams)

            if (response.baseStatus?.serviceStatus == com.tamin.taminhamrah.enums.ServiceStatus.SUCCESS) {
                val list = response.data?.list ?: emptyList()

                if (list.isNotEmpty()) {
                    val items = list.mapNotNull { item ->
                        if (
                            (date.startDate != null
                                    &&
                                    date.endDate != null)
                            &&
                            (item.year >= date.startDate.toString().take(4)
                                    &&
                                    item.year <= date.endDate.toString().take(4))
                        ) {
                            ServiceResponse(
                                action = params.serviceName,
//                                itemType = params.itemType,
                                title = "کلیه سوابق بیمه پردازی",
                                data = ServiceData.KeyValueMessage(
                                    createKeyValue(item)
                                )
                            )
                        } else {
                            null
                        }
                    }

                    if (items.isNotEmpty()) {
                        ServiceResult.Success(items)
                    }else {
                        val serviceResponse = ServiceResponse(
                            params.serviceName,
                            title = params.message,
                            data = ServiceData.StringMessage("متاسفانه اطلاعات بیمه پردازی یافت نشد!")
                        )
                        ServiceResult.Success(listOf(serviceResponse))
                    }
                } else {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.StringMessage("متاسفانه اطلاعات بیمه پردازی یافت نشد!")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                }
            } else {
                val errorMessage = response.getMessage().ifBlank { "متاسفانه در دریافت سوابق بیمه خطایی رخ داده است!" }
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun sumDays(currentItem: AllHistoryInsuranceResponseModel?): Int {
        var sum = 0
        currentItem?.apply {
            month1?.let { sum += it.toInt() }
            month2?.let { sum += it.toInt() }
            month3?.let { sum += it.toInt() }
            month4?.let { sum += it.toInt() }
            month5?.let { sum += it.toInt() }
            month6?.let { sum += it.toInt() }
            month7?.let { sum += it.toInt() }
            month8?.let { sum += it.toInt() }
            month9?.let { sum += it.toInt() }
            month10?.let { sum += it.toInt() }
            month11?.let { sum += it.toInt() }
            month12?.let { sum += it.toInt() }
        }
        return sum
    }

    fun createKeyValue(item: AllHistoryInsuranceResponseModel?): List<KeyValueModel> {
        val keyValueList = mutableListOf<KeyValueModel>()
        keyValueList.add(KeyValueModel("کارگاه", item?.workShopName ?: "-"))
//    keyValueList.add(KeyValueModel("دستمزد", item.wage ?: "0" ))
        keyValueList.add(KeyValueModel("روزهای کارکرد", sumDays(item).toString()))
        keyValueList.add(KeyValueModel("سال", "${item?.year}"))

        return keyValueList
    }
}