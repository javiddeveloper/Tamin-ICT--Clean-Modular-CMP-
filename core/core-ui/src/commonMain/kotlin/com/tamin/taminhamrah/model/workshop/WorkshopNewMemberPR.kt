package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class WorkshopNewMemberPR(
    val id: Long?,
    val dateOfStart: Long?,
    val insuranceId: String?,
    val relationWithTamin: Int?,
    val organizationId: String?,
    val workshopId: String?,
    val job: String?
)
