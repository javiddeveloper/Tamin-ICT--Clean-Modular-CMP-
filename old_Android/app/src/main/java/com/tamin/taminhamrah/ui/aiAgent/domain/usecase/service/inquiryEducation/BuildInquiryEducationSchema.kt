package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation

import com.tamin.taminhamrah.data.repository.ai.model.FormAction
import com.tamin.taminhamrah.data.repository.ai.model.FormActionHandler
import com.tamin.taminhamrah.data.repository.ai.model.FormActionStyle
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.data.repository.ai.model.FormOption
import com.tamin.taminhamrah.data.repository.ai.model.FieldValidation
import com.tamin.taminhamrah.data.repository.ai.model.FieldValidationType
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum

fun buildInquiryEducationSchema(
    payload: Map<String, Any?>?,
    step: Int,
    showCancelButton: Boolean,
    errorMessage: String? = null,
    message: String? = null,
    isLoading: Boolean = false,
    optionsList: List<FormOption> = emptyList(),
    dependentFieldType: FormFieldType = FormFieldType.DROPDOWN,
    dependentFieldLabel: String = "انتخاب فرد (فرزند/تبعی)"
): FormSchema {

    val selectedCode = payload?.get("code")?.toString() ?: "1"
    val studyCode = payload?.get("studyCode")?.toString()

    val step1Fields = buildList {
        // Dropdown field for dependent selection
        add(
            FormField(
                id = "code",
                label = dependentFieldLabel,
                type = dependentFieldType,
                value = selectedCode,
                required = (dependentFieldType != FormFieldType.HIDDEN),
                options = optionsList
            )
        )
        // Text field for study code entry
        add(
            FormField(
                id = "studyCode",
                label = "کد رهگیری تحصیلی",
                type = FormFieldType.TEXT,
                value = studyCode,
                required = true,
                validations = listOf(
                    FieldValidation(
                        type = FieldValidationType.MIN_LENGTH,
                        value = "4",
                        message = "کد رهگیری تحصیلی باید حداقل ۴ رقم باشد"
                    )
                )
            )
        )
    }

    val step1Actions = buildList {
        add(
            FormAction(
                id = ServiceNameEnum.EXTEND_EDUCATION_SUBMIT.key,
                title = "استعلام و ثبت",
                handler = FormActionHandler.SERVICE,
                style = FormActionStyle.PRIMARY
            )
        )
        if (showCancelButton) {
            add(
                FormAction(
                    id = ServiceNameEnum.EXTEND_EDUCATION_CANCEL.key,
                    title = "انصراف",
                    handler = FormActionHandler.SERVICE,
                    style = FormActionStyle.SECONDARY
                )
            )
        }
    }

    return FormSchema(
        key = "inquiry_education",
        steps = listOf(
            FormStep(
                index = 1,
                title = "ورود اطلاعات استعلام",
                fields = step1Fields,
                actions = step1Actions
            ),
            FormStep(
                index = 2,
                title = "نتیجه استعلام"
            )
        ),
        currentStep = step,
        showCancelButton = showCancelButton,
        errorMessage = errorMessage,
        message = message,
        isLoading = isLoading
    )
}
