package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormOption
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

class InquiryEducationGetUseCase @Inject constructor(
    private val repository: ServiceRepository,
    private val mockProvider: InquiryEducationMockProvider
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EXTEND_EDUCATION_GET

    override suspend fun execute(params: ServiceParams): ServiceResult {
        if (InquiryEducationConfig.isMock) {
            return mockProvider.getGetUseCaseResult(params)
        }

        return try {
            val payload = params.payload?.toMutableMap() ?: mutableMapOf()
            val optionsList = mutableListOf<FormOption>()
            var dependentFieldType = FormFieldType.DROPDOWN
            var selectedCode = "1"
            var errorMessage: String? = null

            // Try to load dependents from server using real API
            try {
                val response = repository.checkRenewCondition()
                if (response.isSuccess) {
                    val list = response.data?.list ?: emptyList()
                    if (list.isEmpty()) {
                        // Empty list: hide dependent dropdown selection
                        dependentFieldType = FormFieldType.HIDDEN
                        selectedCode = "1"
                    } else {
                        list.forEach { item ->
                            val identity = item.dependentInfo.identityInfo
                            val fullName = "${identity.firstName ?: ""} ${identity.lastName ?: ""}"
                            val nationalId = identity.nationalId ?: ""
                            if (nationalId.isNotBlank()) {
                                optionsList.add(FormOption(id = nationalId, title = "$fullName (فرزند)"))
                            }
                        }

                        if (optionsList.size == 1) {
                            // Exact 1 dependent: lock it as READ_ONLY and auto-select
                            dependentFieldType = FormFieldType.READ_ONLY
                            selectedCode = optionsList[0].id
                        } else {
                            // Multiple dependents: let user select them, or themselves
                            dependentFieldType = FormFieldType.DROPDOWN
                            optionsList.add(0, FormOption(id = "1", title = "خود بیمه‌شده (خودم)"))
                            selectedCode = "1"
                        }
                    }
                } else {
                    errorMessage = response.reason ?: "خطا در دریافت اطلاعات فرزندان"
                    dependentFieldType = FormFieldType.HIDDEN
                    selectedCode = "1"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "خطا در برقراری ارتباط با سرور"
                dependentFieldType = FormFieldType.HIDDEN
                selectedCode = "1"
            }

            // Set the selected dependent in payload
            payload["code"] = selectedCode
            payload.remove("studyCode")

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "استعلام کد تحصیلی",
                data = ServiceData.GenerativeForm(
                    schema = buildInquiryEducationSchema(
                        payload = payload,
                        step = 1,
                        showCancelButton = true,
                        optionsList = optionsList,
                        dependentFieldType = dependentFieldType,
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
}
