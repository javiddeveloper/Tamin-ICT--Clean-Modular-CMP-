package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/** One ذینفع row. */
@Immutable
data class WorkshopStackHolderPR(
    val nationalId: String = "",
    val fullName: String = "",
    val fatherName: String = "",
    val birthDate: String = "",
    val stackType: String = "",
)
