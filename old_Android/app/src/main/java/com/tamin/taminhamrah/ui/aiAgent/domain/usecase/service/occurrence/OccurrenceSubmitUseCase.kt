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
 * Step 4 → result. Validates documents + the cross-field "work end after work start"
 * rule, maps the payload to an [com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceReq],
 * submits it, and renders the terminal result step. On success the step message carries
 * the tracking number (reportRefrenceNumber); on failure it re-renders Step 4 with an
 * error so the user can retry without losing input.
 *
 * Mirrors OccurrenceReportFragment step-4 submit + mViewModel.sendOccurrenceRequest.
 */
class OccurrenceSubmitUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.OCCURRENCE_REPORT_SUBMIT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val payload = params.payload ?: emptyMap()

            val docTypesResponse = try {
                repository.getDocumentType(null)
            } catch (e: Exception) {
                null
            }
            val docTypes = if (docTypesResponse?.isSuccess == true) {
                docTypesResponse.data?.list?.map {
                    FormOption(id = it.docTypeId.toString(), title = it.docDesc.orEmpty())
                } ?: emptyList()
            } else {
                emptyList()
            }

            val validationError = validate(payload)
            if (validationError != null) {
                return ServiceResult.Success(listOf(errorStep(params, payload, validationError, docTypes)))
            }

            val request = payload.toOccurrenceReq()

            // Optional side-effect call to match native OccurrenceReportViewModel.getAllWorkshopHistory()
            try {
                repository.getAllWorkshopHistory(
                    request.workshopCode,
                    request.pNationalCode,
                    request.branchCode,
                    request.insuranceID,
                    request.occurrenceDate
                )
            } catch (e: Exception) {
                // Non-fatal, native fragment doesn't block on this result.
            }

            val response = repository.sendOccurrenceRequest(request)

            if (response.isSuccess) {
                val tracking = response.data?.reportRefrenceNumber.orEmpty()
                val successResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "اعلام حادثه",
                    data = ServiceData.GenerativeForm(
                        schema = buildOccurrenceSchema(
                            payload = payload,
                            step = 5,
                            showCancelButton = false,
                            message = "درخواست شما با شماره رهگیری $tracking ثبت شد."
                        ),
                        payload = payload.mapValues { it.value?.toString() }
                    )
                )
                ServiceResult.Success(listOf(successResponse))
            } else {
                val reason = response.reason?.takeIf { it.isNotBlank() } ?: "خطا در ثبت درخواست"
                ServiceResult.Success(listOf(errorStep(params, payload, reason, docTypes)))
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    /** Returns a localized error message, or null when the payload is acceptable. */
    private fun validate(payload: Map<String, Any?>): String? {
        val docs = payload[OccurrenceFormKeys.DOCUMENTS]?.toString()
            ?.split(",")?.count { it.isNotBlank() } ?: 0
        if (docs < 1) return "حداقل یک مدرک بارگذاری کنید"

        val start = payload[OccurrenceFormKeys.WORK_START]?.toString().toMinutesOrNull()
        val end = payload[OccurrenceFormKeys.WORK_END]?.toString().toMinutesOrNull()
        if (start != null && end != null && end <= start) {
            return "ساعت پایان کار باید بعد از ساعت شروع کار باشد"
        }
        return null
    }

    private fun errorStep(
        params: ServiceParams,
        payload: Map<String, Any?>,
        error: String,
        docTypes: List<FormOption>
    ): ServiceResponse = ServiceResponse(
        action = params.serviceName,
        title = "اعلام حادثه",
        data = ServiceData.GenerativeForm(
            schema = buildOccurrenceSchema(
                payload = payload,
                step = 4,
                showCancelButton = true,
                errorMessage = error,
                docTypes = docTypes
            ),
            payload = payload.mapValues { it.value?.toString() }
        )
    )

    private fun String?.toMinutesOrNull(): Int? {
        if (this.isNullOrBlank()) return null
        val parts = this.split(":")
        if (parts.size != 2) return null
        val h = parts[0].trim().toIntOrNull() ?: return null
        val m = parts[1].trim().toIntOrNull() ?: return null
        return h * 60 + m
    }
}
