package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN

interface WorkShopsRepository {
    suspend fun getAllEmployerAgreementByNationalId(
        filters: List<ApiFilterDN> = emptyList()
    ): EmployerAgreementListDN?

    suspend fun getPaymentSheets(
        filters: List<ApiFilterDN> = emptyList()
    ): PaymentSheetListDN?

    suspend fun getWorkshopDebit(
        workshopId: String,
        branchCode: String
    ): WorkshopDebitListDN?

    suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): WorkshopDebtInquiryDN?

    suspend fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        filters: List<ApiFilterDN> = emptyList()
    ): WorkShopDebtListDN?

    suspend fun getWorkshopRecentlyAddedMembers(
        filters: List<ApiFilterDN> = emptyList()
    ): WorkshopNewMemberListDN?

    suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        filters: List<ApiFilterDN> = emptyList()
    ): WorkshopsDebtListDN?

    suspend fun getWorkshopMembers(
        filters: List<ApiFilterDN> = emptyList()
    ): WorkshopMemberListDN?

    suspend fun getWorkshopStackHolders(
        filters: List<ApiFilterDN> = emptyList()
    ): WorkshopStackHolderListDN?
}
