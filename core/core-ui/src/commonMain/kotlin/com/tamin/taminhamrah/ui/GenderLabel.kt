package com.tamin.taminhamrah.ui

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.identity_gender_female
import taminx.core.core_ui.identity_gender_male
import taminx.core.core_ui.identity_gender_unknown

/**
 * Names the gender the services report.
 *
 * They send the civil-registry code ("01"/"02"); the letter forms are accepted too, because other
 * calls in the app still use them. Anything else reads as unknown rather than being shown raw.
 *
 * Lives in core-ui because the same codes arrive on the personal, pension and registration
 * payloads, not only on the identity one. Returns a [StringResource] rather than text so the
 * mapping stays a pure function and the wording stays translatable.
 */
fun String?.toGenderLabel(): StringResource =
    when (this?.uppercase()?.trimStart('0')) {
        "M", "MALE", "1" -> Res.string.identity_gender_male
        "F", "FEMALE", "2" -> Res.string.identity_gender_female
        else -> Res.string.identity_gender_unknown
    }
