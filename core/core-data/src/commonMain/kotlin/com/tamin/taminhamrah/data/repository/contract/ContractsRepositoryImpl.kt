package com.tamin.taminhamrah.data.repository.contract

import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.contracts.ContractsRemoteDataSource
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ContractsRepositoryImpl(
    private val contractsRemoteDataSource: ContractsRemoteDataSource,
    private val contractDao: ContractDao,
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
}
