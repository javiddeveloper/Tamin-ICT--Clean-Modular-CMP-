package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_job_infos")
data class HistoryJobInfoEntity(
    @PrimaryKey val id: Int,
    val risuid: String,
    val rwshName: String,
    val brhcode: String,
    val jobDesc: String,
    val startDate: String,
    val rwshId: String
)
