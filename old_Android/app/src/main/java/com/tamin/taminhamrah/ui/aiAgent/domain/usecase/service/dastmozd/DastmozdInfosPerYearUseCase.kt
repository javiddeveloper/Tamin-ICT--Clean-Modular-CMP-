package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.sumDayItems
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.DateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//implemented
//mapped to AverageDastmozdInfos
class DastmozdInfosPerYearUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,

    ) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.DASTMOZD_INFOS_PER_YEAR

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val date = params.getDateFilter()
        val dastmozdInfos = params.data ?: emptyList()

        return try {
            val response = repository.getWageAndHistoryInsurance(getDefaultParamsMap())
            if (response.isSuccess) {
                val items = mutableListOf<KeyValueModel>()
                val filteredList = applyFiltersToWageHistory(
                    response.data?.list,
                    date
                )
                filteredList?.forEach {
                    createKeyValuePerYer(it).forEach { keyValue ->
                        items.add(keyValue)
                    }
                    items.add(KeyValueModel("----------------", "", null))
                }
                val responseList: MutableList<ServiceResponse> = mutableListOf()

                val itemsWithData = dastmozdInfos.map {
                    it.toServiceResponse(params.serviceName, params.message) { uriString ->
                        val uriBuilder = uriString.toUri().buildUpon()
                        when {
                            uriString.contains(Constants.OBJECTION_INSURANCE_NON_EXISTENCE_DEEPLINK) -> {
                                uriBuilder.appendQueryParameter(
                                    Constants.TOOLBAR_TITLE,
                                    context.getString(R.string.objection_non_existent_histories)
                                )
                            }
                            uriString.contains(Constants.OBJECTION_INSURANCE_HISTORY) -> {
                                uriBuilder.appendQueryParameter(
                                    Constants.TOOLBAR_TITLE,
                                    context.getString(R.string.objection_insurance_history)
                                )
                            }
                            uriString.contains(Constants.ISSUANCE_WAGE_CERTIFICATE) -> {
                                uriBuilder.appendQueryParameter(
                                    Constants.TOOLBAR_TITLE,
                                    context.getString(R.string.issuance_wage_certificate)
                                )
                            }
                        }
                        uriBuilder.build().toString()
                    }
                }
                val serviceResponseItem = response(items, params)
                responseList.add(serviceResponseItem)
                responseList.addAll(itemsWithData)
                ServiceResult.Success(responseList.collectAllPromptToList())

            } else {
                val errorMessage = response.getMessage()
                    .ifBlank { "متاسفانه در دریافت سوابق پرداخت بیمه خطایی رخ داده است." }
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun response(
        items: MutableList<KeyValueModel>,
        params: ServiceParams
    ): ServiceResponse = if (items.isNotEmpty()) {
        ServiceResponse(
            params.serviceName,
            title = params.message,
            data = ServiceData.GroupButton(
                prompts = emptyList(),
                actionType = AgentActionContent.SendPrompt(params.message ?: "", params.message ?: ""),
                content = items
            )
        )
    } else {
        ServiceResponse(
            params.serviceName,
            title = params.message,
            data = ServiceData.StringMessage("متاسفانه سوابق پرداخت بیمه یافت نشد.")
        )
    }

    fun getDefaultParamsMap(): MutableMap<String, String> {
        val paramsMap = mutableMapOf<String, String>()
        paramsMap[Constants.PAGE] = Constants.DEFAULT_START_INDEX
        paramsMap[Constants.QUERY_PAGE_SIZE] = Constants.QUERY_PAGE_SIZE_60.toString()
        paramsMap[Constants.START] = Constants.DEFAULT_START_INDEX
        return paramsMap
    }
}

private fun createKeyValuePerYer(wageAndHistoryModel: WageAndHistoryModel): MutableList<KeyValueModel> {
    val finalList: MutableList<KeyValueModel> = mutableListOf()
    finalList.add(KeyValueModel("سابقه سال", wageAndHistoryModel.hisyear))
    finalList.add(KeyValueModel("اطلاعات کارگاه", wageAndHistoryModel.rwshname ?: "-"))
    finalList.add(KeyValueModel("نوع سابقه", wageAndHistoryModel.historytypedesc ?: "-"))
    finalList.add(KeyValueModel("نام شعبه", wageAndHistoryModel.brhname ?: "-"))
    finalList.add(KeyValueModel("مجموع روز ها", wageAndHistoryModel.sumDayItems().sumDays))
    return finalList

}

private fun applyFiltersToWageHistory(
    originalList: List<WageAndHistoryModel>?,
    filters: DateFilter?
): List<WageAndHistoryModel>? {
    if (filters == null) return originalList
    val finalList: MutableList<WageAndHistoryModel> = mutableListOf()

    val startDate = filters.startDate?.year
    val endDate = filters.endDate?.year
    originalList?.filter {
        when {
            startDate == null && endDate != null -> {
                it.hisyear.toInt() <= (endDate.toInt())
            }

            startDate != null && endDate == null -> {
                it.hisyear.toInt() >= (startDate.toInt())
            }

            startDate != null && endDate != null -> {
                it.hisyear.toInt() in (startDate.toInt())..(endDate.toInt())
            }

            else -> true
        }
    }?.map {
        finalList.add(it)
    }
    return finalList
}


