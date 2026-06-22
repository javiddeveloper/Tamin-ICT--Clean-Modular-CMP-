package com.tamin.taminhamrah.data.repository.contract

import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.local.dao.RegistrationInfoDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSource
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class ContractsRepositoryImpl(
    private val contractsRemoteDataSource: ContractsRemoteDataSource,
    private val contractDao: ContractDao,
    private val registrationInfoDao: RegistrationInfoDao,
    private val apiQueryBuilder: ApiQueryBuilder,
) :
    ContractsRepository {
    override fun getContracts(query: ApiQueryParamDN?): Flow<List<ContractDN>> =
        flow {
            val effectiveQuery = query ?: apiQueryBuilder.defaultQuery()
            val localItems = contractDao.getContracts().first()
            emit(localItems.map { it.toDomain() })

            try {
                val response = contractsRemoteDataSource.getContracts(effectiveQuery)
                val remoteItems = response.list.orEmpty()
                contractDao.replaceAll(remoteItems.map { it.toEntity() })
            } catch (e: Exception) {
                if (localItems.isEmpty()) {
                    throw e
                }
            }

            emitAll(
                contractDao.getContracts().map { entities ->
                    entities.map { it.toDomain() }
                }
            )
        }.distinctUntilChanged()

    override fun getStudentInsuranceContracts(): Flow<List<ContractDN>> =
        getContracts(buildStudentInsuranceContractsQuery())

    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> = flow {
        val localInfo = registrationInfoDao.getRegistrationInfo().first()
        localInfo?.toDomain()?.let { emit(it) }

        try {
            val response = contractsRemoteDataSource.getRegistrationInfo()
            registrationInfoDao.upsertRegistrationInfo(response.toDomain().toEntity())
        } catch (e: Exception) {
            if (localInfo == null) {
                throw e
            }
        }
        emitAll(
            registrationInfoDao.getRegistrationInfo()
                .mapNotNull { it?.toDomain() }
        )
    }.distinctUntilChanged()

    private fun buildStudentInsuranceContractsQuery(): ApiQueryParamDN = ApiQueryParamDN(
        page = 1,
        start = 0,
        limit = 100,
        filters = listOf(
            ApiFilterDN(
                property = FilterProperty.PREMIUM_TYPE_CODE,
                operator = FilterOperator.EQ,
                value = FREELANCE_PREMIUM_TYPE_CODE,
            ),
        ),
    )

    private companion object {
        /**
         * Legacy Tamin Hamrah student insurance flow queries list-contracts-mobile
         * with premiumTypeCode "01" (حرف و مشاغل آزاد). Step-2 eligibilityStatus comes from this response.
         */
        const val FREELANCE_PREMIUM_TYPE_CODE = "01"
    }
}
