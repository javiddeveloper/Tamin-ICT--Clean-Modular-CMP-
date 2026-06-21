package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO

interface WorkShopsRemoteDataSource {
    suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): ListData<EmployerAgreementDTO>?

    suspend fun getWorkshopPaymentSheets(
        query: ApiQueryParamDN
    ): ListData<PaymentSheetDTO>?
}
