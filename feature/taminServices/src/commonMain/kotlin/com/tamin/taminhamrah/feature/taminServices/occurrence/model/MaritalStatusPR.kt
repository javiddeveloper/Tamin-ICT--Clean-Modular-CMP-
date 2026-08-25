package com.tamin.taminhamrah.feature.taminServices.occurrence.model

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_field_marital_married
import taminx.core.core_ui.occurrence_field_marital_single

enum class MaritalStatusPR(
    val code: String,
    val displayNameRes: StringResource
) {
    SINGLE("0", Res.string.occurrence_field_marital_single),
    MARRIED("1", Res.string.occurrence_field_marital_married);

    companion object {
        fun fromCode(code: String?): MaritalStatusPR? =
            entries.find { it.code == code }
    }
}
