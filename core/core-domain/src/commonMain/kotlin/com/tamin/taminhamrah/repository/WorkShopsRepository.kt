package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN

interface WorkShopsRepository {
    suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): EmployerAgreementListDN?

    suspend fun getPaymentSheets(
        query: ApiQueryParamDN
    ): PaymentSheetListDN?
}
