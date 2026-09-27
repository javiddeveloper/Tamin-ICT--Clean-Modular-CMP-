package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity

/** بازرسی‌های انجام‌شده — also holds the workshop-inspection list under its own listKey scope. */
@Entity(tableName = "inspection_performed_pages", primaryKeys = ["listKey", "position"])
data class InspectionPerformedPageEntity(
    val listKey: String,
    val position: Int,
    val activityDesc: String,
    val branchCode: String,
    val branchdesc: String,
    val inspectionDate: Long,
    val inspectionNo: String,
    val insuranceNo: String,
    val objectable: String,
    val relationType: String,
    val workshopName: String,
    val workshopNo: String,
    val nationalCode: String,
)

/** Branch picker of the inspection request. */
@Entity(tableName = "inspection_branch_pages", primaryKeys = ["listKey", "position"])
data class InspectionBranchPageEntity(
    val listKey: String,
    val position: Int,
    val operation: String,
    val code: String,
    val name: String,
    val minCode: String,
    val maxCode: String,
    val type: String,
    val branchAddress: String,
    val cityCode: String,
    val status: String,
)

/** Job-title picker, shared by inspection and workshop inspection. */
@Entity(tableName = "inspection_job_pages", primaryKeys = ["listKey", "position"])
data class InspectionJobPageEntity(
    val listKey: String,
    val position: Int,
    val operation: String,
    val jobCode: String,
    val jobDescription: String,
    val status: String,
    val statusDate: String,
)
