package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN

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
}
