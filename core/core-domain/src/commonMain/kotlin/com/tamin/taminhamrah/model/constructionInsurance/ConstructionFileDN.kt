package com.tamin.taminhamrah.model.constructionInsurance

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

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

/**
 * Same EQ filters the non-paged construction-file lookup builds internally — shared here so the
 * paged list use case (and its `Paginator` base query) doesn't redo the mapping.
 */
fun ConstructionFileSearchParamsDN?.toApiQueryParam(): ApiQueryParamDN {
    val filters = mutableListOf<ApiFilterDN>()
    this?.fileNo?.takeIf { it.isNotBlank() }?.let {
        filters.add(ApiFilterDN(FilterProperty.FILE_NO, it, FilterOperator.EQ))
    }
    this?.reqNo?.takeIf { it.isNotBlank() }?.let {
        filters.add(ApiFilterDN(FilterProperty.REQ_NO, it, FilterOperator.EQ))
    }
    this?.workshopId?.takeIf { it.isNotBlank() }?.let {
        filters.add(ApiFilterDN(FilterProperty.WORKSHOP_ID, it, FilterOperator.EQ))
    }
    this?.branchCode?.takeIf { it.isNotBlank() }?.let {
        filters.add(ApiFilterDN(FilterProperty.WORKSHOP_BRANCH_CODE, it, FilterOperator.EQ))
    }
    return ApiQueryParamDN(filters = filters)
}
