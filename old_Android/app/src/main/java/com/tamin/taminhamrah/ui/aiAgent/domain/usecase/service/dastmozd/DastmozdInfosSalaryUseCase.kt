package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.DateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.collections.forEach

class DastmozdInfosSalaryUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum=ServiceNameEnum.DASTMOZD_INFOS_SALARY
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
                filteredList?.forEach { wageAndHistoryModel ->
                    val currentYear = wageAndHistoryModel.hisyear.toIntOrNull() ?: return@forEach

                    val filterStartYear = date.startDate?.year?.toIntOrNull()
                    val filterStartMonth = date.startDate?.month?.toIntOrNull()

                    val filterEndYear = date.endDate?.year?.toIntOrNull()
                    val filterEndMonth = date.endDate?.month?.toIntOrNull()

                    var startMonthForCurrentYear = 1
                    var endMonthForCurrentYear = 12

                    // Adjust start month based on filter
                    if (filterStartYear != null && currentYear == filterStartYear) {
                        startMonthForCurrentYear = filterStartMonth ?: 1
                    }

                    // Adjust end month based on filter
                    if (filterEndYear != null && currentYear == filterEndYear) {
                        endMonthForCurrentYear = filterEndMonth ?: 12
                    }

                    val monthlyEntriesForCurrentYear = mutableListOf<KeyValueModel>()

            // Iterate through the months for the current year
            for (monthNum in startMonthForCurrentYear..endMonthForCurrentYear) {
                wageAndHistoryModel.getMonthlyKeyValue(monthNum)?.let { monthlyKeyValue ->
                    monthlyEntriesForCurrentYear.add(monthlyKeyValue)
                }
            }

                if (monthlyEntriesForCurrentYear.isNotEmpty()) {
                    // Add general info once per WageAndHistoryModel
                    items.add(KeyValueModel("سابقه سال", wageAndHistoryModel.hisyear))
                    items.add(KeyValueModel("اطلاعات کارگاه", wageAndHistoryModel.rwshname ?: "-"))
                    items.add(KeyValueModel("نوع سابقه", wageAndHistoryModel.historytypedesc ?: "-"))
                    items.add(KeyValueModel("نام شعبه", wageAndHistoryModel.brhname ?: "-"))
                    items.addAll(monthlyEntriesForCurrentYear)
                    items.add(KeyValueModel("----------------", "", null))
                }
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
                val errorMessage = response.getMessage().ifBlank { context.getString(R.string.dastmozd_infos_error) }
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
}
