package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity

@Entity(tableName = "job_title_pages", primaryKeys = ["listKey", "position"])
data class JobTitlePageEntity(
    val listKey: String,
    val position: Int,
    val jobCode: String,
    val jobDescription: String,
    val status: String,
    val statusDate: String,
)
