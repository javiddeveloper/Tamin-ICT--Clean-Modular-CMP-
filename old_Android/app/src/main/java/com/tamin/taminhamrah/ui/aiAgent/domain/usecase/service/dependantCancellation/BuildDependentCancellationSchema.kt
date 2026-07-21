package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation

import com.tamin.taminhamrah.data.repository.ai.model.FieldValidation
import com.tamin.taminhamrah.data.repository.ai.model.FieldValidationType
import com.tamin.taminhamrah.data.repository.ai.model.FormAction
import com.tamin.taminhamrah.data.repository.ai.model.FormActionHandler
import com.tamin.taminhamrah.data.repository.ai.model.FormActionStyle
import com.tamin.taminhamrah.data.repository.ai.model.FormField
import com.tamin.taminhamrah.data.repository.ai.model.FormFieldType
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum

fun buildDependentCancellationSchema(
    payload: Map<String, Any?>?,
    step: Int,
    showCancelButton: Boolean,
    errorMessage: String? = null,
    message: String? = null,
    isLoading: Boolean = false
): FormSchema {

    val dependentId = payload?.get("dependentId")?.toString() ?: payload?.get("nationalCode")?.toString()
    val relationId = payload?.get("relationId")?.toString()
    val reasonId = payload?.get("reasonId")?.toString()
    val cancellationDate = payload?.get("cancellationDate")?.toString()

    val step1Fields = listOf(
        FormField(
            id = "dependentId",
            label = "انتخاب فرد تبعی",
            type = FormFieldType.DROPDOWN,
            value = dependentId,
            required = true
        )
    )

    val step1Actions = buildList {
        add(
            FormAction(
                id = ServiceNameEnum.DEPENDENT_CANCELLATION_CONFIRM.key,
                title = "ادامه",
                handler = FormActionHandler.SERVICE,
                style = FormActionStyle.PRIMARY
            )
        )
        if (showCancelButton) {
            add(
                FormAction(
                    id = ServiceNameEnum.DEPENDENT_CANCELLATION_CANCEL.key,
                    title = "انصراف",
                    handler = FormActionHandler.SERVICE,
                    style = FormActionStyle.SECONDARY
                )
            )
        }
    }

    val step2Fields = listOf(
        FormField(
            id = "reasonId",
            label = "علت ابطال",
            type = FormFieldType.DROPDOWN,
            value = reasonId,
            required = true
        ),
        FormField(
            id = "cancellationDate",
            label = "تاریخ ابطال",
            type = FormFieldType.DATE,
            value = cancellationDate,
            required = true,
            extras = payload
        )
    )

    val step2Actions = buildList {
        add(
            FormAction(
                id = ServiceNameEnum.DEPENDENT_CANCELLATION_SUBMIT.key,
                title = "تایید و ثبت",
                handler = FormActionHandler.SERVICE,
                style = FormActionStyle.PRIMARY
            )
        )
        if (showCancelButton) {
            add(
                FormAction(
                    id = ServiceNameEnum.DEPENDENT_CANCELLATION_CANCEL.key,
                    title = "انصراف",
                    handler = FormActionHandler.SERVICE,
                    style = FormActionStyle.SECONDARY
                )
            )
        }
    }

    return FormSchema(
        key = "DEPENDENT_CANCELLATION",
        steps = listOf(
            FormStep(
                index = 1,
                title = "انتخاب فرد",
                fields = step1Fields,
                actions = step1Actions
            ),
            FormStep(
                index = 2,
                title = "علت و تاریخ",
                fields = step2Fields,
                actions = step2Actions
            ),
            FormStep(
                index = 3,
                title = "نتیجه"
            )
        ),
        currentStep = step,
        showCancelButton = showCancelButton,
        errorMessage = errorMessage,
        message = message,
        isLoading = isLoading
    )
}

enum class DependentCancellationAction(val value: String) {
    DELETE("delete")
}