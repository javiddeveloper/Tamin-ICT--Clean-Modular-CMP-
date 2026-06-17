package com.tamin.taminhamrah.data.repository.request

import com.tamin.taminhamrah.data.local.dao.RequestDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.request.RequestRemoteDataSource
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.RequestDN
import com.tamin.taminhamrah.repository.request.RequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class RequestRepositoryImpl(
    private val requestRemoteDataSource: RequestRemoteDataSource,
    private val requestDao: RequestDao,
) : RequestRepository {

    override fun getMyRequests(query: ApiQueryParamDN): Flow<List<RequestDN>> = flow {
        val localRequests = requestDao.getRequests().first()
        emit(localRequests.map { it.toDomain() })

        try {
            val response = requestRemoteDataSource.getRequests(query)
            val remoteRequests = response.list.orEmpty()
            requestDao.replaceAll(remoteRequests.map { it.toEntity() })
        } catch (e: Exception) {
            if (localRequests.isEmpty()) {
                throw e
            }
        }

        emitAll(
            requestDao.getRequests().map { entities ->
                entities.map { it.toDomain() }
            }
        )
    }.distinctUntilChanged()
}
