package com.tamin.taminhamrah.data.repository.ai.model

import com.tamin.taminhamrah.data.remote.models.services.AiChartItem

data class FormSchema(
    val key: String,
    val steps: List<FormStep>,
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null,
    val bottomMessage: String? = null,
    val showCancelButton: Boolean = true
)

data class FormStep(
    val index: Int,
    val title: String? = null,
    val fields: List<FormField> = emptyList(),
    val actions: List<FormAction> = emptyList(),
    val message: String? = null,
    val bottomMessage: String? = null
)

data class FormField(
    val id: String,
    val label: String,
    val type: FormFieldType = FormFieldType.TEXT,
    val value: String? = null,
    val hint: String? = null,
    val required: Boolean = false,
    val enabled: Boolean = true,
    val options: List<FormOption> = emptyList(),
    val validations: List<FieldValidation> = emptyList(),
    val chartItems: List<AiChartItem> = emptyList(),
    val extras: Map<String, Any?>? = null
)

data class FormOption(
    val id: String,
    val title: String,
    val extras: Map<String, Any?>? = null
)

data class FieldValidation(
    val type: FieldValidationType,
    val value: String? = null,
    val message: String
)

data class FormAction(
    val id: String,
    val title: String,
    val handler: FormActionHandler,
    val style: FormActionStyle = FormActionStyle.PRIMARY,
    val enabled: Boolean = true,
    val actionContent: AgentActionContent? = null
)

enum class FormFieldType {
    TEXT,
    PHONE,
    NUMBER,
    OTP,
    DATE,
    TIME,
    CHART,
    DROPDOWN,
    READ_ONLY,
    HIDDEN,
    FILE_UPLOAD
}

enum class FieldValidationType {
    MIN_LENGTH,
    MAX_LENGTH,
    LENGTH,
    PATTERN,
    STARTS_WITH
}

enum class FormActionStyle {
    PRIMARY,
    SECONDARY
}

enum class FormActionHandler {
    SERVICE,
    ACTION_CONTENT
}
