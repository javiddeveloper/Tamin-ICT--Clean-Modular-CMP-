package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.FormOption
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

/**
 * Step 1 → Step 2. Loads the user's workshops (by national code) into a dropdown and
 * pre-fills employer/branch/insurance fields from the insured relation lookup, then
 * renders Step 2. Mirrors OccurrenceReportViewModel.getAllWorkshops + getInsuredRelation.
 */
class OccurrenceWorkshopUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.OCCURRENCE_REPORT_WORKSHOP

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val payload = params.payload?.toMutableMap() ?: mutableMapOf()
            val nationalCode = payload[OccurrenceFormKeys.NATIONAL_ID]?.toString()

            if (nationalCode.isNullOrBlank()) {
                return ServiceResult.Failure(Exception("کد ملی یافت نشد"))
            }

            val workshopsResponse = repository.getAllWorkshops(nationalCode)
            val workshopOptions = mapWorkshopOptions(workshopsResponse.data?.list)

            // Pre-fill employer/branch/insurance details where available (non-fatal).
            runCatching {
                val relation = repository.getInsuredRelation(nationalCode)
                if (relation.isSuccess) {
                    relation.data?.let { info ->
                        payload.putIfAbsentValue(OccurrenceFormKeys.EMPLOYER_NAME, info.employerName)
                        payload.putIfAbsentValue(OccurrenceFormKeys.EMPLOYER_PHONE, info.employerMobile)
                        payload.putIfAbsentValue(OccurrenceFormKeys.WORKSHOP_ADDRESS, info.workAddress)
                        payload.putIfAbsentValue(OccurrenceFormKeys.WORKSHOP_PHONE, info.workTel)
                        payload.putIfAbsentValue(OccurrenceFormKeys.RESIDENTIAL_ADDRESS, info.address)
                        payload[OccurrenceFormKeys.INSURANCE_ID] = info.insuranceId
                        payload[OccurrenceFormKeys.BRANCH_CODE] = info.brhCode
                        payload[OccurrenceFormKeys.BRANCH_NAME] = info.brhName
                        payload[OccurrenceFormKeys.ISU_TYPE_CODE] = info.isuType
                        payload[OccurrenceFormKeys.ISU_TYPE_DESC] = info.isuTypeDesc
                    }
                }
            }

            val errorMessage = if (workshopOptions.isEmpty()) {
                "کارگاهی برای شما یافت نشد"
            } else null

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "اعلام حادثه",
                data = ServiceData.GenerativeForm(
                    schema = buildOccurrenceSchema(
                        payload = payload,
                        step = 2,
                        showCancelButton = true,
                        workshopOptions = workshopOptions,
                        errorMessage = errorMessage
                    ),
                    payload = payload.mapValues { it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    /**
     * Maps the raw workshop list (List<Object> from the server) into dropdown options.
     * Each option id encodes "<workshopCode>|<branchCode>" so the submit use case can
     * recover both. Mirrors OccurrenceReportViewModel.getAllWorkshopFlow parsing.
     */
    private fun mapWorkshopOptions(list: List<Any?>?): List<FormOption> {
        if (list.isNullOrEmpty()) return emptyList()
        return list.mapNotNull { raw ->
            val cleaned = raw.toString().removePrefix("[").removeSuffix("]")
            val parts = cleaned.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (parts.isEmpty()) return@mapNotNull null
            val workshopCode = parts.first()
            val branchCode = parts.last()
            FormOption(
                id = "$workshopCode${OccurrenceFormKeys.WORKSHOP_ID_SEPARATOR}$branchCode",
                title = "$workshopCode - شعبه $branchCode"
            )
        }
    }

    private fun MutableMap<String, Any?>.putIfAbsentValue(key: String, value: String?) {
        if (value.isNullOrBlank()) return
        if (this[key]?.toString().isNullOrBlank()) this[key] = value
    }
}
