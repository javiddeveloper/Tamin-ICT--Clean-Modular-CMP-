package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.prescription

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescription
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.DateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.FilterKey
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.CancellationException
import javax.inject.Inject

class ElectronicPrescriptionLastUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum =
        ServiceNameEnum.PATIENT_HISTORY_LAST


    override suspend fun execute(params: ServiceParams): ServiceResult {

        val nationalCode = repository.getNationalCode()
        val dateFilter = params.getDateFilter()

        val baseFilters = hashMapOf(
            FilterKey.NATIONAL_CODE.key to nationalCode,
            FilterKey.REQUEST_TYPE_ID.key to "1",
            FilterKey.DEPENDANT_USER_NATIONAL_CODE.key to "0"
        )

        return try {

            val list = if (dateFilter.startDate != null || dateFilter.endDate != null) {
                fetchInRange(baseFilters, dateFilter)
            } else {
                fetchLastByMonthlySearch(baseFilters)
            }

            if (list.isEmpty()) {
                createEmptyResponse(params)
            } else {
                ServiceResult.Success(buildResponse(params, list[0], nationalCode))
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }


    private suspend fun fetchInRange(
        filters: HashMap<String, String>,
        date: DateFilter
    ): List<ElectronicPrescription> {

        filters[FilterKey.START_DATE.key] =
            date.startDate!!.toTimeStamp().toString()

        filters[FilterKey.END_DATE.key] =
            date.endDate!!.toTimeStamp().toString()

        val response = repository.getElectronicPrescriptionList(filters)

        return response.data?.list
            ?: emptyList()
    }


    private suspend fun fetchLastByMonthlySearch(
        baseFilters: HashMap<String, String>
    ): List<ElectronicPrescription> {

        val calendar = Calendar.getInstance()

        while (true) {

            val end = calendar.timeInMillis

            calendar.add(Calendar.MONTH, -1)

            val start = calendar.timeInMillis

            val filters = HashMap(baseFilters).apply {
                put(FilterKey.START_DATE.key, start.toString())
                put(FilterKey.END_DATE.key, end.toString())
            }

            val response = repository.getElectronicPrescriptionList(filters)
            val list = response.data?.list ?: emptyList()
            if (response.isError) {
                return list
            }

            if (list.isNotEmpty()) {
                return list
            }
        }

    }


    // =========================================================
    // Build single latest response
    // =========================================================
    private fun buildResponse(
        params: ServiceParams,
        item: ElectronicPrescription,
        nationalCode: String
    ): List<ServiceResponse> {

        return listOf(
            ServiceResponse(
                action = params.serviceName,
                title = params.message,
                data = ServiceData.Clickable(
                    message = item.createAiKeyValue(),
                    actionType = AgentActionContent.LocalDeepLink(
                        "mytamin://prescription_detail".toUri().buildUpon()
                            .appendQueryParameter(
                                "ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION",
                                item.noteHeadEprescID?.toString() ?: "0"
                            )
                            .appendQueryParameter("ARG_REQUEST_TYPE", item.prescType)
                            .appendQueryParameter("PRES_TYPE", item.prescType)
                            .appendQueryParameter("ARG_NATIONAL_CODE", nationalCode)
                            .appendQueryParameter("ARG_CHILD_NATIONAL_CODE", "0")
                            .appendQueryParameter("ARG_FLAG_SATA", item.flagSata)
                            .appendQueryParameter("TOOLBAR_TITLE", item.prescName)
                            .appendQueryParameter(
                                "TOOLBAR_SUBTITLE",
                                "${context.getString(R.string.doc_name)} : ${item.docName}"
                            )
                            .appendQueryParameter("TOOLBAR_ICON_IMAGE", item.iconRes.toString())
                            .build().toString(),
                        "مشاهده جزئیات نسخه"
                    )
                )
            )
        )
    }


    private fun createEmptyResponse(params: ServiceParams) =
        ServiceResult.Success(
            listOf(
                ServiceResponse(
                    params.serviceName,
                    title = params.message,
                    data = ServiceData.StringMessage("نسخه‌ای یافت نشد.")
                )
            )
        )
}