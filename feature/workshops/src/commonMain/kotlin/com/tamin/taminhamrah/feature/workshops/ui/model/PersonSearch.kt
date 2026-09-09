package com.tamin.taminhamrah.feature.workshops.ui.model

import androidx.compose.runtime.Immutable

/**
 * The two fields the کارکنان and ذینفعان searches share.
 *
 * Each value goes to the property that names it — the old stakeholder screen crossed them over and
 * filtered on the wrong column.
 *
 * It sits here rather than in either screen's contract because the panel that collects it is a
 * shared component: leaving it in کارکنان's contract made the shared component depend on one
 * particular screen, which is backwards.
 */
@Immutable
data class PersonSearch(
    val nationalId: String = "",
    val insuranceNumber: String = "",
) {
    val isNotEmpty: Boolean get() = nationalId.isNotBlank() || insuranceNumber.isNotBlank()
}
