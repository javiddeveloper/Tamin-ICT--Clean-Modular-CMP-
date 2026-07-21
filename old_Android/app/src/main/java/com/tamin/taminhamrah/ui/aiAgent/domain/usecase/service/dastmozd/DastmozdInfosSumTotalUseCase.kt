package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.remote.models.services.AiChartItem
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModels
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.data.repository.ai.model.PromptModel
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
class DastmozdInfosSumTotalUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum =  ServiceNameEnum.DASTMOZD_INFOS_SUM_TOTAL

    override suspend fun execute(params: ServiceParams): ServiceResult {

        val viewMode = params.payload?.get("VIEW_MODE") as? String

        return if (viewMode.isNullOrEmpty()) {
            showSelectionButtons(params)
        } else {
            when (viewMode) {
                "TEXT_MODE" -> processTextMode(params)
                "CHART_MODE" -> processChartMode(params)
                else -> ServiceResult.Error("حالت نمایش نامعتبر است")
            }
        }
    }

private fun showSelectionButtons(params: ServiceParams): ServiceResult {
    val title = params.message
    val groupButtonData = ServiceData.GroupButton(
        prompts = listOf(
            PromptModel(
                prompt = "نمایش متنی",
                action = AgentActionContent.DisplayReport(
                    actionText = "TEXT_MODE",
                    title = title,
                    customColor = "#266C63FF"
                )
            ),
            PromptModel(
                prompt = "نمایش نموداری",
                action = AgentActionContent.DisplayReport(
                    actionText = "CHART_MODE",
                    title = title,
                    customColor = "#266C63FF"
                )
            )
        ),
        actionType = AgentActionContent.DisplayReport(actionText = "NONE")
    )

    val serviceResponse = ServiceResponse(
        action = params.serviceName,
        title = "لطفا نحوه نمایش سوابق را انتخاب کنید:",
        data = groupButtonData
    )
    return ServiceResult.Success(listOf(serviceResponse))
}
private suspend fun processTextMode(params: ServiceParams): ServiceResult {
    val date = params.getDateFilter()
    val dastmozdInfos = params.data ?: emptyList()
    return try {
        val response = repository.getWageAndHistoryInsurance(getDefaultParamsMap())
        if (response.isSuccess) {
            val items = mutableListOf<KeyValueModel>()
            val filteredList = applyFiltersToWageHistory(response.data?.list, date)
            var totalSum = 0

            filteredList?.forEach { wageModel ->
                val currentYear = wageModel.hisyear.toIntOrNull() ?: return@forEach

                val filterStartYear = date.startDate?.year?.toIntOrNull()
                val filterStartMonth = date.startDate?.month?.toIntOrNull()
                val filterEndYear = date.endDate?.year?.toIntOrNull()
                val filterEndMonth = date.endDate?.month?.toIntOrNull()

                var startMonthForCurrentYear = 1
                var endMonthForCurrentYear = 12

                if (filterStartYear != null && currentYear == filterStartYear) {
                    startMonthForCurrentYear = filterStartMonth ?: 1
                }

                if (filterEndYear != null && currentYear == filterEndYear) {
                    endMonthForCurrentYear = filterEndMonth ?: 12
                }

                val yearSum = sumMonthsDays(wageModel, startMonthForCurrentYear, endMonthForCurrentYear)
                totalSum += yearSum

                items.add(KeyValueModel(" مجموع روزهای سال ${wageModel.hisyear}", "$yearSum روز"))
                items.add(KeyValueModel("اطلاعات کارگاه", wageModel.rwshname ?: "-"))
                items.add(KeyValueModel("", "", null))
            }
            items.add(KeyValueModel("----------------", "", null))
            items.add(KeyValueModel("مجموع کل روزهای سوابق", "$totalSum روز", null))

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
            if (items.isNotEmpty()) {
                val serviceResponse = ServiceResponse(
                    params.serviceName,
                    title = params.message,
                    data = ServiceData.GroupButton(
                        prompts = emptyList(),
                        actionType = AgentActionContent.SendPrompt(params.message ?: "", params.message ?: ""),
                        content = items
                    )
                )
                responseList.add(serviceResponse)
            } else {
                val serviceResponse = ServiceResponse(
                    params.serviceName,
                    title = params.message,
                    data = ServiceData.StringMessage("سوابق پرداخت بیمه یافت نشد")
                )
                responseList.add(serviceResponse)
            }
            responseList.addAll(itemsWithData)
            ServiceResult.Success(responseList.collectAllPromptToList())

        } else {
            val errorMessage = response.getMessage().ifBlank { context.getString(R.string.wage_sum_error) }
            ServiceResult.Error(errorMessage)
        }
    } catch (e: Exception) {
        ServiceResult.Failure(e)
    }
}
private suspend fun processChartMode(params: ServiceParams): ServiceResult {
    val date = params.getDateFilter()
    val dastmozdInfos = params.data ?: emptyList()
    return try {
        val response = repository.getWageAndHistoryInsurance(getDefaultParamsMap())
        if (response.isSuccess) {
            val originalList = response.data?.list
            val filteredList = applyFiltersToWageHistory(originalList, date)

            val responseList: MutableList<ServiceResponse> = mutableListOf()
            val itemsWithData = dastmozdInfos.map {
                it.toServiceResponse(params.serviceName, params.message) { uriString ->
                    val uriBuilder = uriString.toUri().buildUpon()
                    if (uriString.contains(Constants.OBJECTION_INSURANCE_NON_EXISTENCE_DEEPLINK)) {
                        uriBuilder.appendQueryParameter(
                            Constants.TOOLBAR_TITLE,
                            context.getString(R.string.objection_non_existent_histories)
                        )
                    }
                    uriBuilder.build().toString()
                }
            }

            if (filteredList.isNullOrEmpty()) {
                responseList.add(ServiceResponse(
                    params.serviceName,
                    title = params.message,
                    data = ServiceData.StringMessage("سوابقی برای نمایش نمودار یافت نشد")
                ))
                responseList.addAll(itemsWithData)
                return ServiceResult.Success(responseList)
            }

            val chartItems = filteredList.map { wageModel ->
                val sumDays = sumMonthsDays(wageModel, 1, 12)
                AiChartItem(
                    label = wageModel.hisyear,
                    value = sumDays.toFloat(),
                    description = "اطلاعات سال ${wageModel.hisyear}"
                )
            }

            val detailPayload = chartItems.mapIndexed { i, _ ->
                val sameYearList = WageAndHistoryModels()
                sameYearList.addAll(filteredList.filter { it.hisyear == filteredList[i].hisyear })
                sameYearList
            }

            val detailSchema = detailPayload.mapIndexed { i, yearItems ->
                val year = filteredList[i].hisyear
                fun v(s: String?): Int = s?.trim()?.toIntOrNull() ?: 0
                fun sumMonth(items: WageAndHistoryModels, monthNum: Int): Int {
                    var sum = 0
                    items.forEach { model ->
                        sum += when (monthNum) {
                            1 -> v(model.hismon1)
                            2 -> v(model.hismon2)
                            3 -> v(model.hismon3)
                            4 -> v(model.hismon4)
                            5 -> v(model.hismon5)
                            6 -> v(model.hismon6)
                            7 -> v(model.hismon7)
                            8 -> v(model.hismon8)
                            9 -> v(model.hismon9)
                            10 -> v(model.hismon10)
                            11 -> v(model.hismon11)
                            12 -> v(model.hismon12)
                            else -> 0
                        }
                    }
                    return sum
                }

                val monthChartItems = (1..12).map { monthNum ->
                    AiChartItem(
                        label = monthNum.toString(),
                        value = sumMonth(yearItems, monthNum).toFloat(),
                        description = "ماه $monthNum"
                    )
                }
                val detailChartField = FormField(
                    id = "chartData",
                    label = "",
                    type = FormFieldType.CHART,
                    required = false,
                    enabled = false,
                    chartItems = monthChartItems
                )
                FormSchema(
                    key = "WAGE_AND_HISTORY_DETAIL",
                    steps = listOf(
                        FormStep(
                            index = 1,
                            title = "اطلاعات سال $year",
                            fields = listOf(detailChartField)
                        )
                    ),
                    currentStep = 1
                )
            }

            val chartField = FormField(
                id = "chartData",
                label = "",
                type = FormFieldType.CHART,
                required = false,
                enabled = false,
                chartItems = chartItems,
                extras = mapOf(
                    "detailPayload" to detailPayload,
                    "detailSchema" to detailSchema,
                    "detailAction" to "WAGE_AND_HISTORY_DETAIL"
                )
            )
            val schema = FormSchema(
                key = "CHART",
                steps = listOf(
                    FormStep(
                        index = 1,
                        title = params.message,
                        fields = listOf(chartField)
                    )
                ),
                currentStep = 1,
                showCancelButton = false
            )
            val serviceResponse = ServiceResponse(
                params.serviceName,
                title = params.message,
                data = ServiceData.GenerativeForm(schema = schema)
            )
            responseList.add(serviceResponse)
            responseList.addAll(itemsWithData)
            ServiceResult.Success(responseList.collectAllPromptToList())

        } else {
            val errorMessage = response.getMessage().ifBlank { context.getString(R.string.wage_sum_error) }
            ServiceResult.Error(errorMessage)
        }
    } catch (e: Exception) {
        ServiceResult.Failure(e)
    }
}
private fun getDefaultParamsMap(): MutableMap<String, String> {
        val paramsMap = mutableMapOf<String, String>()
        paramsMap[Constants.PAGE] = Constants.DEFAULT_START_INDEX
        paramsMap[Constants.QUERY_PAGE_SIZE] = Constants.QUERY_PAGE_SIZE_60.toString()
        paramsMap[Constants.START] = Constants.DEFAULT_START_INDEX
        return paramsMap
    }

}

private fun sumMonthsDays(model: WageAndHistoryModel, startMonth: Int, endMonth: Int): Int {
    fun v(s: String?): Int = s?.trim()?.toIntOrNull() ?: 0
    var sum = 0
    for (monthNum in startMonth..endMonth) {
        sum += when (monthNum) {
            1 -> v(model.hismon1)
            2 -> v(model.hismon2)
            3 -> v(model.hismon3)
            4 -> v(model.hismon4)
            5 -> v(model.hismon5)
            6 -> v(model.hismon6)
            7 -> v(model.hismon7)
            8 -> v(model.hismon8)
            9 -> v(model.hismon9)
            10 -> v(model.hismon10)
            11 -> v(model.hismon11)
            12 -> v(model.hismon12)
            else -> 0
        }
    }
    return sum
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

