package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_requests")
data class UserRequestEntity(
    @PrimaryKey
    val id: Long,
    val refCode: String?,
    val title: String?,
    val comment: String?,
    val creationTime: Long?,
    val createByName: String?,
    val statusCode: String?,
    val statusDesc: String?,
    val requestTypeId: Long?,
    val requestTypeTitle: String?,
    val requestTypeDescription: String?,
    val referenceId: String?,
)
