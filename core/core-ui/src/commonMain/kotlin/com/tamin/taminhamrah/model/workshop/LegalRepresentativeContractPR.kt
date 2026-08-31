package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/** One contract (پیمان) belonging to a "special" (پیمانکاری) workshop. */
@Immutable
data class LegalRepresentativeContractPR(
    val contractRow: String,
    val title: String?,
)
