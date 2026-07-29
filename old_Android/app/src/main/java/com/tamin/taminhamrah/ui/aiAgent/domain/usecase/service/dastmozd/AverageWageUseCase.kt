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
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getFilters
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.ceil

//implemented
class AverageWageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.AVERAGE_DASTMOZD_INFOS

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val filters = params.getFilters()
        val averageSalaryYear = filters["averageSalary"]?.toIntOrNull() ?: 0
        val dastmozdInfos = params.data ?: emptyList()

        return try {
            val response = repository.getWageAndHistoryInsurance(getDefaultParamsMap())

            if (response.baseStatus?.serviceStatus == ServiceStatus.SUCCESS) {
                var yearlyRecords = response.data?.list ?: emptyList()
                yearlyRecords = yearlyRecords.sortedByDescending { it.hisyear.toIntOrNull() ?: 0 }

                val neededYearlyRecords = mutableListOf<WageAndHistoryModel>()
                var totalWages = 0L
                var hasAnyRecord = false

                if (averageSalaryYear > 0) {

                    data class MonthRecord(
                        val days: Int,
                        val wage: Long
                    )

                    val targetDays = averageSalaryYear * 365
                    val monthlyRecords = mutableListOf<MonthRecord>()

                    fun addMonth(days: String?, wage: String?) {
                        val daysInt = days?.toIntOrNull() ?: 0
                        val wageLong = wage?.replace(",", "")?.toLongOrNull() ?: 0L

                        if (daysInt > 0 && wageLong > 0L) {
                            monthlyRecords.add(
                                MonthRecord(
                                    days = daysInt,
                                    wage = wageLong
                                )
                            )
                        }
                    }

                    // Match CalculateWagePensionFragment.internalCalc()
                    for (i in yearlyRecords.indices.reversed()) {
                        val record = yearlyRecords[i]

                        addMonth(record.hismon12, record.hiswage12)
                        addMonth(record.hismon11, record.hiswage11)
                        addMonth(record.hismon10, record.hiswage10)
                        addMonth(record.hismon9, record.hiswage9)
                        addMonth(record.hismon8, record.hiswage8)
                        addMonth(record.hismon7, record.hiswage7)
                        addMonth(record.hismon6, record.hiswage6)
                        addMonth(record.hismon5, record.hiswage5)
                        addMonth(record.hismon4, record.hiswage4)
                        addMonth(record.hismon3, record.hiswage3)
                        addMonth(record.hismon2, record.hiswage2)
                        addMonth(record.hismon1, record.hiswage1)
                    }

                    var sumDays = 0

                    for (record in monthlyRecords) {
                        if (sumDays < targetDays) {
                            sumDays += record.days
                            totalWages += record.wage
                            hasAnyRecord = true
                        } else {
                            break
                        }
                    }
                } else {
                    val date = params.getDateFilter()
                    val startYear = date.startDate?.year?.toIntOrNull() ?: 0
                    val endYear = date.endDate?.year?.toIntOrNull() ?: 9999

                    yearlyRecords.forEach { yearRecord ->
                        val year = yearRecord.hisyear.toIntOrNull() ?: 0
                        if (year in startYear..endYear || (startYear == 0 && endYear == 9999)) {
                            neededYearlyRecords.add(yearRecord)
                            hasAnyRecord = true
                        }
                    }
                }

                if (!hasAnyRecord) {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
                        title = params.message,
                        data = ServiceData.StringMessage("متاسفانه اطلاعات دستمزد یافت نشد.")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                } else {
                    val average =
                        if (averageSalaryYear > 0)
                            ceil(totalWages.toDouble() / (averageSalaryYear * 12))
                        else
                            0.0
                    val formattedWage = DecimalFormat("#,###").format(average)

                    val items = mutableListOf<ServiceResponse>()
                    if (averageSalaryYear > 0) {
                        items.add(
                            ServiceResponse(
                                action = params.serviceName,
                                title = params.message,
                                data = ServiceData.GroupButton(
                                    prompts = emptyList(),
                                    actionType = AgentActionContent.SendPrompt(params.message ?: "", params.message ?: ""),
                                    content = listOf(
                                        KeyValueModel(
                                            "میانگین دستمزد : ",
                                            "$formattedWage ${context.getString(R.string.price_rial)}",
                                        )
                                    )
                                )
                            )
                        )
                    } else {
                        val keyValueParams = neededYearlyRecords.flatMap { it.createKeyValue() }
                        if (keyValueParams.isEmpty()) {
                            return ServiceResult.Error("متاسفانه سابقه بیمه پردازی یافت نشد!")
                        } else {
                            val item = ServiceResponse(
                                params.serviceName,
                                title = params.message ?: "",
                                data = ServiceData.GroupButton(
                                    prompts = emptyList(),
                                    actionType = AgentActionContent.SendPrompt(params.message ?: "", params.message ?: ""),
                                    content = keyValueParams
                                )
                            )
                            items.add(item)
                        }
                    }

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
                    items.addAll(itemsWithData)

                    ServiceResult.Success(items.collectAllPromptToList())
                }
            } else {
                val errorMessage = response.getMessage().ifBlank { context.getString(R.string.wage_calculation_error) }
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
