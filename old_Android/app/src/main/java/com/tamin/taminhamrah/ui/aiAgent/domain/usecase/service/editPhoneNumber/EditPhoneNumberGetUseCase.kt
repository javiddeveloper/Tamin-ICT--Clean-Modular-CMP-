package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber

import com.tamin.taminhamrah.data.repository.CommonRepository
import com.tamin.taminhamrah.data.repository.ai.model.FieldValidation
import com.tamin.taminhamrah.data.repository.ai.model.FieldValidationType
import com.tamin.taminhamrah.data.repository.ai.model.FormAction
import com.tamin.taminhamrah.data.repository.ai.model.FormActionHandler
import com.tamin.taminhamrah.data.repository.ai.model.FormActionStyle
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class EditPhoneNumberGetUseCase @Inject constructor(
    private val repository: CommonRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EDIT_PHONE_NUMBER_GET

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val currentPhone = repository.getUserPhoneNumber() ?: ""
            val newData = params.payload?.toMutableMap() ?: mutableMapOf()
            newData.remove("newPhone")
            newData.remove("otpCode")
            newData.remove("code")
            newData.remove("editMobileHash")
            newData["currentPhone"] = currentPhone

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "ویرایش شماره همراه",
                data = ServiceData.GenerativeForm(
                    schema = buildEditPhoneSchema(
                        currentPhone = currentPhone,
                        payload = newData,
                        step = 1,
                        showCancelButton = true
                    ),
                    payload = newData.mapValues { it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}

fun buildEditPhoneSchema(
    currentPhone: String,
    payload: Map<String, Any?>?,
    step: Int,
    showCancelButton: Boolean,
    errorMessage: String? = null,
    message: String? = null,
    isLoading: Boolean = false
): FormSchema {
    val newPhone = payload?.get("newPhone")?.toString()
    val code = payload?.get("code")?.toString() ?: payload?.get("otpCode")?.toString()
    val editMobileHash = payload?.get("editMobileHash")?.toString()
    val step1Fields = listOf(
        FormField(
            id = "currentPhone",
            label = "شماره فعلی",
            type = FormFieldType.READ_ONLY,
            value = currentPhone
        ),
        FormField(
            id = "newPhone",
            label = "شماره جدید",
            type = FormFieldType.PHONE,
            value = newPhone,
            required = true,
            validations = listOf(
                FieldValidation(
                    type = FieldValidationType.LENGTH,
                    value = "11",
                    message = "شماره موبایل باید ۱۱ رقم باشد"
                ),
                FieldValidation(
                    type = FieldValidationType.STARTS_WITH,
                    value = "09",
                    message = "شماره موبایل باید با ۰۹ شروع شود"
                )
            )
        )
    )
    val step1Actions = buildList {
        add(
            FormAction(
                id = ServiceNameEnum.EDIT_PHONE_NUMBER_SEND_OTP.key,
                title = "ارسال کد",
                handler = FormActionHandler.SERVICE,
                style = FormActionStyle.PRIMARY
            )
        )
        if (showCancelButton) {
            add(
                FormAction(
                    id = ServiceNameEnum.EDIT_PHONE_NUMBER_CANCEL.key,
                    title = "انصراف",
                    handler = FormActionHandler.SERVICE,
                    style = FormActionStyle.SECONDARY
                )
            )
        }
    }
    val step2Fields = listOf(
        FormField(
            id = "newPhone",
            label = "شماره جدید",
            type = FormFieldType.READ_ONLY,
            value = newPhone
        ),
        FormField(
            id = "code",
            label = "کد تایید",
            type = FormFieldType.OTP,
            value = code,
            required = true,
            validations = listOf(
                FieldValidation(
                    type = FieldValidationType.MIN_LENGTH,
                    value = "4",
                    message = "کد تایید صحیح نیست"
                )
            )
        ),
        FormField(
            id = "editMobileHash",
            label = "editMobileHash",
            type = FormFieldType.HIDDEN,
            value = editMobileHash
        )
    )
    val step2Actions = buildList {
        add(
            FormAction(
                id = ServiceNameEnum.EDIT_PHONE_NUMBER_VERIFY_OTP.key,
                title = "تایید",
                handler = FormActionHandler.SERVICE,
                style = FormActionStyle.PRIMARY
            )
        )
        add(
            FormAction(
                id = ServiceNameEnum.EDIT_PHONE_NUMBER_GET.key,
                title = "اصلاح شماره",
                handler = FormActionHandler.SERVICE,
                style = FormActionStyle.SECONDARY
            )
        )
        if (showCancelButton) {
            add(
                FormAction(
                    id = ServiceNameEnum.EDIT_PHONE_NUMBER_CANCEL.key,
                    title = "انصراف",
                    handler = FormActionHandler.SERVICE,
                    style = FormActionStyle.SECONDARY
                )
            )
        }
    }
    return FormSchema(
        key = "EDIT_MOBILE",
        steps = listOf(
            FormStep(index = 1, title = "مرحله ۱", fields = step1Fields, actions = step1Actions),
            FormStep(index = 2, title = "مرحله ۲", fields = step2Fields, actions = step2Actions),
            FormStep(index = 3, title = "مرحله ۳")
        ),
        currentStep = step,
        showCancelButton = showCancelButton,
        errorMessage = errorMessage,
        message = message,
        isLoading = isLoading
    )
}
