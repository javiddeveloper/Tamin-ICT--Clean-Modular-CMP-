package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.mapper.toDetails
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.request.UserRequestRemoteDataSource
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class UserRequestRepositoryImpl(
    private val requestRemoteDataSource: UserRequestRemoteDataSource,
    private val requestDao: UserRequestDao,
    private val apiQueryBuilder: ApiQueryBuilder,
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

    override suspend fun getRequestTypes(query: ApiQueryParamDN?): List<UserRequestTypeDN> {
        val effectiveQuery = query ?: apiQueryBuilder.defaultQuery()
        val response = requestRemoteDataSource.getRequestTypes(effectiveQuery)
        return response.list.orEmpty().map { it.toDomain() }
    }

    override suspend fun getRequestErrors(requestId: Long): List<RequestErrorDN> {
        val query = ApiQueryParamDN(
            filters = listOf(
                ApiFilterDN(
                    property = FilterProperty.REQUEST_ID,
                    value = requestId.toString(),
                    operator = FilterOperator.EQ,
                )
            )
        )
        val response = requestRemoteDataSource.getRequestErrors(query)
        return response.list.orEmpty().map { it.toDomain() }
    }

    override suspend fun getSmartGuideList(params: SmartGuideSearchParams): List<SmartGuideDN> {
        // faq/limitation uses flat query params, NOT the filter JSON array used by other endpoints.
        // Correct URL: ?requestType=22&requestStatus=0018&isPublic=1&filter=[]&sort=[]
        val flatParams = buildMap<String, String> {
            params.requestType?.let { put("requestType", it.toString()) }
            params.requestStatus?.let { put("requestStatus", it) }
            params.isPublic?.let { put("isPublic", if (it) "1" else "0") }
            put("filter", "[]")
            put("sort", "[]")
        }
        val response = requestRemoteDataSource.getSmartGuideList(flatParams)
        return response.list.orEmpty().map { it.toDomain() }
    }

    override suspend fun getUserRequestDetail(id: Long): UserRequestDN {
        return requestRemoteDataSource.getUserRequestDetail(id).toDomain()
    }

    override suspend fun getShowRequestInfo(
        referenceId: String,
        requestTypeId: Long,
    ): UserRequestDetailsDN? {
        if (referenceId.isBlank()) return null
        return when (requestTypeId) {
            UserRequestTypeIds.ILL_DAY,
            UserRequestTypeIds.ORTHOTICS_PROSTHESIS,
            UserRequestTypeIds.PREGNANCY -> getShortTermDetails(referenceId, requestTypeId)
            UserRequestTypeIds.ARTICLE16 -> {
                val objectionNumber = referenceId.toLongOrNull() ?: return null
                requestRemoteDataSource.getArticle16RequestInfo(objectionNumber).toDetails()
            }
            UserRequestTypeIds.DEFERRED_INSTALLMENT ->
                requestRemoteDataSource.getDeferredInstallmentInfo(referenceId).toDetails()
            UserRequestTypeIds.FOLLOW_UP_OBJECTION ->
                requestRemoteDataSource.getFollowUpObjectionHistory(referenceId)
                    .list
                    ?.firstOrNull()
                    ?.toDetails()
            else -> null
        }
    }

    override suspend fun downloadUserRequestDocument(guid: String): String {
        return requestRemoteDataSource.downloadDocument(guid)
    }

    private suspend fun getShortTermDetails(
        referenceId: String,
        requestTypeId: Long,
    ): UserRequestDetailsDN = coroutineScope {
        val statusDeferred = async { requestRemoteDataSource.getShortTermRequestStatus(referenceId) }
        val infoDeferred = async { requestRemoteDataSource.getShortTermRequestLoadData(referenceId) }
        val status = statusDeferred.await().list?.lastOrNull()
        val info = infoDeferred.await().list?.firstOrNull()

        val pregnancyStatus = if (requestTypeId == UserRequestTypeIds.PREGNANCY) {
            requestRemoteDataSource.getPregnancyStatus().list.orEmpty()
        } else {
            emptyList()
        }
        val pregnancyTypes = if (requestTypeId == UserRequestTypeIds.PREGNANCY) {
            requestRemoteDataSource.getPregnancyTypes().list.orEmpty()
        } else {
            emptyList()
        }

        info?.toDetails(
            requestTypeId = requestTypeId,
            status = status,
            pregnancyStatus = pregnancyStatus,
            pregnancyTypes = pregnancyTypes,
        ) ?: UserRequestDetailsDN(rejectReason = status?.rejectReason)
    }

    private fun buildQuery(search: UserRequestSearchParams): ApiQueryParamDN = ApiQueryParamDN(
        filters = UserRequestFilter.buildFilters(search),
        sorts = UserRequestSort.defaultSorts(),
    )
}

