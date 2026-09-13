package com.tamin.taminhamrah.model.constructionInsurance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConstructionFileDTO(
    @SerialName("fileNumber") val fileNumber: Long? = null,
    @SerialName("requestNumber") val requestNumber: Long? = null,
    @SerialName("requestDate") val requestDate: String? = null,
    @SerialName("workshopId") val workshopInfo: WorkshopIdInfoDTO? = null,
    @SerialName("postalCode") val postalCode: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("mainPelak") val mainPlaque: Int? = null,
    @SerialName("subPelak") val subPlaque: Int? = null,
    @SerialName("block") val block: Long? = null,
    @SerialName("estate") val propertyConstruction: Int? = null,
    @SerialName("apartment") val apartment: Int? = null,
    @SerialName("trade") val trade: Int? = null,
    @SerialName("partPelak") val partPlaque: Int? = null,
    @SerialName("sumOfComplications") val sumOfComplications: Long? = null,
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("totalPayment") val totalPayment: Long? = null,
    @SerialName("metrage") val meterage: Int? = null,
    @SerialName("debitStatusCode") val debitStatusCode: String? = null,
    @SerialName("buildingProminence") val protrusion: Long? = null,
    @SerialName("postulate") val applicationFees: Long? = null,
    @SerialName("fondation") val residentialServiceInfrastructureFees: Long? = null,
    @SerialName("densityFinance") val excessDensitySurchargeFees: Long? = null,
    @SerialName("optimalCharges") val increasePropertyValue: Long? = null,
    @SerialName("fencesCharges") val issuanceFencingWallConstructionFees: Long? = null,
    @SerialName("thirdItemCharges") val coveredClause3Fees: Long? = null,
    @SerialName("article100") val article100: Long? = null,
    @SerialName("paymentDeadLine") val paymentDeadLine: String? = null
)

@Serializable
data class WorkshopIdInfoDTO(
    @SerialName("workshopRegisterDate") val workshopRegisterDate: String? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("brhCode") val brhCode: String? = null,
    @SerialName("branchCode") val branchCode: String? = null
)
