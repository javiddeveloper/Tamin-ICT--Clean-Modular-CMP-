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
    /**
     * Whether this person is still on the workshop's books.
     *
     * Decided from the leaving date rather than by matching the status wording, which is free
     * text the service composes and can reword without notice.
     */
    val isEmployed: Boolean = false,
)
