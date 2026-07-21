package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.ceil

class DastmozInforCalcIllnessPensionerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.DASTMOZD_INFOS_CALCILLNESS_PENSIONER

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val dastmozdInfos = params.data ?: emptyList()
        return try {
            val eligibleAmountPension = eligibleAmountPension()
            if (eligibleAmountPension.success) {
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
                responseList.add(
                    ServiceResponse(
                        action = params.serviceName,
                        title = params.message,
                        data = ServiceData.GroupButton(
                            prompts = emptyList(),
                            actionType = AgentActionContent.SendPrompt(params.message ?: "", params.message ?: ""),
                            content = listOf(
                                KeyValueModel(
                                    "مبلغ استحقاقی مستمری",
                                    Utility.getRialWithSeparator(eligibleAmountPension.data)
                                )
                            )
                        )
                    )
                )
                responseList.addAll(itemsWithData)
                ServiceResult.Success(responseList.collectAllPromptToList())
            } else {
                ServiceResult.Error(eligibleAmountPension.message)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private suspend fun eligibleAmountPension(): EligibleModel = coroutineScope {
        var premiumPaymentHistoryYear = 0.0
        val wageDataList = mutableListOf<WageAndHistoryModel>()
        val combinedRecordListResponse = async { repository.getCombinedRecordList(null) }
        val wageAndHistoryInsuranceResponse = async { repository.getWageAndHistoryInsurance(null) }
        val combinedRecordListResult = combinedRecordListResponse.await()
        val wageAndHistoryInsuranceResult = wageAndHistoryInsuranceResponse.await()

        if (combinedRecordListResult.isSuccess) {
            combinedRecordListResult.data?.list?.get(0)?.sumHistoryYears?.let { year ->
                premiumPaymentHistoryYear = year.toDouble() / 365
            }
        }
        if (wageAndHistoryInsuranceResult.isSuccess) {
            wageDataList.addAll(wageAndHistoryInsuranceResult.data?.list ?: emptyList())
        }
       return@coroutineScope if (wageDataList.isNotEmpty()) {

            EligibleModel(
                data = internalCalc(wageDataList, premiumPaymentHistoryYear),
                success = true,
                message = ""
            )

        } else {
            val errorMsg = when {
                !combinedRecordListResult.isSuccess -> combinedRecordListResult.getMessage()
                !wageAndHistoryInsuranceResult.isSuccess -> wageAndHistoryInsuranceResult.getMessage()
                else -> "در دریافت مستمری اشکالی به وجود آمده است"
            }
            EligibleModel(
                data = null,
                success = false,
                message = errorMsg.ifBlank { "در دریافت مستمری اشکالی به وجود آمده است" }
            )
        }
    }



    data class EligibleModel(val data: Long?, val message: String, val success: Boolean)


    private fun internalCalc(
        list: List<WageAndHistoryModel>,
        premiumPaymentHistoryYear: Double
    ): Long {
        val basicWage = 11112690
        val listDays = arrayListOf<String>()
        val listWages = arrayListOf<String>()
        (list.size - 1 downTo 1).forEach { i ->
            list[i].hismon12?.let { mon ->
                listDays.add(mon)
                list[i].hiswage12?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon11?.let { mon ->
                listDays.add(mon)
                list[i].hiswage11?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon10?.let { mon ->
                listDays.add(mon)
                list[i].hiswage10?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon9?.let { mon ->
                listDays.add(mon)
                list[i].hiswage9?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon8?.let { mon ->
                listDays.add(mon)
                list[i].hiswage8?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon7?.let { mon ->
                listDays.add(mon)
                list[i].hiswage7?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon6?.let { mon ->
                listDays.add(mon)
                list[i].hiswage6?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon5?.let { mon ->
                listDays.add(mon)
                list[i].hiswage5?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon4?.let { mon ->
                listDays.add(mon)
                list[i].hiswage4?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon3?.let { mon ->
                listDays.add(mon)
                list[i].hiswage3?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon2?.let { mon ->
                listDays.add(mon)
                list[i].hiswage2?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon1?.let { mon ->
                listDays.add(mon)
                list[i].hiswage1?.let { wage ->
                    listWages.add(wage)
                }
            }
        }
        var sumDays = 0
        var sumWages = 0.0


        for (i in 0 until listDays.size) {
            if (sumDays < 730) {
                sumDays += listDays[i].toInt()
                sumWages += listWages[i].toInt()
            } else {
                break
            }
        }
        val data1 =
            BigDecimal(premiumPaymentHistoryYear).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        val data2 = ceil(sumWages / 24)
        var data3 = ceil((data2 / 30) * data1)
        if (data1 >= 20 && data3 < 11112690) {
            data3 = basicWage.toDouble()
        }
        if (data1 < 20) {
            val minWage = (data1 / 30) * basicWage
            if (data3 < minWage) {
                data3 = minWage
            }
        }
        return data3.toLong()

    }

}