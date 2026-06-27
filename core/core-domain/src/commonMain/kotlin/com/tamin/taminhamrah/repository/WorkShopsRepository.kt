package com.tamin.taminhamrah.repository

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
        query: ApiQueryParamDN
    ): EmployerAgreementListDN?

    suspend fun getPaymentSheets(
        query: ApiQueryParamDN
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
        query: ApiQueryParamDN
    ): WorkShopDebtListDN?

    suspend fun getWorkshopRecentlyAddedMembers(
        query: ApiQueryParamDN
    ): WorkshopNewMemberListDN?

    suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN
    ): WorkshopsDebtListDN?

    suspend fun getWorkshopMembers(
        query: ApiQueryParamDN
    ): WorkshopMemberListDN?

    suspend fun getWorkshopStackHolders(
        query: ApiQueryParamDN
    ): WorkshopStackHolderListDN?
}
