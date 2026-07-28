package com.tamin.taminhamrah.feature.treatment.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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
 *
 * Deliberately reads no selection state: the list is who there is, not who is in view, so
 * swiping the carousel cannot invalidate it and rebuild the hub.
 */
fun TreatmentUiState.toPatientList(): ImmutableList<PatientItem> = buildList {
    mainUserNationalCode?.let { nationalCode ->
        val mainRecord = deservedList.firstOrNull()
        add(
            PatientItem(
                nationalId = nationalCode,
                fullName = mainRecord?.fullName ?: FALLBACK_MAIN_INSURED_NAME,
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
}.toImmutableList()

/**
 * One insurance card, ready to draw: who it belongs to, their entitlement, and their position
 * among the dependants (which picks the card's color).
 *
 * Resolved once for the whole carousel rather than per page, so a swipe neither re-runs the
 * entitlement rule nor rescans the list for the color ordinal.
 */
@Immutable
data class PatientCardItem(
    val patient: PatientItem,
    val coverage: CoverageStatus,
    val dependantOrdinal: Int,
)

fun List<PatientItem>.toCardItems(
    deservedList: List<DeservedTreatmentPR>,
): ImmutableList<PatientCardItem> {
    var dependants = 0
    return map { patient ->
        PatientCardItem(
            patient = patient,
            coverage = coverageStatusOf(patient, deservedList),
            // Position among dependants only, so each keeps its own color whether or not
            // a main insured person is present.
            dependantOrdinal = if (patient.isDependent) dependants++ else 0,
        )
    }.toImmutableList()
}
