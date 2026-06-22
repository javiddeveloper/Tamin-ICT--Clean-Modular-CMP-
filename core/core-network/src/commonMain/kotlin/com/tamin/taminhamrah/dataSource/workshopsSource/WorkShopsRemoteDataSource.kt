package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO

interface WorkShopsRemoteDataSource {
    suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): ListData<EmployerAgreementDTO>?

    suspend fun getWorkshopPaymentSheets(
        query: ApiQueryParamDN
    ): ListData<PaymentSheetDTO>?

    suspend fun getWorkshopDebit(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): ListData<WorkshopDebitDTO>?
}
