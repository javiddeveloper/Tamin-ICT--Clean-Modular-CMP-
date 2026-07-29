package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent

import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep

fun buildWeddingPresentSchema(
    step: Int,
    showCancelButton: Boolean,
    errorMessage: String? = null,
    message: String? = null,
    isLoading: Boolean = false
): FormSchema {
    return FormSchema(
        key = "WEDDING_PRESENT",
        steps = listOf(
            FormStep(index = 1, title = "استعلام اطلاعات"),
            FormStep(index = 2, title = "اعتبارسنجی عقد"),
            FormStep(index = 3, title = "محاسبه مبلغ"),
            FormStep(index = 4, title = "ثبت درخواست")
        ),
        currentStep = step,
        showCancelButton = showCancelButton,
        errorMessage = errorMessage,
        message = message,
        isLoading = isLoading
    )
}
