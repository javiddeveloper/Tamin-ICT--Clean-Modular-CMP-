package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "version_history")
data class VersionHistoryEntity(
    @PrimaryKey
    val versionCode: Int,
    val versionName: String,
    val releaseDate: String,
    val isLatest: Boolean,
    val newFeaturesRaw: String,
    val debugRaw: String
)
