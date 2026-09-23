package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "construction_files")
data class ConstructionFileEntity(
    @PrimaryKey val fileNumber: Long,
    val requestNumber: Long?,
    val requestDate: String?,
    val workshopId: String?,
    val workshopRegisterDate: String?,
    val brhCode: String?,
    val postalCode: String?,
    val address: String?,
    val mainPlaque: Int?,
    val subPlaque: Int?,
    val block: Long?,
    val propertyConstruction: Int?,
    val apartment: Int?,
    val trade: Int?,
    val partPlaque: Int?,
    val sumOfComplications: Long?,
    val debitNumber: String?,
    val totalPayment: Long?,
    val meterage: Int?,
    val debitStatusCode: String?,
    val protrusion: Long?,
    val applicationFees: Long?,
    val residentialServiceInfrastructureFees: Long?,
    val excessDensitySurchargeFees: Long?,
    val increasePropertyValue: Long?,
    val issuanceFencingWallConstructionFees: Long?,
    val coveredClause3Fees: Long?,
    val article100: Long?,
    val paymentDeadLine: String?
)
