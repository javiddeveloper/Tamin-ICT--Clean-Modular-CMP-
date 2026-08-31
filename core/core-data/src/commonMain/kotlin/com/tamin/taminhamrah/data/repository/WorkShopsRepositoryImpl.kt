package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeContractListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeListDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toDto
import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkShopsRepositoryImpl(
    private val remoteDataSource: WorkShopsRemoteDataSource,
    private val queryBuilder: ApiQueryBuilder
) : WorkShopsRepository {

    override suspend fun getAllEmployerAgreementByNationalId(
        filters: List<ApiFilterDN>
    ): EmployerAgreementListDN? {
        val query = queryBuilder.defaultQuery().copy(page = 1, limit = 100, filters = filters)
        val response = remoteDataSource.getAllEmployerAgreementByNationalId(query)
        return response?.let {
            EmployerAgreementListDN(
                list = it.list?.map { item -> item.toDomain() },
                total = it.total
            )
        }
    }

    override suspend fun getPaymentSheets(
        filters: List<ApiFilterDN>
    ): PaymentSheetListDN? {
        val query = queryBuilder.defaultQuery().copy(page = 1, limit = 100, filters = filters)
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
        val queryParam = queryBuilder.defaultQuery()
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

    override fun getWorkshopObjectionableDebitList(
        workshopNumber: String,
        branchCode: String,
        filters: List<ApiFilterDN>
    ): Flow<WorkShopDebtListDN?> = flow {
        val response = remoteDataSource.getWorkshopObjectionableDebitList(
            workshopNumber, branchCode, ApiQueryParamDN(filters = filters)
        )
        emit(
            response?.let {
                WorkShopDebtListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getWorkshopRecentlyAddedMembers(
        filters: List<ApiFilterDN>
    ): Flow<WorkshopNewMemberListDN?> = flow {
        val response = remoteDataSource.getWorkshopRecentlyAddedMembers(ApiQueryParamDN(filters = filters))
        emit(
            response?.let {
                WorkshopNewMemberListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getWorkshopsDebtsList(
        workshopId: String,
        branchId: String,
        filters: List<ApiFilterDN>
    ): Flow<WorkshopsDebtListDN?> = flow {
        val response = remoteDataSource.getWorkshopsDebtsList(workshopId, branchId, ApiQueryParamDN(filters = filters))
        emit(
            response?.let {
                WorkshopsDebtListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getWorkshopMembers(
        filters: List<ApiFilterDN>
    ): Flow<WorkshopMemberListDN?> = flow {
        val response = remoteDataSource.getWorkshopMembers(ApiQueryParamDN(filters = filters))
        emit(
            response?.let {
                WorkshopMemberListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getWorkshopStackHolders(
        filters: List<ApiFilterDN>
    ): Flow<WorkshopStackHolderListDN?> = flow {
        val response = remoteDataSource.getWorkshopStackHolders(ApiQueryParamDN(filters = filters))
        emit(
            response?.let {
                WorkshopStackHolderListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getLegalRepresentativeWorkshops(): Flow<LegalRepresentativeWorkshopListDN?> = flow {
        val response = remoteDataSource.getLegalRepresentativeWorkshops()
        emit(
            response?.let {
                LegalRepresentativeWorkshopListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getLegalRepresentatives(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeListDN?> = flow {
        val response = remoteDataSource.getLegalRepresentatives(workshopId, branchCode)
        emit(
            response?.let {
                LegalRepresentativeListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override fun getLegalRepresentativeWorkshopContracts(
        workshopId: String,
        branchCode: String
    ): Flow<LegalRepresentativeContractListDN?> = flow {
        val response = remoteDataSource.getLegalRepresentativeWorkshopContracts(workshopId, branchCode)
        emit(
            response?.let {
                LegalRepresentativeContractListDN(
                    list = it.list?.map { item -> item.toDomain() } ?: emptyList(),
                    total = it.total ?: 0
                )
            }
        )
    }

    override suspend fun requestLegalRepresentativeTicket(nationalCode: String?) {
        remoteDataSource.requestLegalRepresentativeTicket(nationalCode)
    }

    override suspend fun verifyLegalRepresentativeTicket(ticket: String) {
        remoteDataSource.verifyLegalRepresentativeTicket(ticket)
    }

    override suspend fun submitLegalRepresentative(ticket: String, request: LegalRepresentativeRequestDN) {
        remoteDataSource.submitLegalRepresentative(ticket, request.toDto(ticket))
    }

    override suspend fun deleteLegalRepresentative(ticket: String, stakeId: Long) {
        remoteDataSource.deleteLegalRepresentative(ticket, stakeId)
    }
}
