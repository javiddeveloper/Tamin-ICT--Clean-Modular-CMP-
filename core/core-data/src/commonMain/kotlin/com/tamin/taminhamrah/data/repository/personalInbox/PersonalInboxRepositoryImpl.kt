package com.tamin.taminhamrah.data.repository.personalInbox

import com.tamin.taminhamrah.data.local.dao.PersonalInboxDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.inbox.PersonalInboxRemoteDataSource
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class PersonalInboxRepositoryImpl(
    private val personalInboxRemoteDataSource: PersonalInboxRemoteDataSource,
    private val personalInboxDao: PersonalInboxDao,
    private val apiQueryBuilder: ApiQueryBuilder,
) : PersonalInboxRepository {

    override fun getInboxItems(query: ApiQueryParamDN?): Flow<List<PersonalInboxItemDN>> = flow {
        val effectiveQuery = query ?: apiQueryBuilder.defaultQuery()
        val localItems = personalInboxDao.getInboxItems().first()
        emit(localItems.map { it.toDomain() })

        try {
            val response = personalInboxRemoteDataSource.getInboxItems(effectiveQuery)
            val remoteItems = response.list.orEmpty()
            personalInboxDao.replaceAllInboxItems(remoteItems.map { it.toEntity() })
        } catch (e: Exception) {
            if (localItems.isEmpty()) {
                throw e
            }
        }

        emitAll(
            personalInboxDao.getInboxItems().map { entities ->
                entities.map { it.toDomain() }
            }
        )
    }.distinctUntilChanged()

    override fun getInboxSize(): Flow<PersonalInboxSizeDN> = flow {
        val cached = personalInboxDao.getInboxSize().firstOrNull()?.toDomain()
        cached?.let { emit(it) }

        try {
            val remoteData = personalInboxRemoteDataSource.getInboxSize()
            personalInboxDao.upsertInboxSize(remoteData.toEntity())
        } catch (e: Exception) {
            if (cached == null) {
                throw e
            }
        }

        personalInboxDao.getInboxSize().firstOrNull()?.toDomain()?.let { emit(it) }
    }.distinctUntilChanged()

    override fun getMyRequestPDF(requestId: String): Flow<PersonalInboxItemDN> = flow {
        emit(personalInboxRemoteDataSource.getMyRequestPDF(requestId).toDomain())
    }

    override fun deleteMyRequest(requestId: String): Flow<Unit> = flow {
        personalInboxRemoteDataSource.deleteMyRequest(requestId)
        emit(Unit)
    }

    override fun inboxInquiryLicense(
        requestId: String,
        operation: String,
        duration: String?
    ): Flow<Unit> = flow {
        personalInboxRemoteDataSource.inboxInquiryLicense(requestId, operation, duration)
        emit(Unit)
    }
}
