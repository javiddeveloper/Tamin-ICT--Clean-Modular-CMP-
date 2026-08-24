package com.tamin.taminhamrah.feature.taminServices.occurrence.model

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.identity_gender_female
import taminx.core.core_ui.identity_gender_male

enum class GenderPR(
    val code: String,
    val displayNameRes: StringResource,
    /** The legacy "occurence" submit endpoint's own gender scale (1 = male, 2 = female). */
    val legacyCode: Int,
) {
    MALE("01", Res.string.identity_gender_male, 1),
    FEMALE("02", Res.string.identity_gender_female, 2);

    companion object {
        fun fromCode(code: String?): GenderPR? =
            entries.find { it.code == code }
    }
}
