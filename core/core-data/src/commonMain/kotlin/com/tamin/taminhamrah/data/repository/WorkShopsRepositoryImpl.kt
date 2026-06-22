package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN

class WorkShopsRepositoryImpl(
    private val remoteDataSource: WorkShopsRemoteDataSource
) : WorkShopsRepository {

    override suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): EmployerAgreementListDN? {
        val response = remoteDataSource.getAllEmployerAgreementByNationalId(query)
        return response?.let {
            EmployerAgreementListDN(
                list = it.list?.map { item -> item.toDomain() },
                total = it.total
            )
        }
    }

    override suspend fun getPaymentSheets(
        query: ApiQueryParamDN
    ): PaymentSheetListDN? {
        val response = remoteDataSource.getWorkshopPaymentSheets(query)
        return response?.let {
            PaymentSheetListDN(
                list = it.list?.map { item -> item.toDomain() },
                total = it.total
            )
        }
    }

    override suspend fun getWorkshopDebit(
        workshopId: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): WorkshopDebitListDN? {
        val response = remoteDataSource.getWorkshopDebit(workshopId, branchCode, query)
        return response?.let {
            WorkshopDebitListDN(
                list = it.list?.map { item -> item.toDomain() },
                total = it.total
            )
        }
    }
}
