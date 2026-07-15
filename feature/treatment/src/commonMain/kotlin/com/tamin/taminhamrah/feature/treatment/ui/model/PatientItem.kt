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
    /** Label for the patient filter: the insured reads as «خودم», dependants as «رابطه - نام». */
    val filterLabel: String
        get() = when {
            !isDependent -> "خودم"
            relation.isNullOrEmpty() -> fullName
            else -> "$relation - $fullName"
        }
}

/**
 * The people whose treatment data can be viewed: the main insured first, then dependants under 18.
 *
 * Shared by the dashboard carousel and the records patient filter so both list the same people.
 */
fun TreatmentUiState.toPatientItems(): List<PatientItem> = buildList {
    val mainUserCode = mainUserNationalCode
    if (mainUserCode != null) {
        val mainUser = deservedList.firstOrNull()
        add(
            PatientItem(
                nationalId = mainUserCode,
                fullName = mainUser?.fullName ?: selectedPatientName ?: "بیمه‌شده اصلی",
                isDependent = false,
                brhName = mainUser?.brhName,
                insuranceType = mainUser?.insuranceType
            )
        )
    }
    dependantList.forEach { dep ->
        add(
            PatientItem(
                nationalId = dep.nationalId,
                fullName = dep.fullName,
                isDependent = true,
                relation = "تحت تکفل"
            )
        )
    }
}
