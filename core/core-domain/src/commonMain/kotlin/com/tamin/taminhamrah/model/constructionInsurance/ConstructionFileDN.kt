package com.tamin.taminhamrah.model.constructionInsurance

data class ConstructionFileDN(
    val fileNumber: Long?,
    val requestNumber: Long?,
    val requestDate: String?,
    val workshopInfo: WorkshopIdInfoDN?,
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
    val paymentDeadLine: String?,
)

data class WorkshopIdInfoDN(
    val workshopRegisterDate: String?,
    val workshopId: String?,
    val brhCode: String?,
)

data class ConstructionFileSearchParamsDN(
    val fileNo: String?,
    val reqNo: String?,
    val workshopId: String?,
    val branchCode: String?,
)
