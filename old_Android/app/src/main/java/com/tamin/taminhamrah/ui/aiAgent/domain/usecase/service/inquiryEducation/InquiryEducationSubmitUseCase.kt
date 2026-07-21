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

class InquiryEducationSubmitUseCase @Inject constructor(
    private val repository: ServiceRepository,
    private val mockProvider: InquiryEducationMockProvider
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EXTEND_EDUCATION_SUBMIT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        if (InquiryEducationConfig.isMock) {
            return mockProvider.getSubmitUseCaseResult(params)
        }

        return try {
            val code = params.payload?.get("code")?.toString() ?: "1"
            val studyCode = params.payload?.get("studyCode")?.toString() ?: ""

            if (studyCode.isBlank()) {
                return ServiceResult.Failure(Exception("کد رهگیری تحصیلی الزامی است"))
            }

            var apiSuccess = false
            var messageText = ""
            var errorText = ""

            // Real API Call matching InquiryStudyCodeFragment
            try {
                // If code is "1" (myself), the API receives empty string or "1".
                // InquiryStudyCodeFragment passes selectedDependent which is nationalId (if single dependent/selected), or empty if self.
                val apiCode = if (code == "1") "" else code
                val result = repository.inquiryStudyCodeCertificate(code = apiCode, studyCode = studyCode)
                if (result.isSuccess) {
                    if (result.data.isNullOrBlank()) {
                        errorText = "اطلاعات استعلام تحصیلی یافت نشد. لطفا کد رهگیری تحصیلی را بررسی نمایید."
                    } else {
                        apiSuccess = true
                        messageText = "استعلام تحصیلی با موفقیت تایید شد:\n${result.data}"
                    }
                } else {
                    errorText = result.reason ?: "خطا در استعلام اطلاعات تحصیلی"
                }
            } catch (e: Exception) {
                errorText = e.message ?: "خطا در برقراری ارتباط با سرور"
            }

            val newData = params.payload?.toMutableMap() ?: mutableMapOf()

            val formResponse = if (apiSuccess && errorText.isEmpty()) {
                // Success: Move to Step 2 (Completed)
                ServiceResponse(
                    action = params.serviceName,
                    title = "استعلام کد تحصیلی",
                    data = ServiceData.GenerativeForm(
                        schema = buildInquiryEducationSchema(
                            payload = newData,
                            step = 2,
                            showCancelButton = false,
                            message = messageText
                        ),
                        payload = newData.mapValues { it.value?.toString() }
                    )
                )
            } else {
                // Failure: Stay on Step 1, show error message
                val optionsList = mutableListOf<FormOption>()
                var dependentFieldType = FormFieldType.DROPDOWN
                try {
                    val response = repository.checkRenewCondition()
                    if (response.isSuccess) {
                        val list = response.data?.list ?: emptyList()
                        if (list.isEmpty()) {
                            dependentFieldType = FormFieldType.HIDDEN
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
                                dependentFieldType = FormFieldType.READ_ONLY
                            } else {
                                dependentFieldType = FormFieldType.DROPDOWN
                                optionsList.add(0, FormOption(id = "1", title = "خود بیمه‌شده (خودم)"))
                            }
                        }
                    } else {
                        dependentFieldType = FormFieldType.HIDDEN
                    }
                } catch (e: Exception) {
                    dependentFieldType = FormFieldType.HIDDEN
                }

                ServiceResponse(
                    action = params.serviceName,
                    title = "استعلام کد تحصیلی",
                    data = ServiceData.GenerativeForm(
                        schema = buildInquiryEducationSchema(
                            payload = newData,
                            step = 1,
                            showCancelButton = true,
                            errorMessage = errorText,
                            optionsList = optionsList,
                            dependentFieldType = dependentFieldType
                        ),
                        payload = newData.mapValues { it.value?.toString() }
                    )
                )
            }

            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
