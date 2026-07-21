package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class WorkshopMemberPR(
    val leavingWorkStatus: String?,
    val leavingWorkDate: String?,
    val specialSubType: String?
)
