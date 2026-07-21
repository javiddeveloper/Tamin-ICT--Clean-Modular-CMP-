package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.illness

import android.content.Context
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateTimestampFilter
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//implemented
//might not work properly
//mapped to CalculateIllness
class WageCompensationUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.CALCULATE_ILLNESS

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val date = params.getDateTimestampFilter()

        // End Date: If null, use "Now" (System.currentTimeMillis())
        val effectiveEndDate: Long = date.endDate ?: System.currentTimeMillis()

        // Start Date: If null, use "effectiveEndDate - 1 Month"
        val effectiveStartDate: Long = date.startDate ?: run {
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = effectiveEndDate
            calendar.add(java.util.Calendar.MONTH, -1) // Subtract 1 month
            calendar.timeInMillis
        }

        return try {
            var maritalStatus = "1"
            val marital = repository.getInsuranceActiveRelation(getDefaultParamsMap())

            if (marital.baseStatus?.serviceStatus == ServiceStatus.SUCCESS) {
                val mlist = marital.data?.list ?: emptyList()
                val conditionMet = mlist.any {
                    (it.relationWithTamin?.baseTendency?.tendencyCode?.toIntOrNull()
                        ?: 0) in intArrayOf(100, 103, 107, 108, 109)
                }
                maritalStatus = if (conditionMet) "2" else "1"
            }
            val response = repository.calculateWageIllDay(
                effectiveStartDate.toString(),
                effectiveEndDate.toString(),
                maritalStatus
            )
            if (response.isSuccess) {
                val list = response.data

                if (list.isNullOrEmpty()) {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.StringMessage("متاسفانه اطلاعات غرامت دستمزد یافت نشد!")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                } else {
                    val items = mutableListOf<KeyValueModel>()
                    if (list[0].toLongOrNull() == null) {
                        val serviceResponse = ServiceResponse(
                            params.serviceName,
//                        itemType = ItemType.KeyValue,
                            title = params.message,
                            data = ServiceData.StringMessage(list[0])
                        )
                        ServiceResult.Success(listOf(serviceResponse))

                    } else {
                        items.add(
                            KeyValueModel(
                                "متوسط دستمزد ۹۰ روز آخر ",
                                Utility.getRialWithSeparator(list[0].toLong())
                            )
                        )
                        items.add(
                            KeyValueModel(
                                "مبلغ قابل پرداخت ",
                                Utility.getRialWithSeparator(list[1].toLong())
                            )
                        )
                        if (date.startDate == null && date.endDate == null) {
                            items.add(
                                KeyValueModel(
                                    "بازه زمانی محاسبه",
                                    "به‌صورت پیش‌فرض یک ماه اخیر — برای محاسبه دقیق‌تر، تاریخ بیماری را وارد کنید."
                                )
                            )
                        }


                        val serviceResponse = ServiceResponse(
                            params.serviceName,
//                        itemType = ItemType.KeyValue,
                            title = params.message,
                            data = ServiceData.KeyValueMessage(items)
                        )
                        ServiceResult.Success(listOf(serviceResponse))
                    }
                }
            } else {
                val errorMessage = response.message?.message?.ifBlank { "متاسفانه در دریافت اطلاعات غرامت دستمزد خطایی رخ داده است!" }
                    ?: "متاسفانه در دریافت اطلاعات غرامت دستمزد خطایی رخ داده است!"
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Error(e.message.toString())
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
