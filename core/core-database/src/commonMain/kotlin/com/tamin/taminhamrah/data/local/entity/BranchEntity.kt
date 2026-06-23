package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")
data class BranchEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val branchAddress: String,
    val cityCode: String,
    val minCode: String?,
    val maxCode: String?,
)
