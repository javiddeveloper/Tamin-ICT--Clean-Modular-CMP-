package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN

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
        branchCode: String
    ): WorkshopDebitListDN? {
        val queryParam = ApiQueryParamDN()
        val response = remoteDataSource.getWorkshopDebit(workshopId, branchCode, queryParam)
        return response?.let {
            WorkshopDebitListDN(
                list = it.list?.map { item -> item.toDomain() },
                total = it.total
            )
        }
    }

    override suspend fun getWorkshopDebtInquiry(
        workshopId: String,
        branchCode: String
    ): WorkshopDebtInquiryDN? {
        val response = remoteDataSource.getWorkshopDebtInquiry(workshopId, branchCode)
        return response?.toDomain()
    }

    override suspend fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        query: ApiQueryParamDN
    ): WorkShopDebtListDN? {
        val response = remoteDataSource.getWorkshopObjectionableDebitList(workshopNumber, branchCode, query)
        return response?.let {
            WorkShopDebtListDN(list = it.list?.map { item -> item.toDomain() }, total = it.total)
        }
    }

    override suspend fun getWorkshopRecentlyAddedMembers(
        query: ApiQueryParamDN
    ): WorkshopNewMemberListDN? {
        val response = remoteDataSource.getWorkshopRecentlyAddedMembers(query)
        return response?.let {
            WorkshopNewMemberListDN(list = it.list?.map { item -> item.toDomain() }, total = it.total)
        }
    }

    override suspend fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN
    ): WorkshopsDebtListDN? {
        val response = remoteDataSource.getWorkshopsDebtsList(workshopId, branchId, query)
        return response?.let {
            WorkshopsDebtListDN(list = it.list?.map { item -> item.toDomain() }, total = it.total)
        }
    }

    override suspend fun getWorkshopMembers(
        query: ApiQueryParamDN
    ): WorkshopMemberListDN? {
        val response = remoteDataSource.getWorkshopMembers(query)
        return response?.let {
            WorkshopMemberListDN(list = it.list?.map { item -> item.toDomain() }, total = it.total)
        }
    }

    override suspend fun getWorkshopStackHolders(
        query: ApiQueryParamDN
    ): WorkshopStackHolderListDN? {
        val response = remoteDataSource.getWorkshopStackHolders(query)
        return response?.let {
            WorkshopStackHolderListDN(list = it.list?.map { item -> item.toDomain() }, total = it.total)
        }
    }
}
