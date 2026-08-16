package com.tamin.taminhamrah.model.userRequest

import androidx.compose.runtime.Immutable

@Immutable
data class UserRequestPR(
    val id: Long,
    val refCode: String,
    val title: String,
    val comment: String,
    val creationTime: String,
    val createByName: String,
    val statusDesc: String,
    val statusCode: String,
    val requestTypeId: Long,
    val requestTypeTitle: String,
    val requestDetails: String? = null,
    val details: UserRequestDetailsPR? = null,
)
