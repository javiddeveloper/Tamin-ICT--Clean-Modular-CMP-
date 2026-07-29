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
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.ceil
import kotlin.math.roundToLong

//implemented

@Singleton
class AverageWagePerDateUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum =
        ServiceNameEnum.AVERAGE_DASTMOZD_INFOS_PER_DATE

    companion object {
        private const val MIN_YEAR = 0
        private const val MAX_YEAR = 9999
    }

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val dastmozdInfos = params.data ?: emptyList()

        return try {
            val response = repository.getWageAndHistoryInsurance(getDefaultParamsMap())

            if (response.baseStatus?.serviceStatus != ServiceStatus.SUCCESS) {
                return ServiceResult.Error(
                    response.getMessage()
                        .ifBlank { context.getString(R.string.wage_calculation_error) }
                )
            }

            val records = response.data?.list.orEmpty()
            val dateFilter = params.getDateFilter()

            val startYear = dateFilter.startDate?.year?.toIntOrNull() ?: MIN_YEAR
            val startMonth = dateFilter.startDate?.month?.toIntOrNull() ?: 1

            val endYear = dateFilter.endDate?.year?.toIntOrNull() ?: MAX_YEAR
            val endMonth = dateFilter.endDate?.month?.toIntOrNull() ?: 12

// 📌 لاگ ۱: بررسی دقیق بازه زمانی که از سمت فیلتر کامپوننت ارسال شده است
            Timber.tag("WAGE_DEBUG").d("=== بازه زمانی فیلتر شده ===")
            Timber.tag("WAGE_DEBUG")
                .d("شروع از: $startYear/$startMonth تا پایان: $endYear/$endMonth")

            val monthlyDataMap = mutableMapOf<Pair<Int, Int>, Pair<Long, Int>>()

            records.forEach { record ->
                val year = record.hisyear.toIntOrNull() ?: return@forEach
                if (year !in startYear..endYear) return@forEach

                val monthStart = if (year == startYear) startMonth else 1
                val monthEnd = if (year == endYear) endMonth else 12

                record.monthlyRecords().forEachIndexed { index, monthRecord ->
                    val month = index + 1
                    if (month !in monthStart..monthEnd) return@forEachIndexed

                    val wage = monthRecord.wage?.replace(",", "")?.toLongOrNull() ?: 0L
                    val days = monthRecord.days?.toIntOrNull() ?: 0

                    if (days > 0 && wage > 0) {
                        val currentKey = Pair(year, month)
                        val existing = monthlyDataMap[currentKey] ?: Pair(0L, 0)
                        monthlyDataMap[currentKey] =
                            Pair(existing.first + wage, existing.second + days)
                    }
                }
            }

// 📌 لاگ ۲: لیست تمام ماه‌هایی که در این بازه فیلتر شده، دیتای معتبر داشتند
            Timber.tag("WAGE_DEBUG").d("=== ماه‌های وارد شده به محاسبات در این بازه ===")
            monthlyDataMap.forEach { (key, value) ->
                Timber.tag("WAGE_DEBUG")
                    .d("سال و ماه: ${key.first}/${key.second} -> دستمزد: ${value.first} ریال | روز: ${value.second}")
            }

            var totalWages = 0L
            var totalDays = 0

            monthlyDataMap.values.forEach { (wage, days) ->
                totalWages += wage
                totalDays += days
            }

// 📌 لاگ ۳: مبالغ کل جمع‌آوری شده قبل از تقسیم نهایی
            Timber.tag("WAGE_DEBUG").d("=== مجموع کل محاسبات بازه ===")
            Timber.tag("WAGE_DEBUG").d("جمع کل مبالغ دستمزد: $totalWages ریال")
            Timber.tag("WAGE_DEBUG").d("جمع کل روزهای کارکرد واقعی: $totalDays روز")

            if (totalDays == 0) {
                return ServiceResult.Success(
                    listOf(
                        ServiceResponse(
                            params.serviceName,
                            title = params.message,
                            data = ServiceData.StringMessage(
                                "متاسفانه اطلاعات دستمزد در بازه زمانی مشخص شده یافت نشد."
                            )
                        )
                    )
                )
            }

            val dailyAverage = totalWages.toDouble() / totalDays
            val average = ceil(dailyAverage * 30).toLong()

// 📌 لاگ ۴: خروجی نهایی فرمول تأمین اجتماعی بر اساس روز
            Timber.tag("WAGE_DEBUG").d("دستمزد میانگین روزانه (کل تقسیم بر روز): $dailyAverage")
            Timber.tag("WAGE_DEBUG").d("میانگین ماهانه نهایی (روزانه ضرب در ۳۰): $average")

            val formattedWage = DecimalFormat("#,###").format(average)

            val items = mutableListOf<ServiceResponse>()

            items += ServiceResponse(
                action = params.serviceName,
                title = params.message,
                data = ServiceData.GroupButton(
                    prompts = emptyList(),
                    actionType = AgentActionContent.SendPrompt(
                        params.message.orEmpty(),
                        params.message.orEmpty()
                    ),
                    content = listOf(
                        KeyValueModel(
                            "میانگین دستمزد در بازه زمانی : ",
                            "$formattedWage ${context.getString(R.string.price_rial)}"
                        )
                    )
                )
            )

            items += dastmozdInfos.map {
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

            ServiceResult.Success(items.collectAllPromptToList())

        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private data class MonthlyRecord(
        val wage: String?,
        val days: String?
    )

    private fun WageAndHistoryModel.monthlyRecords(): List<MonthlyRecord> =
        listOf(
            MonthlyRecord(hiswage1, hismon1),
            MonthlyRecord(hiswage2, hismon2),
            MonthlyRecord(hiswage3, hismon3),
            MonthlyRecord(hiswage4, hismon4),
            MonthlyRecord(hiswage5, hismon5),
            MonthlyRecord(hiswage6, hismon6),
            MonthlyRecord(hiswage7, hismon7),
            MonthlyRecord(hiswage8, hismon8),
            MonthlyRecord(hiswage9, hismon9),
            MonthlyRecord(hiswage10, hismon10),
            MonthlyRecord(hiswage11, hismon11),
            MonthlyRecord(hiswage12, hismon12)
        )

    private fun getDefaultParamsMap(): MutableMap<String, String> =
        mutableMapOf<String, String>().apply {
            put(Constants.PAGE, Constants.DEFAULT_START_INDEX)
            put(Constants.QUERY_PAGE_SIZE, Constants.QUERY_PAGE_SIZE_60.toString())
            put(Constants.START, Constants.DEFAULT_START_INDEX)
        }
}