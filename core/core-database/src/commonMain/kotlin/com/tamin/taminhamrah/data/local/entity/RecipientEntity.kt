package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipients")
data class RecipientEntity(
    @PrimaryKey
    val recipientCode: String,
    val recipientName: String?
)
