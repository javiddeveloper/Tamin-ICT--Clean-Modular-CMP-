package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSource
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class UserRequestRepositoryImpl(
    private val requestRemoteDataSource: UserRequestRemoteDataSource,
    private val requestDao: UserRequestDao,
) : UserRequestRepository {

    override fun getUserRequests(filters: List<ApiFilterDN>): Flow<List<UserRequestDN>> = flow {
        val localRequests = requestDao.getUserRequests().first()
        emit(localRequests.map { it.toDomain() })

        try {
            val response = requestRemoteDataSource.getUserRequests(buildQuery(filters))
            val remoteRequests = response.list.orEmpty()
            requestDao.replaceAll(remoteRequests.map { it.toEntity() })
        } catch (e: Exception) {
            if (localRequests.isEmpty()) {
                throw e
            }
        }

        emitAll(
            requestDao.getUserRequests().map { entities ->
                entities.map { it.toDomain() }
            }
        )
    }.distinctUntilChanged()

    private fun buildQuery(filters: List<ApiFilterDN>): ApiQueryParamDN = ApiQueryParamDN(
        filters = filters.ifEmpty { UserRequestFilter.defaultFilters() },
        sorts = UserRequestSort.defaultSorts(),
    )
}
