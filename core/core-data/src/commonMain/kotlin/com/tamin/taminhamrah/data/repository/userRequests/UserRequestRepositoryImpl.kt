package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSource
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.model.request.UserRequestTypeDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
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

    override fun getUserRequests(search: UserRequestSearchParams): Flow<List<UserRequestDN>> = flow {
        val localRequests = requestDao.getUserRequests().first()
        emit(localRequests.map { it.toDomain() })

        try {
            val response = requestRemoteDataSource.getUserRequests(buildQuery(search))
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

    override suspend fun getRequestTypes(
        page: Int,
        start: Int,
        limit: Int,
    ): List<UserRequestTypeDN> {
        val response = requestRemoteDataSource.getRequestTypes(
            ApiQueryParamDN(page = page, start = start, limit = limit)
        )
        return response.list.orEmpty().map { it.toDomain() }
    }

    private fun buildQuery(search: UserRequestSearchParams): ApiQueryParamDN = ApiQueryParamDN(
        filters = UserRequestFilter.buildFilters(search),
        sorts = UserRequestSort.defaultSorts(),
    )
}
