package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/** One کارکنان row. */
@Immutable
data class WorkshopMemberPR(
    val insuranceNumber: String = "",
    val fullName: String = "",
    val nationalId: String = "",
    val idCardNumber: String = "",
    val fatherName: String = "",
    val nationality: String = "",
    val relationType: String = "",
    val leavingWorkStatus: String = "",
    val leavingWorkDate: String = "",
)
