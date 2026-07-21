package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.prescription

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.FilterKey
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

//implemented
//mapped to PatientHistory
class ElectronicPrescriptionUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,

) : ServiceUseCase {
    override val serviceName: ServiceNameEnum=ServiceNameEnum.PATIENT_HISTORY

    override suspend fun execute(params: ServiceParams): ServiceResult {

        val filters = HashMap<String, String>()
        val date = params.getDateFilter()
        val nationalCode = repository.getNationalCode()
        filters[FilterKey.NATIONAL_CODE.key] = nationalCode
        filters[FilterKey.REQUEST_TYPE_ID.key] = "1"
        filters[FilterKey.DEPENDANT_USER_NATIONAL_CODE.key] = "0"
        val startDate = date.startDate
        val endDate = date.endDate

        val now = System.currentTimeMillis()
        val sevenDaysMillis = TimeUnit.DAYS.toMillis(7)

        when {
            // 1) both null → last 7 days until now
            startDate == null && endDate == null -> {
                filters[FilterKey.START_DATE.key] = (now - sevenDaysMillis).toString()
                filters[FilterKey.END_DATE.key] = now.toString()
            }

            // 2) start null, end provided → last 7 days until endDate
            startDate == null && endDate != null -> {
                val endTs = endDate.toTimeStamp()
                filters[FilterKey.START_DATE.key] = (endTs?.minus(sevenDaysMillis)).toString()
                filters[FilterKey.END_DATE.key] = endTs.toString()
            }

            // 3) start provided, end null → start until now
            startDate != null && endDate == null -> {
                filters[FilterKey.START_DATE.key] = startDate.toTimeStamp().toString()
                filters[FilterKey.END_DATE.key] = now.toString()
            }

            // 4) both provided
            else -> {
                filters[FilterKey.START_DATE.key] = startDate!!.toTimeStamp().toString()
                filters[FilterKey.END_DATE.key] = endDate!!.toTimeStamp().toString()
            }
        }



        return try {
            val response = repository.getElectronicPrescriptionList(filters)

            if (response.baseStatus?.serviceStatus == ServiceStatus.SUCCESS) {
                val list = response.data?.list ?: emptyList()

                if (list.isEmpty()) {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.StringMessage("نسخه‌ای یافت نشد.")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                } else {
                    val items =  mutableListOf<ServiceResponse>()
                   items.add( ServiceResponse(
                        action = params.serviceName,
//                        itemType = params.itemType,
                        title =params.message,
                        data = ServiceData.HeaderMessage
                    ))
                    items.addAll( list.take(5).map {
                        ServiceResponse(
                            action = params.serviceName,
//                            itemType = params.itemType,
                            title =it.patientName,
                            data = ServiceData.Clickable(
                                message = it.createAiKeyValue(),
                                actionType = AgentActionContent.LocalDeepLink(
                                    "mytamin://prescription_detail".toUri().buildUpon()
                                        .appendQueryParameter("ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION", it.noteHeadEprescID?.toString() ?: "0")
                                        .appendQueryParameter("ARG_REQUEST_TYPE", it.prescType)
                                        .appendQueryParameter("PRES_TYPE", it.prescType)
                                        .appendQueryParameter("ARG_NATIONAL_CODE", nationalCode)
                                        .appendQueryParameter("ARG_CHILD_NATIONAL_CODE", "0")
                                        .appendQueryParameter("ARG_FLAG_SATA", it.flagSata)
                                        .appendQueryParameter("TOOLBAR_TITLE", it.prescName)
                                        .appendQueryParameter("TOOLBAR_SUBTITLE", "${context.getString(R.string.doc_name)} : ${it.docName}")
                                        .appendQueryParameter("TOOLBAR_ICON_IMAGE", it.iconRes.toString())
                                        .build().toString(),
                                    "مشاهده جزئیات نسخه"
                                )
                            )
                        )

                    })
                    ServiceResult.Success(items)
                }
            } else {
                val errorMessage = response.getMessage().ifBlank { "متاسفانه در دریافت نسخ الکترونیک خطایی رخ داده است." }
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
