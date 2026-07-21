package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount

import com.tamin.taminhamrah.data.repository.ai.model.FieldValidation
import com.tamin.taminhamrah.data.repository.ai.model.FieldValidationType
import com.tamin.taminhamrah.data.repository.ai.model.FormAction
import com.tamin.taminhamrah.data.repository.ai.model.FormActionHandler
import com.tamin.taminhamrah.data.repository.ai.model.FormActionStyle
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormOption
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class EditBankAccountGetUseCase @Inject constructor() : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EDIT_BANK_ACCOUNT_GET

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val newData = params.payload?.toMutableMap() ?: mutableMapOf()

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "ویرایش شماره حساب",
                data = ServiceData.GenerativeForm(
                    schema = buildEditBankAccountSchema(
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

fun buildEditBankAccountSchema(
    payload: Map<String, Any?>?,
    step: Int,
    showCancelButton: Boolean,
    errorMessage: String? = null,
    message: String? = null,
    isLoading: Boolean = false
): FormSchema {
    val accountNumber = payload?.get("accountNumber")?.toString()
    val bankId = payload?.get("bankId")?.toString()
    val accountTypeId = payload?.get("accountTypeId")?.toString()
    val startDate = payload?.get("startDate")?.toString()
    val bankOptions = listOf(
        FormOption("01", "بانک رفاه"),
        FormOption("02", "بانک ملی ایران"),
        FormOption("03", "بانک ملت"),
        FormOption("04", "بانک تجارت"),
        FormOption("07", "بانک سپه"),
        FormOption("05", "بانک صادرات")
    )
    val accountTypeOptions = listOf(
        FormOption("01", "قرض الحسنه"),
        FormOption("02", "پس انداز عادی"),
        FormOption("03", "پس انداز همراه"),
        FormOption("04", "جاری عادی"),
        FormOption("05", "جاری همراه")
    )
    val fields = listOf(
        FormField(
            id = "accountNumber",
            label = "شماره حساب",
            type = FormFieldType.NUMBER,
            value = accountNumber,
            required = true,
            validations = listOf(
                FieldValidation(
                    type = FieldValidationType.MIN_LENGTH,
                    value = "6",
                    message = "شماره حساب معتبر نیست"
                )
            )
        ),
        FormField(
            id = "bankId",
            label = "بانک",
            type = FormFieldType.DROPDOWN,
            value = bankId,
            required = true,
            options = bankOptions
        ),
        FormField(
            id = "accountTypeId",
            label = "نوع حساب",
            type = FormFieldType.DROPDOWN,
            value = accountTypeId,
            required = true,
            options = accountTypeOptions
        ),
        FormField(
            id = "startDate",
            label = "تاریخ شروع",
            type = FormFieldType.DATE,
            value = startDate,
            required = true
        )
    )
    val actions = buildList {
        add(
            FormAction(
                id = ServiceNameEnum.EDIT_BANK_ACCOUNT_SUBMIT.key,
                title = "ثبت",
                handler = FormActionHandler.SERVICE,
                style = FormActionStyle.PRIMARY
            )
        )
        if (showCancelButton) {
            add(
                FormAction(
                    id = ServiceNameEnum.EDIT_BANK_ACCOUNT_CANCEL.key,
                    title = "انصراف",
                    handler = FormActionHandler.SERVICE,
                    style = FormActionStyle.SECONDARY
                )
            )
        }
    }
    return FormSchema(
        key = "EDIT_BANK_ACCOUNT",
        steps = listOf(
            FormStep(index = 1, title = "مرحله ۱", fields = fields, actions = actions),
            FormStep(index = 2, title = "مرحله ۲")
        ),
        currentStep = step,
        showCancelButton = showCancelButton,
        errorMessage = errorMessage,
        message = message,
        isLoading = isLoading
    )
}
