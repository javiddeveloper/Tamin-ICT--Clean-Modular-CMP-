package com.tamin.taminhamrah.ui

import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.pension_status_type_retirement

/**
 * Names the pensioner-type code the services report.
 *
 * They send a numeric code (`"101"` = retirement). Prefer the server's own `pensionerTypeDesc`
 * when it is present; this mapping is only for the code, and returns null for anything else so
 * the raw value (or a dash) can be shown instead of invented copy.
 *
 * Returns a [StringResource] rather than text so the mapping stays a pure function and the
 * wording stays in compose resources — same pattern as [toGenderLabel].
 */
fun String?.toPensionerTypeLabel(): StringResource? =
    when (this?.trim()) {
        RETIREMENT_PENSIONER_TYPE_CODE -> Res.string.pension_status_type_retirement
        else -> null
    }

internal const val RETIREMENT_PENSIONER_TYPE_CODE = "101"
