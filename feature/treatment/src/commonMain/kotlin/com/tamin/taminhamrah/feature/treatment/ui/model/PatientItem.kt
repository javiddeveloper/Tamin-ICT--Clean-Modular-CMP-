package com.tamin.taminhamrah.feature.treatment.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState

@Immutable
data class PatientItem(
    val nationalId: String,
    val fullName: String,
    val isDependent: Boolean,
    val relation: String? = null,
    val brhName: String? = null,
    val insuranceType: String? = null
) {
    /** Label for the records patient filter: the insured reads as «خودم», dependants as «رابطه - نام». */
    val filterLabel: String
        get() = when {
            !isDependent -> "خودم"
            relation.isNullOrEmpty() -> fullName
            else -> "$relation - $fullName"
        }
}

private const val FALLBACK_MAIN_INSURED_NAME = "بیمه‌شده اصلی"
private const val DEPENDANT_RELATION = "تحت تکفل"

/**
 * Flattens the treatment state into the carousel's display order: the main insured
 * person first, then dependants under 18. Returns an empty list until the main national
 * code arrives, which the screen surfaces as its empty state.
 */
fun TreatmentUiState.toPatientList(): List<PatientItem> = buildList {
    mainUserNationalCode?.let { nationalCode ->
        val mainRecord = deservedList.firstOrNull()
        add(
            PatientItem(
                nationalId = nationalCode,
                fullName = mainRecord?.fullName
                    ?: selectedPatientName
                    ?: FALLBACK_MAIN_INSURED_NAME,
                isDependent = false,
                brhName = mainRecord?.brhName,
                insuranceType = mainRecord?.insuranceType,
            ),
        )
    }
    dependantList.forEach { dependant ->
        add(
            PatientItem(
                nationalId = dependant.nationalId,
                fullName = dependant.fullName,
                isDependent = true,
                relation = DEPENDANT_RELATION,
            ),
        )
    }
}
