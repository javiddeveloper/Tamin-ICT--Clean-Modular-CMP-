package com.tamin.taminhamrah.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity

/** کارگاه‌های بدون تعهدنامه (stepper step 2). */
@Entity(tableName = "workshop_without_contract_pages", primaryKeys = ["listKey", "position"])
data class WorkshopWithoutContractPageEntity(
    val listKey: String,
    val position: Int,
    val workshopId: String,
    val branchCode: String,
    val name: String,
    val nationalId: String,
    val postalCode: String,
    val tel: String,
    val address: String,
    val branchOfficeName: String,
    val branchOfficeCode: String,
)

/** ردیف‌های پیمان of one workshop + branch. */
@Entity(tableName = "workshop_contract_row_pages", primaryKeys = ["listKey", "position"])
data class WorkshopContractRowPageEntity(
    val listKey: String,
    val position: Int,
    val contractRow: String,
    val startDate: String,
    val endDate: String,
    val firstName: String,
    val lastName: String,
    val mobile: String,
    val email: String,
    val nationalCode: String,
    val tel: String,
    val postalCode: String,
    @Embedded(prefix = "workshop_") val workshop: WorkshopSummaryColumns,
)

/** The nested workshop block of a contract row, flattened into `workshop_*` columns. */
data class WorkshopSummaryColumns(
    val workshopId: String,
    val branchCode: String,
    val name: String,
    val employerName: String,
    val activityName: String,
    val address: String,
    val registerDate: String,
    val approveDate: String,
    val contractRow: String,
    val branchOfficeCode: String,
    val branchTitle: String,
    val branchOfficeName: String,
    val characterCode: String,
    val legalNationalId: String,
    val characterDescription: String,
    val workshopTypeDescription: String,
    val statusCode: String,
    val statusDescription: String,
)
